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

import java.io.ByteArrayInputStream;
import java.io.OutputStream;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.aim.aiminterface.IAdaptiveInstrumentation;
import org.aim.aiminterface.description.instrumentation.InstrumentationDescription;
import org.aim.aiminterface.entities.measurements.MeasurementData;
import org.aim.aiminterface.entities.results.FlatInstrumentationState;
import org.aim.aiminterface.entities.results.OverheadData;
import org.aim.aiminterface.entities.results.SupportedExtensions;
import org.aim.aiminterface.exceptions.InstrumentationException;
import org.aim.aiminterface.exceptions.MeasurementException;
import org.aim.artifacts.measurement.collector.StreamReader;
import org.aim.mainagent.AdaptiveInstrumentationFacadeMXBean;

/**
 * Service client for instrumentation.
 * 
 * @author Steffen Becker
 * 
 */
public class JMXAdaptiveInstrumentationClient implements IAdaptiveInstrumentation {
	private final String host;
	private final String port;
	private final AdaptiveInstrumentationFacadeMXBean bean;
	private JMXServiceURL url;
	private boolean monitoringEnabled;

	/**
	 * Constructor.
	 * 
	 * @param host
	 *            host of the service
	 * @param port
	 *            port where to reach service
	 */
	public JMXAdaptiveInstrumentationClient(final String host, final String port) {
		super();
		this.host = host;
		this.port = port;
		try {
			this.url =  new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+host+":"+port+"/jmxrmi");
			final JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			final MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			final ObjectName mbeanName = new ObjectName("org.aim:type=AdaptiveInstrumentationFacade");
			this.bean = JMX.newMXBeanProxy(mbsc, mbeanName, AdaptiveInstrumentationFacadeMXBean.class, true);
		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(-1);
			throw new RuntimeException();
		}
	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#instrument(org.aim.description.InstrumentationDescription)
	 */
	@Override
	public void instrument(final InstrumentationDescription description)
			throws InstrumentationException {
		bean.instrument(description);
	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#uninstrument()
	 */
	@Override
	public void uninstrument() throws InstrumentationException {
		bean.undoInstrumentation();
	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#enableMonitoring()
	 */
	@Override
	public void enableMonitoring() throws MeasurementException {
		bean.enableMonitoring();
		this.monitoringEnabled = true;
	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#disableMonitoring()
	 */
	@Override
	public void disableMonitoring() throws MeasurementException {
		bean.disableMonitoring();
		this.monitoringEnabled = false;
	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#isMonitoringEnabled()
	 */
	@Override
	public boolean isMonitoringEnabled() {
		return this.monitoringEnabled;
	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#getInstrumentationState()
	 */
	@Override
	public FlatInstrumentationState getInstrumentationState() {
		return bean.getInstrumentationState();
	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#getSupportedExtensions()
	 */
	@Override
	public SupportedExtensions getSupportedExtensions() {
		return bean.getSupportedExtensions();
	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#getMeasurementData()
	 */
	@Override
	public MeasurementData getMeasurementData() throws MeasurementException {
		final byte[] data = bean.getMeasurementData();
		final StreamReader reader = new StreamReader();
		reader.setSource(new ByteArrayInputStream(data));
	    return reader.read();
	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#measureProbeOverhead(java.lang.Class)
	 */
	@Override
	public OverheadData measureProbeOverhead(
			final String probeClassName) {
		return bean.measureProbeOverhead(probeClassName);
	}

	/**
	 * 
	 * @param oStream
	 *            output stream where to pipe the input to
	 * @throws MeasurementException
	 *             thrown if data cannot be retrieved
	 */
	@Override
	public void pipeToOutputStream(final OutputStream oStream)
			throws MeasurementException {
		final byte[] data = bean.getMeasurementData();
		final StreamReader reader = new StreamReader();
		reader.setSource(new ByteArrayInputStream(data));
		reader.pipeToOutputStream(oStream);
	}

	/* (non-Javadoc)
	 * @see org.aim.artifacts.instrumentation.IAdaptiveInstrumentation#getCurrentTime()
	 */
	@Override
	public long getCurrentTime() {
		return bean.getCurrentTime();
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
		// TODO
		// FIXME
		return true;
	}
}
