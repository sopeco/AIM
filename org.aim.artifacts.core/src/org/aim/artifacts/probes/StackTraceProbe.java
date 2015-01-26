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
import org.aim.artifacts.records.StackTraceRecord;
import org.aim.description.probes.MeasurementProbe;
import org.aim.description.scopes.MethodsEnclosingScope;
import org.lpe.common.extension.IExtension;

/**
 * Probe for deriving stack traces.
 * 
 * @author Henning Schulz
 * 
 */
public class StackTraceProbe extends AbstractEnclosingProbe {

	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider
	 */
	public StackTraceProbe(IExtension<?> provider) {
		super(provider);
	}

	public static final MeasurementProbe<MethodsEnclosingScope> MODEL_PROBE = new MeasurementProbe<>(
			StackTraceProbe.class.getName());

	@ProbeVariable
	public StackTraceRecord _StackTraceProbe_record;

	@ProbeVariable
	public java.lang.StringBuilder _StackTraceProbe_builder;

	@ProbeVariable
	public boolean _StackTraceProbe_first;
	
	@ProbeVariable
	public StackTraceElement[] _StackTraceProbe_stackTrace;
	
	@ProbeVariable
	public StackTraceElement _StackTraceProbe_stackTraceElem;

	@ProbeAfterPart()
	public void afterPart() {
		_StackTraceProbe_builder = new java.lang.StringBuilder();
		_StackTraceProbe_first = true;
		_StackTraceProbe_stackTrace = java.lang.Thread.currentThread().getStackTrace();
		for (int _StackTraceProbe_i = 0; _StackTraceProbe_i < _StackTraceProbe_stackTrace.length; _StackTraceProbe_i++) {
			_StackTraceProbe_stackTraceElem = _StackTraceProbe_stackTrace[_StackTraceProbe_i];
			if (_StackTraceProbe_first) {
				_StackTraceProbe_first = false;
			} else {
				_StackTraceProbe_builder.append(" | ");
			}
			_StackTraceProbe_builder.append(_StackTraceProbe_stackTraceElem.getClassName());
			_StackTraceProbe_builder.append(".");
			_StackTraceProbe_builder.append(_StackTraceProbe_stackTraceElem.getMethodName());
			_StackTraceProbe_builder.append("(");
			_StackTraceProbe_builder.append(_StackTraceProbe_stackTraceElem.getLineNumber());
			_StackTraceProbe_builder.append(")");
		}

		_StackTraceProbe_record = new StackTraceRecord();
		_StackTraceProbe_record.setCallId(_GenericProbe_callId);
		_StackTraceProbe_record.setOperation(__methodSignature);
		_StackTraceProbe_record.setStackTrace(_StackTraceProbe_builder.toString());
		_StackTraceProbe_record.setTimeStamp(_GenericProbe_startTime);
		_GenericProbe_collector.newRecord(_StackTraceProbe_record);
	}

}
