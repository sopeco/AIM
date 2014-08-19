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
package org.aim.artifacts.instrumentation;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import javax.ws.rs.core.MediaType;

import org.aim.api.exceptions.InstrumentationException;
import org.aim.api.exceptions.MeasurementException;
import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.entities.FlatInstrumentationState;
import org.aim.api.instrumentation.entities.OverheadData;
import org.aim.api.instrumentation.entities.SupportedExtensions;
import org.aim.api.measurement.MeasurementData;
import org.aim.artifacts.measurement.collector.StreamReader;
import org.aim.description.InstrumentationDescription;
import org.lpe.common.util.web.LpeWebUtils;

import com.sun.jersey.api.client.WebResource;

/**
 * Service client for instrumentation.
 * 
 * @author Alexander Wert
 * 
 */
public class InstrumentationClient {
	public static final String URL_PATH_INSTRUMENTATION = "instrumentation";
	public static final String URL_PATH_MEASUREMENT = "measurement";
	public static final String PATH_PREFIX = "agent";

	private static final String TEST_CONNECTION = PATH_PREFIX + "/" + "testConnection";
	private static final String INSTRUMENT = PATH_PREFIX + "/" + URL_PATH_INSTRUMENTATION + "/" + "instrument";
	private static final String UNINSTRUMENT = PATH_PREFIX + "/" + URL_PATH_INSTRUMENTATION + "/" + "uninstrument";
	private static final String GET_STATE = PATH_PREFIX + "/" + URL_PATH_INSTRUMENTATION + "/" + "getState";
	private static final String GET_SUPPORTED_EXTENSIONS = PATH_PREFIX + "/" + URL_PATH_INSTRUMENTATION + "/"
			+ "getSupportedExtensions";

	private static final String ENABLE = PATH_PREFIX + "/" + URL_PATH_MEASUREMENT + "/" + "enable";
	private static final String DISABLE = PATH_PREFIX + "/" + URL_PATH_MEASUREMENT + "/" + "disable";
	private static final String GET_DATA = PATH_PREFIX + "/" + URL_PATH_MEASUREMENT + "/" + "getdata";
	private static final String CURRENT_TIME = PATH_PREFIX + "/" + URL_PATH_MEASUREMENT + "/" + "currentTime";
	private static final String MEASURE_OVERHEAD = PATH_PREFIX + "/" + URL_PATH_MEASUREMENT + "/" + "measureOverhead";

	private final String baseUrl;
	private String host;
	private String port;
	private WebResource webResource;

	/**
	 * Constructor.
	 * 
	 * @param host
	 *            host of the service
	 * @param port
	 *            port where to reach service
	 */
	public InstrumentationClient(String host, String port) {
		this.host = host;
		this.port = port;
		this.baseUrl = "http://" + host + ":" + port;
		webResource = LpeWebUtils.getWebClient().resource(baseUrl);
	}

	/**
	 * Instruments the code according to the passed
	 * {@link InstrumentationDescription}.
	 * 
	 * @param description
	 *            describes where and how to instrument the application code
	 * @throws InstrumentationException
	 *             thrown if exception occur during instrumentation
	 */
	public void instrument(InstrumentationDescription description) throws InstrumentationException {

		webResource.path(INSTRUMENT).type(MediaType.APPLICATION_JSON).post(description);

	}

	/**
	 * Reverts all previous instrumentation steps and resets the application
	 * code to the original state.
	 * 
	 * false
	 * 
	 * @throws InstrumentationException
	 *             thrown if exception occur during uninstrumentation
	 */
	public void uninstrument() throws InstrumentationException {

		webResource.path(UNINSTRUMENT).post();

	}

	/**
	 * Enables monitoring or measurement data collection.
	 * 
	 * @throws MeasurementException
	 *             thrown if monitoring fails
	 */
	public void enableMonitoring() throws MeasurementException {
		webResource.path(ENABLE).post();
	}

	/**
	 * Disables monitoring or measurement data collection.
	 * 
	 * @throws MeasurementException
	 *             thrown if monitoring fails
	 */
	public void disableMonitoring() throws MeasurementException {
		webResource.path(DISABLE).post();
	}

	/**
	 * Retrieves the current instrumentation state.
	 * 
	 * @return a flat representation of the internal instrumentation state.
	 */
	public FlatInstrumentationState getInstrumentationState() {
		return webResource.path(GET_STATE).accept(MediaType.APPLICATION_JSON).get(FlatInstrumentationState.class);
	}

	/**
	 * Retrieves all supported extensions for the extension points of the
	 * instrumentation description.
	 * 
	 * @return an object wrapping all supported extensions
	 */
	public SupportedExtensions getSupportedExtensions() {
		return webResource.path(GET_SUPPORTED_EXTENSIONS).accept(MediaType.APPLICATION_JSON)
				.get(SupportedExtensions.class);
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
			connection = LpeWebUtils.get(baseUrl + "/" + GET_DATA);
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
	 * Measures the overhead of the given probe type.
	 * 
	 * @param probeType
	 *            type of the probe
	 * @return overhead information.
	 */
	public OverheadData measureProbeOverhead(Class<? extends AbstractEnclosingProbe> probeType) {
		String probeClassName = probeType.getName();
		return webResource.path(MEASURE_OVERHEAD).accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.post(OverheadData.class, probeClassName);
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
			connection = LpeWebUtils.get(baseUrl + "/" + GET_DATA);
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
	 * @return the current local timestamp of the specific controller
	 */
	public long getCurrentTime() {
		return webResource.path(CURRENT_TIME).accept(MediaType.APPLICATION_JSON).get(Long.class);
	}

	/**
	 * 
	 * @return true if connecting to service possible
	 */
	public boolean testConnection() {
		return testConnection(host, port);
	}

	/**
	 * tests connection to the agent.
	 * 
	 * @param host
	 *            instrumentation host
	 * @param port
	 *            instrumentation port
	 * @return true if connection could have been established
	 */
	public static boolean testConnection(String host, String port) {
		String path = TEST_CONNECTION;
		return LpeWebUtils.testConnection(host, port, path);
	}
}
