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
import org.aim.artifacts.probes.ResponsetimeProbe;
import org.aim.artifacts.records.ResponseTimeRecord;

public class ResponseTimeProbeTest extends ProbeTest {

	public static final String TEST_METHOD = "testMethod";

	public static final String AFTER_CONTROL_SEQUENCE_1 = "_ResponsetimeProbe_stopTime = System.currentTimeMillis()";
	public static final String AFTER_CONTROL_SEQUENCE_2 = "_ResponsetimeProbe_record.setOperation(\"" + TEST_METHOD
			+ "\")";
	public static final String AFTER_CONTROL_SEQUENCE_3 = "_GenericProbe_collector.newRecord(_ResponsetimeProbe_record)";

	public static final int NUM_VARIABLES = 2;
	public static final int NUM_GENERIC_VARIABLES = 3;


	@Override
	protected Class<? extends AbstractEnclosingProbe> getProbeType() {
		return ResponsetimeProbe.class;
	}

	@Override
	protected void checkPreCall(MeasurementData mData) {
		Assert.assertTrue(mData.getRecords().isEmpty());

	}

	@Override
	protected void checkPostCall(MeasurementData mData) {
		Assert.assertFalse(mData.getRecords().isEmpty());
		Assert.assertEquals(ResponseTimeRecord.class, mData.getRecords().get(0).getClass());
		ResponseTimeRecord record = mData.getRecords(ResponseTimeRecord.class).get(0);
		Assert.assertEquals(INSTRUmENTED_METHOD, record.getOperation());
	}


	@Override
	protected int getNumberOfMethodInvokations() {
		return 1;
	}
}
