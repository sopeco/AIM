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
import org.aim.api.instrumentation.ProbeBeforePart;
import org.aim.api.instrumentation.ProbeVariable;
import org.aim.artifacts.records.NanoResponseTimeRecord;
import org.aim.description.probes.MeasurementProbe;
import org.aim.description.scopes.MethodsEnclosingScope;
import org.lpe.common.extension.IExtension;

/**
 * Probe for measuring response time in nano-seconds.
 * 
 * @author Alexander Wert
 * 
 */
public class NanoResponsetimeProbe extends AbstractEnclosingProbe {
	public static final MeasurementProbe<MethodsEnclosingScope> MODEL_PROBE = new MeasurementProbe<>(
			NanoResponsetimeProbe.class.getName());

	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider.
	 */
	public NanoResponsetimeProbe(IExtension<?> provider) {
		super(provider);
	}

	@ProbeVariable
	public long _NanoResponsetimeProbe_startTime;
	@ProbeVariable
	public long _NanoResponsetimeProbe_stopTime;
	@ProbeVariable
	public NanoResponseTimeRecord _NanoResponsetimeProbe_record;

	/**
	 * Before part.
	 */
	@ProbeBeforePart
	public void beforePart() {
		_NanoResponsetimeProbe_startTime = System.nanoTime();
	}

	/**
	 * After part.
	 */
	@ProbeAfterPart
	public void afterPart() {
		_NanoResponsetimeProbe_stopTime = System.nanoTime();
		_NanoResponsetimeProbe_record = new NanoResponseTimeRecord();
		_NanoResponsetimeProbe_record.setCallId(_GenericProbe_callId);
		_NanoResponsetimeProbe_record.setOperation(__methodSignature);
		_NanoResponsetimeProbe_record.setNanoTimestamp(_NanoResponsetimeProbe_startTime);
		_NanoResponsetimeProbe_record.setResponseTime(_NanoResponsetimeProbe_stopTime
				- _NanoResponsetimeProbe_startTime);
		_NanoResponsetimeProbe_record.setTimeStamp(_GenericProbe_startTime);

		_GenericProbe_collector.newRecord(_NanoResponsetimeProbe_record);
	}
}
