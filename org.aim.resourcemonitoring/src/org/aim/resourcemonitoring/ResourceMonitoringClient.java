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
package org.aim.resourcemonitoring;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import javax.ws.rs.core.MediaType;

import org.aim.aiminterface.description.instrumentation.InstrumentationDescription;
import org.aim.aiminterface.entities.measurements.MeasurementData;
import org.aim.aiminterface.exceptions.MeasurementException;
import org.aim.artifacts.measurement.collector.StreamReader;
import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.lpe.common.util.LpeHTTPUtils;

import com.sun.jersey.api.client.WebResource;

/**
 * Client for resource monitoring.
 * 
 * @author Alexander Wert
 * 
 */
public class ResourceMonitoringClient {
	private static final AIMLogger LOGGER = AIMLoggerFactory.getLogger(ResourceMonitoringClient.class);

	private static final String REST = ServerLauncher.PREFIX + "/MonitoringService";
	private static final String TEST_CONNECTION = "testConnection";
	private static final String ENABLE = "startMonitor";
	private static final String DISABLE = "stopMonitor";
	private static final String GET_DATA = "getResult";
	private static final String CURRENT_TIME = "currentTime";

	private final String url;
	private final String host;
	private final String port;
	private final WebResource webResource;

	/**
	 * 
	 * @param host
	 *            host of the service
	 * @param port
	 *            port where to reach service
	 */
	public ResourceMonitoringClient(final String host, final String port) {
		this.host = host;
		this.port = port;
		url = "http://" + host + ":" + port;
		webResource = LpeHTTPUtils.getWebClient().resource(url);
	}

	/**
	 * enables monitoring / sampling.
	 * 
	 * @param instDescription
	 *            instrumentation description containing the sampling
	 *            configuration
	 */
	public void enableMonitoring(final InstrumentationDescription instDescription) {
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
			connection = LpeHTTPUtils.get(url + "/" + REST + "/" + GET_DATA);
			final StreamReader reader = new StreamReader();
			reader.setSource(connection.getInputStream());
			return reader.read();
		} catch (final IOException e) {
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
	public void pipeToOutputStream(final OutputStream oStream) throws MeasurementException {

		HttpURLConnection connection = null;
		try {
			connection = LpeHTTPUtils.get(url + "/" + REST + "/" + GET_DATA);
			final StreamReader reader = new StreamReader();
			reader.setSource(connection.getInputStream());
			reader.pipeToOutputStream(oStream);
		} catch (final IOException e) {
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
	public static boolean testConnection(final String host, final String port) {
		final String path = REST + "/" + TEST_CONNECTION;
		return LpeHTTPUtils.testConnection(host, port, path);
	}

}
