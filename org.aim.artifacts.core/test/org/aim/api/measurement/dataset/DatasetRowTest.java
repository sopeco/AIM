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
package org.aim.api.measurement.dataset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.aim.artifacts.records.MemoryRecord;
import org.aim.artifacts.records.ResponseTimeRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DatasetRowTest {
	DatasetRowBuilder rowBuilder;

	List<Long> timestamps;
	List<Long> responseTimes;
	private static final String OPERATION = "op";
	private static final String PID = "pid";

	@Before
	public void createSimpleDatasetRow() {
		rowBuilder = new DatasetRowBuilder(ResponseTimeRecord.class);

		timestamps = new ArrayList<Long>();
		responseTimes = new ArrayList<Long>();
		for (int i = 0; i < 10; i++) {
			timestamps.add(new Long(i));
			responseTimes.add(new Long(i * 100));
			
			ResponseTimeRecord record = new ResponseTimeRecord(i, OPERATION, i * 100);
			record.setProcessId(PID);
			rowBuilder.addRecord(record);
		}

	}

	@Test
	public void testDatasetRowCreation() {
		DatasetRow row = rowBuilder.build();
		Assert.assertEquals(ResponseTimeRecord.class, row.getRecordType());
		Assert.assertEquals(2, row.getInputParameterNames().size());
		Assert.assertEquals(3, row.getObservationPropertiesNames().size());

		Assert.assertTrue(row.getObservationPropertiesNames().contains(ResponseTimeRecord.PAR_RESPONSE_TIME));
		Assert.assertTrue(row.getValues(ResponseTimeRecord.PAR_TIMESTAMP).containsAll(timestamps));
		Assert.assertTrue(row.getValues(ResponseTimeRecord.PAR_RESPONSE_TIME).containsAll(responseTimes));
		Assert.assertTrue(row.getValues(ResponseTimeRecord.PAR_TIMESTAMP,
				new ResponseTimeRecord().getType(ResponseTimeRecord.PAR_TIMESTAMP)).containsAll(timestamps));
		Assert.assertTrue(row.getValues(ResponseTimeRecord.PAR_RESPONSE_TIME,
				new ResponseTimeRecord().getType(ResponseTimeRecord.PAR_RESPONSE_TIME)).containsAll(responseTimes));
	}

	@Test
	public void testDatasetRowParameters() {
		String testParameterName = "testParameter";
		Long testParameterValue = 5L;
		rowBuilder.addInputParameter(testParameterName, testParameterValue);
		DatasetRow row = rowBuilder.build();
		Assert.assertEquals(ResponseTimeRecord.class, row.getRecordType());
		Assert.assertEquals(3, row.getInputParameterNames().size());
		Assert.assertEquals(3, row.getObservationPropertiesNames().size());

		Set<String> selection = new HashSet<String>();
		selection.add(testParameterName);
		selection.add(ResponseTimeRecord.PAR_OPERATION);
		selection.add(ResponseTimeRecord.PAR_PROCESS_ID);

		Assert.assertEquals(row.getRecordStructureHash(), selection.hashCode() + ResponseTimeRecord.class.hashCode());

		String[] header = { ResponseTimeRecord.PAR_CALL_ID, ResponseTimeRecord.PAR_OPERATION,
				ResponseTimeRecord.PAR_PROCESS_ID, ResponseTimeRecord.PAR_RESPONSE_TIME,
				ResponseTimeRecord.PAR_TIMESTAMP, testParameterName };
		String[] types = { "Long", "String", "String", "Long", "Long", "Long" };
		String[] values = { "0", OPERATION, PID, "100", "1", testParameterValue.toString() };
		Assert.assertArrayEquals(header, row.getHeader());
		Assert.assertArrayEquals(types, row.getTypes());

		Set<Parameter> parameters = new TreeSet<>();
		parameters.add(new Parameter(testParameterName, testParameterValue));
		Assert.assertArrayEquals(values, DatasetRow.getValueArray(row.getRecords().get(1), parameters));

		Assert.assertTrue(row.getObservationPropertiesNames().contains(ResponseTimeRecord.PAR_RESPONSE_TIME));
		Assert.assertTrue(row.getValues(ResponseTimeRecord.PAR_TIMESTAMP).containsAll(timestamps));
		Assert.assertTrue(row.getValues(ResponseTimeRecord.PAR_RESPONSE_TIME).containsAll(responseTimes));
		Assert.assertTrue(row.getValues(ResponseTimeRecord.PAR_TIMESTAMP,
				new ResponseTimeRecord().getType(ResponseTimeRecord.PAR_TIMESTAMP)).containsAll(timestamps));
		Assert.assertTrue(row.getValues(ResponseTimeRecord.PAR_RESPONSE_TIME,
				new ResponseTimeRecord().getType(ResponseTimeRecord.PAR_RESPONSE_TIME)).containsAll(responseTimes));
	}

	@Test
	public void testWrappedRecordExceptions() {
		boolean thrown = false;
		try {
			rowBuilder.addRecord(null);
		} catch (Exception e) {
			thrown = true;
		}
		Assert.assertTrue(thrown);
		thrown = false;

		try {
			rowBuilder.addRecord(new MemoryRecord(1, 123));
		} catch (Exception e) {
			thrown = true;
		}
		Assert.assertTrue(thrown);
		thrown = false;

		try {
			rowBuilder.addInputParameter("invalidParameter", new Object());
		} catch (Exception e) {
			thrown = true;
		}
		Assert.assertTrue(thrown);
		thrown = false;

		try {
			rowBuilder.addInputParameter(ResponseTimeRecord.PAR_OPERATION, "abc");
		} catch (Exception e) {
			thrown = true;
		}
		Assert.assertTrue(thrown);
		thrown = false;

		try {
			rowBuilder.addInputParameter(ResponseTimeRecord.PAR_OPERATION, "op");
		} catch (Exception e) {
			thrown = true;
		}
		Assert.assertFalse(thrown);
	}

}
