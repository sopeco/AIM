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
package org.lpe.common.resourcemonitoring;

import java.lang.reflect.Field;
import java.util.Properties;

import org.aim.api.exceptions.MeasurementException;
import org.aim.api.measurement.MeasurementData;
import org.aim.artifacts.records.CPUUtilizationRecord;
import org.aim.artifacts.sampler.CPUSampler;
import org.aim.description.builder.InstrumentationDescriptionBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lpe.common.config.GlobalConfiguration;
import org.lpe.common.extension.ExtensionRegistry;

/**
 * Tests the resource monitoring server.
 * 
 * @author C5170547
 * 
 */
public class ResourceMonitorinServerTest {
	private static final String PORT = "8123";
	public static ResourceMonitoringClient client;

	/**
	 * starts server.
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	@BeforeClass
	public static void startServer() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		String[] args = { "start", "port=" + PORT };
		ServerLauncher.main(args);
		Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
		fieldSysPath.setAccessible(true);
		fieldSysPath.set(null, null);
		client = new ResourceMonitoringClient("localhost", PORT);
		
		Properties globalProperties = new Properties();
		String currentDir = System.getProperty("user.dir");
		globalProperties.setProperty(ExtensionRegistry.APP_ROOT_DIR_PROPERTY_KEY, currentDir);
		globalProperties.setProperty(ExtensionRegistry.PLUGINS_FOLDER_PROPERTY_KEY, "plugins");
		GlobalConfiguration.initialize(globalProperties);
	}

	/**
	 * tests connection.
	 */
	@Test
	public void testConnection() {
		Assert.assertTrue(client.testConnection());
	}

	/**
	 * tests get Timestamp.
	 */
	@Test
	public void testGetTimestamp() {
		long before = System.currentTimeMillis();
		long timestamp = client.getCurrentTime();
		long after = System.currentTimeMillis();

		Assert.assertTrue(before <= timestamp);
		Assert.assertTrue(timestamp <= after);
	}

	/**
	 * tests measurement.
	 */
	@Test
	public void testMeasurement() {
		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();

		idBuilder.newSampling(CPUSampler.class.getName(), 100);

		client.enableMonitoring(idBuilder.build());

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		client.disableMonitoring();

		MeasurementData mData = null;
		try {
			mData = client.getMeasurementData();
		} catch (MeasurementException e) {
			Assert.fail("Collecting data failed!");
		}
		Assert.assertNotNull(mData);
		Assert.assertTrue(mData.getRecords(CPUUtilizationRecord.class).size() > 0);
	}

}
