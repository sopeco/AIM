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

import junit.framework.Assert;

import org.aim.artifacts.records.ResponseTimeRecord;
import org.aim.artifacts.records.ThreadTracingRecord;
import org.junit.Test;

public class SelectionTest {
	@Test
	public void testRowSelection() {
		DatasetCollection dsCollection = SmallDatasetCollection.createDataset();
		DatasetRow row = dsCollection.getRows().get(0);

		ParameterSelection parSelectionOperation = new ParameterSelection();
		parSelectionOperation.select(ResponseTimeRecord.PAR_OPERATION, SmallDatasetCollection.OPERATION_1);

		DatasetRow selectedRow = parSelectionOperation.applyTo(row);
		Assert.assertEquals(row.size(), selectedRow.size());

		parSelectionOperation = new ParameterSelection();
		parSelectionOperation.select(ResponseTimeRecord.PAR_OPERATION, SmallDatasetCollection.OPERATION_2);
		selectedRow = parSelectionOperation.applyTo(row);
		Assert.assertNull(selectedRow);

		parSelectionOperation = new ParameterSelection();
		parSelectionOperation.select(ResponseTimeRecord.PAR_RESPONSE_TIME, 4L);
		selectedRow = parSelectionOperation.applyTo(row);
		Assert.assertEquals(1, selectedRow.size());

		parSelectionOperation = new ParameterSelection();
		parSelectionOperation.largerOrEquals(ResponseTimeRecord.PAR_RESPONSE_TIME, 4L);
		selectedRow = parSelectionOperation.applyTo(row);
		Assert.assertEquals(4, selectedRow.size());

		parSelectionOperation = new ParameterSelection();
		parSelectionOperation.smallerOrEquals(ResponseTimeRecord.PAR_RESPONSE_TIME, 4L);
		selectedRow = parSelectionOperation.applyTo(row);
		Assert.assertEquals(3, selectedRow.size());

		parSelectionOperation = new ParameterSelection();
		parSelectionOperation.between(ResponseTimeRecord.PAR_RESPONSE_TIME, 4L, 6L);
		selectedRow = parSelectionOperation.applyTo(row);
		Assert.assertEquals(2, selectedRow.size());
	}

	@Test
	public void testDatasetSelection() {
		DatasetCollection dsCollection = SmallDatasetCollection.createDataset();
		Dataset dataset = dsCollection.getDataSet(ThreadTracingRecord.class);

		ParameterSelection parSelectionOperation = new ParameterSelection();
		parSelectionOperation.select(ThreadTracingRecord.PAR_OPERATION, SmallDatasetCollection.OPERATION_1);

		Dataset selectedDataset = parSelectionOperation.applyTo(dataset);
		Assert.assertEquals(18, selectedDataset.size());

		parSelectionOperation = new ParameterSelection();
		parSelectionOperation.select(ThreadTracingRecord.PAR_OPERATION, SmallDatasetCollection.OPERATION_2);
		selectedDataset = parSelectionOperation.applyTo(dataset);
		Assert.assertEquals(18, selectedDataset.size());

		parSelectionOperation.select(ThreadTracingRecord.PAR_THREAD_ID, 4L);
		selectedDataset = parSelectionOperation.applyTo(dataset);
		Assert.assertEquals(3, selectedDataset.size());

		parSelectionOperation = new ParameterSelection();
		parSelectionOperation.select(ThreadTracingRecord.PAR_THREAD_ID, 4L);
		selectedDataset = parSelectionOperation.applyTo(dataset);
		Assert.assertEquals(6, selectedDataset.size());

		parSelectionOperation = new ParameterSelection();
		parSelectionOperation.largerOrEquals(ThreadTracingRecord.PAR_THREAD_ID, 4L);
		selectedDataset = parSelectionOperation.applyTo(dataset);
		Assert.assertEquals(6 * 4, selectedDataset.size());

		parSelectionOperation = new ParameterSelection();
		parSelectionOperation.smallerOrEquals(ThreadTracingRecord.PAR_THREAD_ID, 4L);
		selectedDataset = parSelectionOperation.applyTo(dataset);
		Assert.assertEquals(6 * 3, selectedDataset.size());

		parSelectionOperation = new ParameterSelection();
		parSelectionOperation.between(ThreadTracingRecord.PAR_THREAD_ID, 4L, 6L);
		selectedDataset = parSelectionOperation.applyTo(dataset);
		Assert.assertEquals(6 * 2, selectedDataset.size());

		dataset = dsCollection.getDataSet(ResponseTimeRecord.class);
		parSelectionOperation = new ParameterSelection();
		parSelectionOperation.select(ThreadTracingRecord.PAR_THREAD_ID, 4L);
		selectedDataset = parSelectionOperation.applyTo(dataset);
		Assert.assertNull(selectedDataset);
	}

	@Test
	public void testDatasetCollectionSelection() {
		DatasetCollection dsCollection = SmallDatasetCollection.createDataset();

		ParameterSelection parSelectionOperation = new ParameterSelection();
		parSelectionOperation.select(ThreadTracingRecord.PAR_OPERATION, SmallDatasetCollection.OPERATION_1);

		DatasetCollection selectedCollection = parSelectionOperation.applyTo(dsCollection);
		Assert.assertEquals(72, selectedCollection.size());

		parSelectionOperation = new ParameterSelection();
		parSelectionOperation.select(ThreadTracingRecord.PAR_OPERATION, SmallDatasetCollection.OPERATION_2);
		selectedCollection = parSelectionOperation.applyTo(dsCollection);
		Assert.assertEquals(72, selectedCollection.size());

		parSelectionOperation.select(ThreadTracingRecord.PAR_THREAD_ID, 4L);
		selectedCollection = parSelectionOperation.applyTo(dsCollection);
		Assert.assertEquals(3, selectedCollection.size());

		parSelectionOperation = new ParameterSelection();
		parSelectionOperation.select(SmallDatasetCollection.NUM_USERS_PARAM, SmallDatasetCollection.NUMUSERS_2);
		selectedCollection = parSelectionOperation.applyTo(dsCollection);
		Assert.assertEquals(48, selectedCollection.size());

		parSelectionOperation = new ParameterSelection();
		parSelectionOperation.select(SmallDatasetCollection.NUM_USERS_PARAM, SmallDatasetCollection.NUMUSERS_2);
		parSelectionOperation.select(ThreadTracingRecord.PAR_OPERATION, SmallDatasetCollection.OPERATION_2);
		selectedCollection = parSelectionOperation.applyTo(dsCollection);
		Assert.assertEquals(24, selectedCollection.size());

		parSelectionOperation = new ParameterSelection();
		parSelectionOperation.between(SmallDatasetCollection.NUM_USERS_PARAM, SmallDatasetCollection.NUMUSERS_2,
				SmallDatasetCollection.NUMUSERS_3);
		parSelectionOperation.select(ThreadTracingRecord.PAR_OPERATION, SmallDatasetCollection.OPERATION_2);
		selectedCollection = parSelectionOperation.applyTo(dsCollection);
		Assert.assertEquals(24 * 2, selectedCollection.size());

		parSelectionOperation = new ParameterSelection();
		parSelectionOperation.select(ThreadTracingRecord.PAR_THREAD_ID, 4L);
		selectedCollection = parSelectionOperation.applyTo(dsCollection);
		Assert.assertEquals(6, selectedCollection.size());
	}

}
