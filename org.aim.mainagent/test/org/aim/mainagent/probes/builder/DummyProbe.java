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
package org.aim.mainagent.probes.builder;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.ProbeAfterPart;
import org.aim.api.instrumentation.ProbeBeforePart;
import org.aim.api.instrumentation.ProbeVariable;
import org.aim.artifacts.records.NanoResponseTimeRecord;
import org.aim.artifacts.records.ResponseTimeRecord;
import org.lpe.common.extension.IExtension;

public class DummyProbe extends AbstractEnclosingProbe {

	public DummyProbe(IExtension<?> provider) {
		super(provider);
	}

	@ProbeVariable
	public int _DummyProbe_testVariable;

	@ProbeVariable
	public ResponseTimeRecord _DummyProbe_record;

	@ProbeVariable
	public NanoResponseTimeRecord _DummyProbe_NanoResponseTimeRecordVariable;

	@ProbeBeforePart
	public void beforePart() {
		System.out.println("BeforeControlSequence");
		_DummyProbe_record = new org.aim.artifacts.records.ResponseTimeRecord();
		_DummyProbe_NanoResponseTimeRecordVariable = new NanoResponseTimeRecord();
		_DummyProbe_record.setOperation(__methodSignature);
		_DummyProbe_NanoResponseTimeRecordVariable.setTimeStamp(_GenericProbe_startTime);
	}

	@ProbeAfterPart
	public void afterPart() {
		System.out.println("AfetControlSequence");
		_GenericProbe_collector.newRecord(_DummyProbe_record);
	}
}
