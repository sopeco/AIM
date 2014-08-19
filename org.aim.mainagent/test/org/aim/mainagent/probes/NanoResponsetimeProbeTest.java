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

import junit.framework.Assert;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.measurement.MeasurementData;
import org.aim.artifacts.probes.NanoResponsetimeProbe;
import org.aim.artifacts.records.NanoResponseTimeRecord;

public class NanoResponsetimeProbeTest extends ProbeTest {

	@Override
	protected Class<? extends AbstractEnclosingProbe> getProbeType() {
		// TODO Auto-generated method stub
		return NanoResponsetimeProbe.class;
	}

	@Override
	protected void checkPreCall(MeasurementData mData) {
		Assert.assertTrue(mData.getRecords().isEmpty());

	}

	@Override
	protected void checkPostCall(MeasurementData mData) {
		Assert.assertFalse(mData.getRecords().isEmpty());
		Assert.assertEquals(NanoResponseTimeRecord.class, mData.getRecords().get(0).getClass());
		NanoResponseTimeRecord record = mData.getRecords(NanoResponseTimeRecord.class).get(0);
		Assert.assertEquals(INSTRUmENTED_METHOD, record.getOperation());

	}

	@Override
	protected int getNumberOfMethodInvokations() {
		return 1;
	}

}
