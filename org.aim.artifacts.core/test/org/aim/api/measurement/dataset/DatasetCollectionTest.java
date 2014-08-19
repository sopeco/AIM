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

import org.aim.artifacts.records.ResponseTimeRecord;
import org.aim.artifacts.records.ThreadTracingRecord;
import org.junit.Assert;
import org.junit.Test;

public class DatasetCollectionTest {
	

	@Test
	public void testDatasetCollectionCreation() {
		DatasetCollection dsCollection = SmallDatasetCollection.createDataset();

		Assert.assertEquals(SmallDatasetCollection.NUM_TYPES, dsCollection.getDifferentRecordTypes().size());
		Assert.assertTrue(dsCollection.getDifferentRecordTypes().contains(ResponseTimeRecord.class));
		Assert.assertTrue(dsCollection.getDifferentRecordTypes().contains(ThreadTracingRecord.class));

		Assert.assertEquals(SmallDatasetCollection.NUM_DATASETS, dsCollection.getDataSets().size());
		Assert.assertNotNull(dsCollection.getDataSet(ResponseTimeRecord.class));
		Assert.assertNotNull(dsCollection.getDataSet(ThreadTracingRecord.class));
		Assert.assertEquals(2, dsCollection.getDataSets(ResponseTimeRecord.class).size());
		Assert.assertEquals(1, dsCollection.getDataSets(ThreadTracingRecord.class).size());
		Assert.assertEquals(SmallDatasetCollection.NUM_CONFIGS * SmallDatasetCollection.NUM_RECORDS_PER_CONFIG, dsCollection.getDataSet(ThreadTracingRecord.class).size());
	}

}
