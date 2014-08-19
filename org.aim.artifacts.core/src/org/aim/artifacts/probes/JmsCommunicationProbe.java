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
package org.aim.artifacts.probes;

import java.lang.management.ManagementFactory;

import javax.jms.Message;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.ProbeAfterPart;
import org.aim.api.instrumentation.ProbeBeforePart;
import org.aim.api.instrumentation.ProbeVariable;
import org.aim.artifacts.records.JmsRecord;
import org.aim.description.probes.MeasurementProbe;
import org.aim.description.scopes.MethodsEnclosingScope;
import org.lpe.common.extension.IExtension;

/**
 * JMS communication probe. Collects information about JMS messaging flow.
 * 
 * @author Alexander Wert
 * 
 */
public class JmsCommunicationProbe extends AbstractEnclosingProbe {
	public static final MeasurementProbe<MethodsEnclosingScope> MODEL_PROBE = new MeasurementProbe<>(
			JmsCommunicationProbe.class.getName());
	public static final String MSG_CORRELATION_VARIABLE = "org_ppd_measurement_trace_msg_correlation";

	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider.
	 */
	public JmsCommunicationProbe(IExtension<?> provider) {
		super(provider);
	}

	@ProbeVariable
	public JmsRecord _JmsCommunicationProbe_record;

	@ProbeVariable
	public String _JmsCommunicationProbe_correlationValue;

	@ProbeVariable
	public String _JmsCommunicationProbe_stackTrace;

	/**
	 * Before part for the JMS send method.
	 */
	@ProbeBeforePart(requiredMethodName = "send(javax.jms.Message")
	public void sendMethodBeforePart() {
		_JmsCommunicationProbe_record = new JmsRecord();
		_JmsCommunicationProbe_record.setCallId(_GenericProbe_callId);
		_JmsCommunicationProbe_record.setThreadId(Thread.currentThread().getId());
		_JmsCommunicationProbe_record.setTimeStamp(_GenericProbe_startTime);

		_JmsCommunicationProbe_record.setClientId(ManagementFactory.getRuntimeMXBean().getName());
		_JmsCommunicationProbe_record.setWasSent((byte) 1);
		_JmsCommunicationProbe_correlationValue = String.valueOf(System.nanoTime()
				+ (int) (1000 * java.lang.Math.random()));

		try {
			((Message) __parameter[1]).setStringProperty(MSG_CORRELATION_VARIABLE,
					_JmsCommunicationProbe_correlationValue);
			_JmsCommunicationProbe_record.setMessageCorrelationHash(_JmsCommunicationProbe_correlationValue);
		} catch (Exception e) {
			_JmsCommunicationProbe_record.setMessageCorrelationHash("-");
		}

		_GenericProbe_collector.newRecord(_JmsCommunicationProbe_record);
	}

	/**
	 * Before part for the onMessage method.
	 */
	@ProbeBeforePart(requiredMethodName = "onMessage(javax.jms.Message")
	public void onMessageBeforePart() {
		_JmsCommunicationProbe_record = new JmsRecord();
		_JmsCommunicationProbe_record.setCallId(_GenericProbe_callId);
		_JmsCommunicationProbe_record.setThreadId(Thread.currentThread().getId());
		_JmsCommunicationProbe_record.setTimeStamp(_GenericProbe_startTime);

		_JmsCommunicationProbe_record.setClientId(ManagementFactory.getRuntimeMXBean().getName());
		_JmsCommunicationProbe_record.setWasSent((byte) 0);
		_JmsCommunicationProbe_correlationValue = String.valueOf(System.nanoTime()
				+ (int) (1000 * java.lang.Math.random()));

		try {
			if (((Message) __parameter[1]).propertyExists(MSG_CORRELATION_VARIABLE)) {
				_JmsCommunicationProbe_correlationValue = ((Message) __parameter[1])
						.getStringProperty(MSG_CORRELATION_VARIABLE);
				_JmsCommunicationProbe_record.setMessageCorrelationHash(_JmsCommunicationProbe_correlationValue);
			}

		} catch (Exception e) {
			_JmsCommunicationProbe_record.setMessageCorrelationHash("-");
		}

		_GenericProbe_collector.newRecord(_JmsCommunicationProbe_record);
	}

	/**
	 * After part for the JMS receive method.
	 */
	@ProbeAfterPart(requiredMethodName = "receive(")
	public void receiveAfterPart() {
		_JmsCommunicationProbe_record = new JmsRecord();
		_JmsCommunicationProbe_record.setCallId(_GenericProbe_callId);
		_JmsCommunicationProbe_record.setThreadId(Thread.currentThread().getId());
		_JmsCommunicationProbe_record.setTimeStamp(_GenericProbe_startTime);

		_JmsCommunicationProbe_record.setClientId(ManagementFactory.getRuntimeMXBean().getName());
		_JmsCommunicationProbe_record.setWasSent((byte) 0);
		_JmsCommunicationProbe_correlationValue = String.valueOf(System.nanoTime()
				+ (int) (1000 * java.lang.Math.random()));

		try {
			if (((Message) __returnObject).propertyExists(MSG_CORRELATION_VARIABLE)) {
				_JmsCommunicationProbe_correlationValue = ((Message) __returnObject)
						.getStringProperty(MSG_CORRELATION_VARIABLE);
				_JmsCommunicationProbe_record.setMessageCorrelationHash(_JmsCommunicationProbe_correlationValue);
			}

		} catch (Exception e) {
			_JmsCommunicationProbe_record.setMessageCorrelationHash("-");
		}

		_GenericProbe_collector.newRecord(_JmsCommunicationProbe_record);
	}

}
