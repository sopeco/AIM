package org.aim.mainagent;

import static org.junit.Assert.fail;
import static org.junit.Assume.assumeNoException;

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
import org.aim.artifacts.measurement.collector.MemoryDataSource;
import org.aim.artifacts.records.EventTimeStampRecord;
import org.aim.description.InstrumentationDescription;
import org.aim.description.builder.InstrumentationDescriptionBuilder;
import org.aim.mainagent.events.ForMonitorWaitingTimeProbe;
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
	public void instrument() throws InstrumentationException {
		try {
			jvmti = new DummyJVMTIFacade();
		} catch (NoSuchMethodException | SecurityException e) {
			assumeNoException(e);
		}

		try {
			Field monitorEventListenerField = CEventAgentAdapter.class.getDeclaredField("initialized");
			monitorEventListenerField.setAccessible(true);
			monitorEventListenerField.setBoolean(null, true);

			Field activatedField = CEventAgentAdapter.class.getDeclaredField("activated");
			activatedField.setAccessible(true);
			activatedField.setBoolean(null, true);
		} catch (NoSuchFieldException | SecurityException e) {
			assumeNoException(e);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			assumeNoException(e);
		}

		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		idBuilder.newSynchronizedScopeEntity().addProbe(ForMonitorWaitingTimeProbe.MODEL_PROBE).entityDone();
		InstrumentationDescription desc = idBuilder.build();

		AdaptiveInstrumentationFacade.getInstance().instrument(desc);

		try {
			collector = AbstractDataSource.getDefaultDataSource();

			collector.enable();
			Sampling.getInstance().start();
		} catch (MeasurementException e) {
			assumeNoException(e);
		}
	}

	@Test
	public void monitorEventsTest() throws IllegalAccessException, InvocationTargetException,
			MeasurementException {
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
			jvmti.newMonitorWaitEvent(null, this, i);
			jvmti.newMonitorEnteredEvent(thread, getClass(), i);
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			assumeNoException(e);
		}

		data = getData();
		Assert.assertEquals(102, data.getRecords(EventTimeStampRecord.class).size());

		jvmti.newMonitorEnteredEvent(null, null, -1);
		jvmti.newMonitorWaitEvent(null, null, -1);

		// redeactivate

		try {
			Field activatedField = CEventAgentAdapter.class.getDeclaredField("activated");
			activatedField.setAccessible(true);
			activatedField.setBoolean(null, false);

			Field eventInstrumentorField = AdaptiveInstrumentationFacade.class.getDeclaredField("eventInstrumentor");
			Method undoInstrumentation = EventInstrumentor.class.getDeclaredMethod("undoInstrumentation");
			undoInstrumentation.invoke(eventInstrumentorField.get(null));

			activatedField.setBoolean(null, true);
		} catch (NoSuchFieldException | SecurityException e) {
			assumeNoException(e);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			assumeNoException(e);
		} catch (NoSuchMethodException e) {
			assumeNoException(e);
		}

		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		idBuilder.newSynchronizedScopeEntity().addProbe(ForMonitorWaitingTimeProbe.MODEL_PROBE).entityDone();
		InstrumentationDescription desc = idBuilder.build();

		try {
			AdaptiveInstrumentationFacade.getInstance().instrument(desc);
		} catch (InstrumentationException e) {
			assumeNoException(e);
		}

		// Redeactivated

		jvmti.newMonitorWaitEvent(thread, getClass(), 1000);
		data = getData();
		Assert.assertEquals(103, data.getRecords(EventTimeStampRecord.class).size());
	}
	
	@Test
	public void loadTest() throws MeasurementException {
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
		
		try {
			threadOne.join();
			threadTwo.join();
		} catch (InterruptedException e) {
			assumeNoException(e);
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			assumeNoException(e);
		}
		
		MeasurementData data = getData();
		Assert.assertEquals(250, data.getRecords(EventTimeStampRecord.class).size());
	}

	@After
	public void uninstrument() throws InstrumentationException {
		collector.disable();
	}

	public static MeasurementData getData() throws MeasurementException {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			assumeNoException(e);
		}

		AbstractDataSource dataSource = AbstractDataSource.getDefaultDataSource();

		return dataSource.read();
	}

}
