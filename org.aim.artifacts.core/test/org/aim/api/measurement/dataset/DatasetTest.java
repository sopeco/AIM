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

import org.aim.api.measurement.AbstractRecord;
import org.aim.artifacts.records.ResponseTimeRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DatasetTest {

	DatasetBuilder dsBuilder;
	private static final String NUM_USERS_PARAM = "numUsers";

	private static final String OPERATION_1 = "op1";
	private static final String OPERATION_2 = "op2";
	private static final int numRecords = 30;
	private static final int numUsers_1 = 1;
	private static final int numUsers_2 = 5;
	private static final int numUsers_3 = 10;
	private static final int numConfigurations = 6;
	private static final int numInputParams = 3;
	private static final int numObservationParams = 3;
	private List<Long> timestamps = new ArrayList<>();
	private List<Long> responseTimes = new ArrayList<>();

	@Before
	public void createDataset() {
		List<AbstractRecord> records = new ArrayList<>();
		for (int i = 0; i < numRecords / numConfigurations; i++) {
			timestamps.add(new Long(i));
			responseTimes.add(new Long(i * 2));
			records.add(new ResponseTimeRecord(i, OPERATION_1, i * 2));
			records.add(new ResponseTimeRecord(i, OPERATION_2, i * 2));
		}

		dsBuilder = new DatasetBuilder(ResponseTimeRecord.class);

		for (AbstractRecord record : records) {
			Set<Parameter> parameters = new TreeSet<>();
			parameters.add(new Parameter(NUM_USERS_PARAM, numUsers_1));
			dsBuilder.addRecord(record, parameters);

			parameters = new HashSet<>();
			parameters.add(new Parameter(NUM_USERS_PARAM, numUsers_2));
			dsBuilder.addRecord(record, parameters);

			parameters = new HashSet<>();
			parameters.add(new Parameter(NUM_USERS_PARAM, numUsers_3));
			dsBuilder.addRecord(record, parameters);
		}

	}

	@Test
	public void testDatasetCreation() {
		Dataset dataset = dsBuilder.build();

		Assert.assertEquals(ResponseTimeRecord.class, dataset.getRecordType());
		Assert.assertEquals(numRecords, dataset.getRecords().size());
		Assert.assertEquals(numConfigurations, dataset.getAllParameterConfigurations().size());
		Assert.assertEquals(numConfigurations, dataset.getRows().size());

		Set<String> selection = new HashSet<String>();
		selection.add(NUM_USERS_PARAM);
		selection.add(ResponseTimeRecord.PAR_OPERATION);
		selection.add(ResponseTimeRecord.PAR_PROCESS_ID);
		Assert.assertEquals(selection.hashCode() + ResponseTimeRecord.class.hashCode(),
				dataset.getRecordStructureHash());
		String[] header = { ResponseTimeRecord.PAR_CALL_ID, ResponseTimeRecord.PAR_OPERATION,
				ResponseTimeRecord.PAR_PROCESS_ID, ResponseTimeRecord.PAR_RESPONSE_TIME,
				ResponseTimeRecord.PAR_TIMESTAMP, NUM_USERS_PARAM };
		String[] types = { "Long", "String", "String", "Long", "Long", "Integer" };
		Assert.assertArrayEquals(header, dataset.getHeader());
		Assert.assertArrayEquals(types, dataset.getTypes());
		Assert.assertArrayEquals(types, dataset.getTypes());
		Assert.assertEquals(numInputParams, dataset.getInputParameterNames().size());
		Assert.assertEquals(numObservationParams, dataset.getObservationPropertiesNames().size());

		Assert.assertTrue(dataset.getObservationPropertiesNames().contains(ResponseTimeRecord.PAR_RESPONSE_TIME));
		Assert.assertTrue(dataset.getInputParameterNames().contains(NUM_USERS_PARAM));
		Assert.assertTrue(dataset.getValues(ResponseTimeRecord.PAR_TIMESTAMP).containsAll(timestamps));
		Assert.assertTrue(dataset.getValues(ResponseTimeRecord.PAR_RESPONSE_TIME).containsAll(responseTimes));
		Assert.assertTrue(dataset.getValues(ResponseTimeRecord.PAR_TIMESTAMP,
				new ResponseTimeRecord().getType(ResponseTimeRecord.PAR_TIMESTAMP)).containsAll(timestamps));
		Assert.assertTrue(dataset.getValues(ResponseTimeRecord.PAR_RESPONSE_TIME,
				new ResponseTimeRecord().getType(ResponseTimeRecord.PAR_RESPONSE_TIME)).containsAll(responseTimes));

	}

}
