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
package org.aim.mainagent.probes;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.measurement.MeasurementData;
import org.aim.artifacts.probes.SQLQueryProbe;

public class SQLQueryProbeTest extends ProbeTest {

	@Override
	protected Class<? extends AbstractEnclosingProbe> getProbeType() {
		return SQLQueryProbe.class;
	}

	@Override
	protected void checkPreCall(MeasurementData mData) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void checkPostCall(MeasurementData mData) {
		// TODO Auto-generated method stub

	}

	@Override
	protected int getNumberOfMethodInvokations() {
		return 1;
	}

}
