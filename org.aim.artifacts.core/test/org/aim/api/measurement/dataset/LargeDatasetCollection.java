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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.aim.api.measurement.AbstractRecord;
import org.aim.artifacts.records.ResponseTimeRecord;
import org.aim.artifacts.records.ThreadTracingRecord;

public class LargeDatasetCollection {
	protected static final String NUM_USERS_PARAM = "numUsers";

	protected static final String OPERATION_1 = "op1";
	protected static final String OPERATION_2 = "op2";
	protected static final String OPERATION_3 = "op3";
	protected static final String OPERATION_4 = "op4";
	protected static int numRecords;

	protected static final int NUMUSERS_1 = 4;
	protected static final int NUM_RECORDS_PER_CONFIG = 100;
	protected static final int NUM_CONFIGS = 6;

	protected static final int NUM_TYPES = 2;
	protected static final int NUM_DATASETS = 3;
	protected static final int NUM_INPUT_PARAMS = 2;
	protected static final int NUM_OBS_PARAMS = 3;
	protected static final List<Long> TIMESTAMPS = new ArrayList<>();
	protected static final List<Long> RESPONSE_TIMES = new ArrayList<>();

	protected static final Set<Parameter> additionalParameters = new TreeSet<Parameter>();
	static {
		additionalParameters.add(new Parameter(NUM_USERS_PARAM, NUMUSERS_1));
	}

	public static DatasetCollection createDataset() {
		List<AbstractRecord> records = new ArrayList<>();
		for (int i = 0; i < NUM_RECORDS_PER_CONFIG; i++) {
			TIMESTAMPS.add(new Long(i));
			RESPONSE_TIMES.add(new Long(i * 2));
			records.add(new ResponseTimeRecord(i, OPERATION_1, i * 2));
			records.add(new ResponseTimeRecord(i, OPERATION_2, i * 2));
			records.add(new ResponseTimeRecord(i, OPERATION_3, i * 2));
			records.add(new ResponseTimeRecord(i, OPERATION_4, i * 2));

			ThreadTracingRecord ttr = new ThreadTracingRecord();
			ttr.setCallId(10);
			ttr.setExitNanoTime(10);
			ttr.setOperation(OPERATION_1);
			ttr.setThreadId(i * 2);
			ttr.setTimeStamp(i);
			records.add(ttr);
			ttr = new ThreadTracingRecord();
			ttr.setCallId(10);
			ttr.setExitNanoTime(10);
			ttr.setOperation(OPERATION_2);
			ttr.setThreadId(i * 2);
			ttr.setTimeStamp(i);
			records.add(ttr);
			ttr = new ThreadTracingRecord();
			ttr.setCallId(10);
			ttr.setExitNanoTime(10);
			ttr.setOperation(OPERATION_3);
			ttr.setThreadId(i * 2);
			ttr.setTimeStamp(i);
			records.add(ttr);
			ttr = new ThreadTracingRecord();
			ttr.setCallId(10);
			ttr.setExitNanoTime(10);
			ttr.setOperation(OPERATION_4);
			ttr.setThreadId(i * 2);
			ttr.setTimeStamp(i);
			records.add(ttr);
		}

		DatasetCollectionBuilder dscBuilder = new DatasetCollectionBuilder();
		numRecords = records.size();
		for (AbstractRecord record : records) {
			Set<Parameter> parameters = new TreeSet<>();
			parameters.add(new Parameter(NUM_USERS_PARAM, NUMUSERS_1));
			dscBuilder.addRecord(record, parameters);
		}
		return dscBuilder.build();
	}

}
