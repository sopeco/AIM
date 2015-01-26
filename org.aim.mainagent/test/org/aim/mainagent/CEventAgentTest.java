/**
 * Copyright 2014 SAP AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aim.mainagent;

import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import junit.framework.Assert;

import org.aim.api.exceptions.InstrumentationException;
import org.aim.api.exceptions.MeasurementException;
import org.aim.api.measurement.MeasurementData;
import org.aim.api.measurement.collector.AbstractDataSource;
import org.aim.api.measurement.collector.CollectorFactory;
import org.aim.api.measurement.collector.IDataCollector;
import org.aim.artifacts.events.probes.MonitorWaitingTimeProbe;
import org.aim.artifacts.measurement.collector.MemoryDataSource;
import org.aim.artifacts.records.EventTimeStampRecord;
import org.aim.description.InstrumentationDescription;
import org.aim.description.builder.InstrumentationDescriptionBuilder;
import org.aim.mainagent.sampling.Sampling;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lpe.common.config.GlobalConfiguration;
import org.lpe.common.extension.ExtensionRegistry;

public class CEventAgentTest {

	private DummyJVMTIFacade jvmti;
	private IDataCollector collector;
	
	private static final Thread TEST_THREAD = new Thread();

	@BeforeClass
	public static void prepareCollector() {
		AbstractDataSource dataSource = CollectorFactory.createDataSource(MemoryDataSource.class.getName(), null);
		AbstractDataSource.setDefaultDataSource(dataSource);

		Properties globalProperties = new Properties();
		String currentDir = System.getProperty("user.dir");
		globalProperties.setProperty(ExtensionRegistry.APP_ROOT_DIR_PROPERTY_KEY, currentDir);
		globalProperties.setProperty(ExtensionRegistry.PLUGINS_FOLDER_PROPERTY_KEY, "plugins");
		GlobalConfiguration.initialize(globalProperties);
	}

	@Before
	public void instrument() throws InstrumentationException, NoSuchMethodException, SecurityException,
			NoSuchFieldException, IllegalArgumentException, IllegalAccessException, MeasurementException {
		jvmti = new DummyJVMTIFacade();

		Field monitorEventListenerField = CEventAgentAdapter.class.getDeclaredField("initialized");
		monitorEventListenerField.setAccessible(true);
		monitorEventListenerField.setBoolean(null, true);

		Field activatedField = CEventAgentAdapter.class.getDeclaredField("activated");
		activatedField.setAccessible(true);
		activatedField.setBoolean(null, true);

		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		idBuilder.newSynchronizedScopeEntity().addProbe(MonitorWaitingTimeProbe.MODEL_PROBE).entityDone();
		InstrumentationDescription desc = idBuilder.build();

		AdaptiveInstrumentationFacade.getInstance().instrument(desc);

		collector = AbstractDataSource.getDefaultDataSource();

		collector.enable();
		Sampling.getInstance().start();
	}

	@Test
	public void monitorEventsTest() throws IllegalAccessException, InvocationTargetException, MeasurementException,
			NoSuchFieldException, SecurityException, NoSuchMethodException, InstrumentationException,
			InterruptedException {
		Thread thread = new Thread();
		jvmti.newMonitorWaitEvent(thread, this, 1000);
		MeasurementData data = getData();
		// this package should be excluded
		Assert.assertTrue(data.getRecords().isEmpty());

		jvmti.newMonitorWaitEvent(thread, getClass(), 1000);
		data = getData();
		Assert.assertEquals(1, data.getRecords(EventTimeStampRecord.class).size());

		jvmti.newMonitorEnteredEvent(thread, getClass(), 200);
		data = getData();
		Assert.assertEquals(2, data.getRecords(EventTimeStampRecord.class).size());

		for (int i = 0; i < 100; i++) {
			jvmti.newMonitorWaitEvent(TEST_THREAD, this, i);
			jvmti.newMonitorEnteredEvent(thread, getClass(), i);
		}

		Thread.sleep(1000);

		data = getData();
		Assert.assertEquals(102, data.getRecords(EventTimeStampRecord.class).size());

		jvmti.newMonitorEnteredEvent(TEST_THREAD, null, -1);
		jvmti.newMonitorWaitEvent(TEST_THREAD, null, -1);
		data = getData();
		Assert.assertEquals(104, data.getRecords(EventTimeStampRecord.class).size());

		// redeactivate

		Field activatedField = CEventAgentAdapter.class.getDeclaredField("activated");
		activatedField.setAccessible(true);
		activatedField.setBoolean(null, false);

		Method undoInstrumentation = EventInstrumentor.class.getDeclaredMethod("undoInstrumentation");
		undoInstrumentation.invoke(EventInstrumentor.getInstance());

		activatedField.setBoolean(null, true);

		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		idBuilder.newSynchronizedScopeEntity().addProbe(MonitorWaitingTimeProbe.MODEL_PROBE).entityDone();
		InstrumentationDescription desc = idBuilder.build();

		AdaptiveInstrumentationFacade.getInstance().instrument(desc);

		// Redeactivated

		jvmti.newMonitorWaitEvent(thread, getClass(), 1000);
		data = getData();
		Assert.assertEquals(105, data.getRecords(EventTimeStampRecord.class).size());
	}

	@Test
	public void loadTest() throws MeasurementException, InterruptedException {
		final Thread refThread = new Thread();

		final Thread threadOne = new Thread() {
			public void run() {
				for (int i = 0; i < 100; i++) {
					try {
						jvmti.newMonitorWaitEvent(refThread, getClass(), i);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						fail("Exception!\n" + e.getStackTrace());
					}
				}
			};
		};

		final Thread threadTwo = new Thread() {
			public void run() {
				for (int i = 0; i < 150; i++) {
					try {
						jvmti.newMonitorEnteredEvent(refThread, getClass(), i);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						fail("Exception!\n" + e.getStackTrace());
					}
				}
			};
		};

		threadOne.start();
		threadTwo.start();

		threadOne.join();
		threadTwo.join();

		Thread.sleep(2000);

		MeasurementData data = getData();
		Assert.assertEquals(250, data.getRecords(EventTimeStampRecord.class).size());
	}

	@After
	public void uninstrument() throws InstrumentationException {
		collector.disable();
	}

	public static MeasurementData getData() throws MeasurementException, InterruptedException {
		Thread.sleep(100);

		AbstractDataSource dataSource = AbstractDataSource.getDefaultDataSource();

		return dataSource.read();
	}

}
