/** Copyright 2014 SAP AG
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Properties;

import org.aim.aiminterface.description.instrumentation.InstrumentationDescription;
import org.aim.aiminterface.entities.measurements.MeasurementData;
import org.aim.aiminterface.exceptions.InstrumentationException;
import org.aim.aiminterface.exceptions.MeasurementException;
import org.aim.api.measurement.collector.AbstractDataSource;
import org.aim.api.measurement.collector.CollectorFactory;
import org.aim.artifacts.measurement.collector.MemoryDataSource;
import org.aim.artifacts.probes.ResponsetimeProbe;
import org.aim.artifacts.records.ResponseTimeRecord;
import org.aim.description.builder.InstrumentationDescriptionBuilder;
import org.aim.mainagent.instrumentation.JInstrumentation;
import org.junit.After;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lpe.common.config.GlobalConfiguration;
import org.lpe.common.extension.ExtensionRegistry;
import org.test.sut.ClassA;
import org.test.sut.ThreadA;

public class GranularityTest {

	public static void enableMeasurement() throws MeasurementException {
		final AbstractDataSource dataSource = AbstractDataSource.getDefaultDataSource();

		dataSource.enable();
	}

	public static void disableMeasurement() throws MeasurementException {
		final AbstractDataSource dataSource = AbstractDataSource.getDefaultDataSource();

		dataSource.disable();
	}

	public static MeasurementData getData() throws MeasurementException {
		final AbstractDataSource dataSource = AbstractDataSource.getDefaultDataSource();

		return dataSource.read();
	}

	@BeforeClass
	public static void prepareCollector() {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		final ClassA a = new ClassA();
		final AbstractDataSource dataSource = CollectorFactory.createDataSource(MemoryDataSource.class.getName(), null);
		AbstractDataSource.setDefaultDataSource(dataSource);

		final Properties globalProperties = new Properties();
		final String currentDir = System.getProperty("user.dir");
		globalProperties.setProperty(ExtensionRegistry.APP_ROOT_DIR_PROPERTY_KEY, currentDir);
		globalProperties.setProperty(ExtensionRegistry.PLUGINS_FOLDER_PROPERTY_KEY, "plugins");
		GlobalConfiguration.initialize(globalProperties);
	}

	@After
	public void revertInstrumentation() throws InstrumentationException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		AdaptiveInstrumentationFacade.getInstance().undoInstrumentation();
	}

	@Test
	public void testGranularity() throws InstrumentationException, MeasurementException, InterruptedException {
		Assume.assumeNotNull(System.getProperties().get(JInstrumentation.J_INSTRUMENTATION_KEY));
		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		final InstrumentationDescription descr = idBuilder.newMethodScopeEntity(ClassA.class.getName() + ".methodA1()")
				.addProbe(ResponsetimeProbe.MODEL_PROBE).entityDone().newGlobalRestriction().setGranularity(0.5)
				.restrictionDone().build();

		AdaptiveInstrumentationFacade.getInstance().instrument(descr);
		enableMeasurement();

		final Thread[] threads = { new ThreadA(1), new ThreadA(2), new ThreadA(3), new ThreadA(4) };

		for (final Thread t : threads) {
			t.start();
		}

		for (final Thread t : threads) {
			t.join();
		}

		disableMeasurement();
		final MeasurementData data = getData();
		assertFalse(data.getRecords().isEmpty());
		assertEquals(2, data.selectRecords(ResponseTimeRecord.class).size());
	}

}
