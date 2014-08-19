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

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import javax.ws.rs.core.MediaType;

import org.aim.api.exceptions.MeasurementException;
import org.aim.api.measurement.MeasurementData;
import org.aim.artifacts.measurement.collector.StreamReader;
import org.aim.description.InstrumentationDescription;
import org.lpe.common.util.web.LpeWebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.WebResource;

/**
 * Client for resource monitoring.
 * 
 * @author Alexander Wert
 * 
 */
public class ResourceMonitoringClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceMonitoringClient.class);

	private static final String REST = ServerLauncher.PREFIX + "/MonitoringService";
	private static final String TEST_CONNECTION = "testConnection";
	private static final String ENABLE = "startMonitor";
	private static final String DISABLE = "stopMonitor";
	private static final String GET_DATA = "getResult";
	private static final String CURRENT_TIME = "currentTime";

	private String url;
	private String host;
	private String port;
	private WebResource webResource;

	/**
	 * 
	 * @param host
	 *            host of the service
	 * @param port
	 *            port where to reach service
	 */
	public ResourceMonitoringClient(String host, String port) {
		this.host = host;
		this.port = port;
		url = "http://" + host + ":" + port;
		webResource = LpeWebUtils.getWebClient().resource(url);
	}

	/**
	 * enables monitoring / sampling.
	 * 
	 * @param instDescription
	 *            instrumentation description containing the sampling
	 *            configuration
	 */
	public void enableMonitoring(InstrumentationDescription instDescription) {
		webResource.path(REST).path(ENABLE).type(MediaType.APPLICATION_JSON).post(instDescription);
		LOGGER.debug("Resource monitoring enabled!");
	}

	/**
	 * disables sampling.
	 * 
	 */
	public void disableMonitoring() {
		webResource.path(REST).path(DISABLE).post();
		LOGGER.debug("Resource monitoring disabled!");
	}

	/**
	 * 
	 * @return collected measurement data
	 * @throws MeasurementException
	 *             thrown if data cannot be retrieved
	 */
	public MeasurementData getMeasurementData() throws MeasurementException {

		HttpURLConnection connection = null;
		try {
			connection = LpeWebUtils.get(url + "/" + REST + "/" + GET_DATA);
			StreamReader reader = new StreamReader();
			reader.setSource(connection.getInputStream());
			return reader.read();
		} catch (IOException e) {
			throw new MeasurementException(e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}

		}
	}

	/**
	 * 
	 * @param oStream
	 *            output stream where to pipe the input to
	 * @throws MeasurementException
	 *             thrown if data cannot be retrieved
	 */
	public void pipeToOutputStream(OutputStream oStream) throws MeasurementException {

		HttpURLConnection connection = null;
		try {
			connection = LpeWebUtils.get(url + "/" + REST + "/" + GET_DATA);
			StreamReader reader = new StreamReader();
			reader.setSource(connection.getInputStream());
			reader.pipeToOutputStream(oStream);
		} catch (IOException e) {
			throw new MeasurementException(e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}

		}
	}

	/**
	 * 
	 * @return current time of the local machine
	 */
	public long getCurrentTime() {
		return webResource.path(REST).path(CURRENT_TIME).accept(MediaType.APPLICATION_JSON).get(long.class);
	}

	/**
	 * 
	 * @return true if connecting to service possible
	 */
	public boolean testConnection() {
		return testConnection(host, port);
	}

	/**
	 * Tests connection to resource monitoring.
	 * 
	 * @param host
	 *            server host
	 * @param port
	 *            server port
	 * @return true, if connection could be established
	 */
	public static boolean testConnection(String host, String port) {
		String path = REST + "/" + TEST_CONNECTION;
		return LpeWebUtils.testConnection(host, port, path);
	}

}
