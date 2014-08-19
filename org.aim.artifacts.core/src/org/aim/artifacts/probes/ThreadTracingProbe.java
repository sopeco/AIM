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
import org.aim.artifacts.records.ThreadTracingRecord;
import org.aim.description.probes.MeasurementProbe;
import org.aim.description.scopes.MethodsEnclosingScope;
import org.lpe.common.extension.IExtension;

/**
 * Collects thread information to be able to reconstruct traces.
 * 
 * @author Alexander Wert
 * 
 */
public class ThreadTracingProbe extends AbstractEnclosingProbe {

	public static final MeasurementProbe<MethodsEnclosingScope> MODEL_PROBE = new MeasurementProbe<>(
			ThreadTracingProbe.class.getName());

	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider.
	 */
	public ThreadTracingProbe(IExtension<?> provider) {
		super(provider);
	}

	@ProbeVariable
	public Long _ThreadTracingProbe_startNanoTime;
	@ProbeVariable
	public Long _ThreadTracingProbe_stopNanoTime;
	@ProbeVariable
	public Long _ThreadTracingProbe_threadId;
	@ProbeVariable
	public ThreadTracingRecord _ThreadTracingProbe_record;

	/**
	 * Before part.
	 */
	@ProbeBeforePart
	public void beforePart() {
		_ThreadTracingProbe_startNanoTime = System.nanoTime();
	}

	/**
	 * After part.
	 */
	@ProbeAfterPart
	public void afterPart() {
		_ThreadTracingProbe_stopNanoTime = System.nanoTime();
		_ThreadTracingProbe_threadId = java.lang.Thread.currentThread().getId();
		_ThreadTracingProbe_record = new ThreadTracingRecord(_GenericProbe_startTime, __methodSignature,
				_ThreadTracingProbe_threadId);
		_ThreadTracingProbe_record.setCallId(_GenericProbe_callId);
		_ThreadTracingProbe_record.setEnterNanoTime(_ThreadTracingProbe_startNanoTime);
		_ThreadTracingProbe_record.setExitNanoTime(_ThreadTracingProbe_stopNanoTime);
		_GenericProbe_collector.newRecord(_ThreadTracingProbe_record);

	}
}
