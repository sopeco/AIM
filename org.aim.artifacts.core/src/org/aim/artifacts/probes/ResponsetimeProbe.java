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

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.ProbeAfterPart;
import org.aim.api.instrumentation.ProbeVariable;
import org.aim.artifacts.records.ResponseTimeRecord;
import org.aim.description.probes.MeasurementProbe;
import org.aim.description.scopes.MethodsEnclosingScope;
import org.lpe.common.extension.IExtension;

/**
 * Probe for measuring response time in milli-seconds.
 * 
 * @author Alexander Wert
 * 
 */
public class ResponsetimeProbe extends AbstractEnclosingProbe {
	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider.
	 */
	public ResponsetimeProbe(IExtension<?> provider) {
		super(provider);
	}

	public static final MeasurementProbe<MethodsEnclosingScope> MODEL_PROBE = new MeasurementProbe<>(
			ResponsetimeProbe.class.getName());

	@ProbeVariable
	public long _ResponsetimeProbe_stopTime;
	@ProbeVariable
	public ResponseTimeRecord _ResponsetimeProbe_record;

	/**
	 * After part.
	 */
	@ProbeAfterPart()
	public void afterPart() {
		_ResponsetimeProbe_stopTime = System.currentTimeMillis();
		_ResponsetimeProbe_record = new ResponseTimeRecord();
		_ResponsetimeProbe_record.setCallId(_GenericProbe_callId);
		_ResponsetimeProbe_record.setOperation(__methodSignature);
		_ResponsetimeProbe_record.setResponseTime(_ResponsetimeProbe_stopTime - _GenericProbe_startTime);
		_ResponsetimeProbe_record.setTimeStamp(_GenericProbe_startTime);
		_GenericProbe_collector.newRecord(_ResponsetimeProbe_record);
	}

}
