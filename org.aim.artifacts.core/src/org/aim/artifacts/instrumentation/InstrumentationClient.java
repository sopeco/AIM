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

import org.aim.aiminterface.IAdaptiveInstrumentation;
import org.aim.aiminterface.description.instrumentation.InstrumentationDescription;
import org.aim.aiminterface.entities.measurements.MeasurementData;
import org.aim.aiminterface.entities.results.FlatInstrumentationState;
import org.aim.aiminterface.entities.results.OverheadData;
import org.aim.aiminterface.entities.results.SupportedExtensions;
import org.aim.aiminterface.exceptions.InstrumentationException;
import org.aim.aiminterface.exceptions.MeasurementException;
import org.aim.artifacts.measurement.collector.StreamReader;
import org.lpe.common.util.LpeHTTPUtils;

import com.sun.jersey.api.client.WebResource;

/**
 * Service client for instrumentation.
 * 
 * @author Alexander Wert
 * 
 */
public class InstrumentationClient implements IAdaptiveInstrumentation {
	public static final String URL_PATH_INSTRUMENTATION = "instrumentation";
	public static final String URL_PATH_MEASUREMENT = "measurement";
	public static final String PATH_PREFIX = "agent";

	private static final String TEST_CONNECTION = PATH_PREFIX + "/"
			+ "testConnection";
	private static final String INSTRUMENT = PATH_PREFIX + "/"
			+ URL_PATH_INSTRUMENTATION + "/" + "instrument";
	private static final String UNINSTRUMENT = PATH_PREFIX + "/"
			+ URL_PATH_INSTRUMENTATION + "/" + "uninstrument";
	private static final String GET_STATE = PATH_PREFIX + "/"
			+ URL_PATH_INSTRUMENTATION + "/" + "getState";
	private static final String GET_SUPPORTED_EXTENSIONS = PATH_PREFIX + "/"
			+ URL_PATH_INSTRUMENTATION + "/" + "getSupportedExtensions";

	private static final String ENABLE = PATH_PREFIX + "/"
			+ URL_PATH_MEASUREMENT + "/" + "enable";
	private static final String DISABLE = PATH_PREFIX + "/"
			+ URL_PATH_MEASUREMENT + "/" + "disable";
	private static final String GET_DATA = PATH_PREFIX + "/"
			+ URL_PATH_MEASUREMENT + "/" + "getdata";
	private static final String CURRENT_TIME = PATH_PREFIX + "/"
			+ URL_PATH_MEASUREMENT + "/" + "currentTime";
	private static final String MEASURE_OVERHEAD = PATH_PREFIX + "/"
			+ URL_PATH_MEASUREMENT + "/" + "measureOverhead";
	private static final String MONITORING_STATE = PATH_PREFIX + "/"
			+ URL_PATH_MEASUREMENT + "/" + "monitoringState";

	private final String baseUrl;
	private final String host;
	private final String port;
	private final WebResource webResource;

	/**
	 * Constructor.
	 * 
	 * @param host
	 *            host of the service
	 * @param port
	 *            port where to reach service
	 */
	public InstrumentationClient(final String host, final String port) {
		this.host = host;
		this.port = port;
		this.baseUrl = "http://" + host + ":" + port;
		webResource = LpeHTTPUtils.getWebClient().resource(baseUrl);
	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#instrument(org.aim.description.InstrumentationDescription)
	 */
	@Override
	public void instrument(final InstrumentationDescription description)
			throws InstrumentationException {

		webResource.path(INSTRUMENT).type(MediaType.APPLICATION_JSON)
				.post(description);

	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#uninstrument()
	 */
	@Override
	public void uninstrument() throws InstrumentationException {

		webResource.path(UNINSTRUMENT).post();

	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#enableMonitoring()
	 */
	@Override
	public void enableMonitoring() throws MeasurementException {
		webResource.path(ENABLE).post();
	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#disableMonitoring()
	 */
	@Override
	public void disableMonitoring() throws MeasurementException {
		webResource.path(DISABLE).post();

	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#isMonitoringEnabled()
	 */
	@Override
	public boolean isMonitoringEnabled() {
		return webResource.path(MONITORING_STATE)
				.accept(MediaType.APPLICATION_JSON).get(Boolean.class);
	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#getInstrumentationState()
	 */
	@Override
	public FlatInstrumentationState getInstrumentationState() {
		return webResource.path(GET_STATE).accept(MediaType.APPLICATION_JSON)
				.get(FlatInstrumentationState.class);
	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#getSupportedExtensions()
	 */
	@Override
	public SupportedExtensions getSupportedExtensions() {
		return webResource.path(GET_SUPPORTED_EXTENSIONS)
				.accept(MediaType.APPLICATION_JSON)
				.get(SupportedExtensions.class);
	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#getMeasurementData()
	 */
	@Override
	public MeasurementData getMeasurementData() throws MeasurementException {

		HttpURLConnection connection = null;
		try {
			connection = LpeHTTPUtils.get(baseUrl + "/" + GET_DATA);
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

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#measureProbeOverhead(java.lang.Class)
	 */
	@Override
	public OverheadData measureProbeOverhead(
			final String probeClassName) {
		return webResource.path(MEASURE_OVERHEAD)
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.post(OverheadData.class, probeClassName);
	}

	/**
	 * 
	 * @param oStream
	 *            output stream where to pipe the input to
	 * @throws MeasurementException
	 *             thrown if data cannot be retrieved
	 */
	public void pipeToOutputStream(final OutputStream oStream)
			throws MeasurementException {
		HttpURLConnection connection = null;
		try {
			connection = LpeHTTPUtils.get(baseUrl + "/" + GET_DATA);
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

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#getCurrentTime()
	 */
	@Override
	public long getCurrentTime() {
		return webResource.path(CURRENT_TIME)
				.accept(MediaType.APPLICATION_JSON).get(Long.class);
	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#testConnection()
	 */
	@Override
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
	public static boolean testConnection(final String host, final String port) {
		final String path = TEST_CONNECTION;
		return LpeHTTPUtils.testConnection(host, port, path);
	}
}
