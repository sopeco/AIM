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
import org.aim.artifacts.records.ResponseTimeRecord;
import org.lpe.common.extension.IExtension;


public class AnotherDummyProbe extends AbstractEnclosingProbe {
	public AnotherDummyProbe(IExtension<?> provider) {
		super(provider);
	}

	@ProbeVariable
	public int _AnotherDummyProbe_testVariable;

	@ProbeVariable
	public ResponseTimeRecord _AnotherDummyProbe_record;

	@ProbeBeforePart
	public void beforePart() {
		System.out.println("BeforeControlSequence");
		System.out.println("AnotherBeforeControlSequence");
		_AnotherDummyProbe_record = new ResponseTimeRecord();
		_AnotherDummyProbe_record.setOperation(__methodSignature);

	}

	@ProbeAfterPart
	public void afterPart() {
		System.out.println("AnotherAfterControlSequence");
		_GenericProbe_collector.newRecord(_AnotherDummyProbe_record);
	}
}
