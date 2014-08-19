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
import org.aim.artifacts.probes.ThreadTracingProbe;
import org.aim.artifacts.records.ThreadTracingRecord;

public class ThreadTracingProbeTest extends ProbeTest {

	@Override
	protected Class<? extends AbstractEnclosingProbe> getProbeType() {
		return ThreadTracingProbe.class;
	}

	@Override
	protected void checkPreCall(MeasurementData mData) {
		Assert.assertTrue(mData.getRecords().isEmpty());

	}

	@Override
	protected void checkPostCall(MeasurementData mData) {
		Assert.assertFalse(mData.getRecords().isEmpty());
		Assert.assertEquals(Thread.currentThread().getId(), mData.getRecords(ThreadTracingRecord.class).get(0)
				.getThreadId());
		Assert.assertTrue(mData.getRecords(ThreadTracingRecord.class).get(0).getExitNanoTime() >= mData
				.getRecords(ThreadTracingRecord.class).get(0).getEnterNanoTime());

	}

	@Override
	protected int getNumberOfMethodInvokations() {
		return 1;
	}

}
