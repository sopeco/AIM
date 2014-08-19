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

public class DummyProbe2 extends AbstractEnclosingProbe {
	public DummyProbe2(IExtension<?> provider) {
		super(provider);
	}

	@ProbeVariable
	public int _DummyProbe_testVariable;

	@ProbeVariable
	public ResponseTimeRecord _DummyProbe_record;

	@ProbeVariable
	public NanoResponseTimeRecord _DummyProbe_NanoResponseTimeRecordVariable;

	@ProbeBeforePart(requiredMethodName = "methodReq")
	public void beforePart() {
		System.out.println("BeforeControlSequence");
	}

	@ProbeBeforePart(requiredMethodName = "otherMethod")
	public void beforePart2() {
		System.out.println("AnotherControlSequence");
	}

	@ProbeAfterPart(requiredMethodName = "methodReq")
	public void afterPart() {
		System.out.println("AfterControlSequence");
	}
	
	@ProbeAfterPart(requiredMethodName = "otherMethod")
	public void afterPart2() {
		System.out.println("AfterSecondControlSequence");
	}
}
