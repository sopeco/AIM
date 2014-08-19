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

import java.lang.instrument.Instrumentation;

import javax.jms.Message;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.ProbeAfterPart;
import org.aim.api.instrumentation.ProbeBeforePart;
import org.aim.api.instrumentation.ProbeVariable;
import org.aim.artifacts.records.JmsMessageSizeRecord;
import org.aim.description.probes.MeasurementProbe;
import org.aim.description.scopes.APIScope;
import org.lpe.common.extension.IExtension;

/**
 * The {@link JmsMessageSizeProbe} collects information on JMS message sizes.
 * 
 * @author Alexander Wert
 * 
 */
public class JmsMessageSizeProbe extends AbstractEnclosingProbe {
	public static final MeasurementProbe<APIScope> MODEL_PROBE = new MeasurementProbe<>(
			JmsMessageSizeProbe.class.getName());

	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider.
	 */
	public JmsMessageSizeProbe(IExtension<?> provider) {
		super(provider);
	}

	@ProbeVariable
	public JmsMessageSizeRecord _JmsMessageSizeProbe_record;

	@ProbeVariable
	public String _JmsMessageSizeProbe_correlationValue;

	@ProbeVariable
	public Object _JmsMessageSizeProbe_payload;

	/**
	 * Before part for JMS onMessage method.
	 */
	@ProbeBeforePart(requiredMethodName = "onMessage(javax.jms.Message")
	public void onMessageBeforePart() {
		try {
			if (((Message) __parameter[1]).propertyExists(JmsCommunicationProbe.MSG_CORRELATION_VARIABLE)) {

				_JmsMessageSizeProbe_record = new JmsMessageSizeRecord();
				_JmsMessageSizeProbe_record.setTimeStamp(_GenericProbe_startTime);
				_JmsMessageSizeProbe_record.setCallId(_GenericProbe_callId);
				_JmsMessageSizeProbe_record.setSize(((Instrumentation) System.getProperties()
						.get(J_INSTRUMENTATION_KEY)).getObjectSize(__parameter[1]));
				_JmsMessageSizeProbe_record.setBodySize(((Instrumentation) System.getProperties().get(
						J_INSTRUMENTATION_KEY)).getObjectSize(((Message) __parameter[1]).getBody(Object.class)));
				((Message) __parameter[1]).getBody(Object.class);
				_JmsMessageSizeProbe_correlationValue = ((Message) __parameter[1])
						.getStringProperty(JmsCommunicationProbe.MSG_CORRELATION_VARIABLE);
				_JmsMessageSizeProbe_record.setMessageCorrelationHash(_JmsMessageSizeProbe_correlationValue);

				_GenericProbe_collector.newRecord(_JmsMessageSizeProbe_record);
			}

		} catch (Exception e) {
			_JmsMessageSizeProbe_record = null;
		}
	}

	/**
	 * After part for JMS receive method.
	 */
	@ProbeAfterPart(requiredMethodName = "receive(")
	public void receiveAfterPart() {
		try {
			if (((Message) __returnObject).propertyExists(JmsCommunicationProbe.MSG_CORRELATION_VARIABLE)) {

				_JmsMessageSizeProbe_record = new JmsMessageSizeRecord();
				_JmsMessageSizeProbe_record.setTimeStamp(_GenericProbe_startTime);
				_JmsMessageSizeProbe_record.setCallId(_GenericProbe_callId);
				_JmsMessageSizeProbe_record.setSize(((Instrumentation) System.getProperties()
						.get(J_INSTRUMENTATION_KEY)).getObjectSize(__returnObject));
				_JmsMessageSizeProbe_record.setBodySize(((Instrumentation) System.getProperties().get(
						J_INSTRUMENTATION_KEY)).getObjectSize(((Message) __returnObject).getBody(Object.class)));
				((Message) __returnObject).getBody(Object.class);
				_JmsMessageSizeProbe_correlationValue = ((Message) __returnObject)
						.getStringProperty(JmsCommunicationProbe.MSG_CORRELATION_VARIABLE);
				_JmsMessageSizeProbe_record.setMessageCorrelationHash(_JmsMessageSizeProbe_correlationValue);

				_GenericProbe_collector.newRecord(_JmsMessageSizeProbe_record);
			}

		} catch (Exception e) {
			_JmsMessageSizeProbe_record = null;
		}
	}
}
