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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aim.aiminterface.entities.measurements.AbstractRecord;

/**
 * Used to build a collection of datasets.
 * 
 * @author Alexander Wert
 * 
 */
public class DatasetCollectionBuilder {
	private final List<DatasetBuilder> dsBuilder;
	private final List<Dataset> datasets;

	/**
	 * Constructor.
	 */
	public DatasetCollectionBuilder() {
		dsBuilder = new ArrayList<>();
		datasets = new ArrayList<>();
	}

	/**
	 * Adds a record to the dataset collection, assigning it to the right
	 * dataset.
	 * 
	 * @param record
	 *            record to add
	 * @param inputParameters
	 *            additional input parameters to add
	 */
	public void addRecord(final AbstractRecord record, final Set<Parameter> inputParameters) {
		final List<String> names = record.getNonMetricParameterNames();

		for (final Parameter par : inputParameters) {
			names.add(par.getName());
		}

		final Set<String> parNames = new HashSet<>();
		parNames.addAll(names);

		final int recordStructureHash = parNames.hashCode() + record.getClass().hashCode();

		for (final DatasetBuilder dsb : dsBuilder) {
			if (dsb.getRecordStructureHash() == recordStructureHash) {
				dsb.addRecord(record, inputParameters);
				return;
			}
		}

		final DatasetBuilder newBuilder = new DatasetBuilder(record.getClass());
		newBuilder.addRecord(record, inputParameters);
		dsBuilder.add(newBuilder);

	}

	/**
	 * 
	 * @param dataSet
	 *            dataset to add
	 */
	public void addDataSet(final Dataset dataSet) {
		for (final DatasetBuilder dsb : dsBuilder) {
			if (dsb.getRecordStructureHash() == dataSet.getRecordStructureHash()) {
				dsb.addRow(dataSet.getRows());
				return;
			}
		}
		final DatasetBuilder newBuilder = new DatasetBuilder(dataSet.getRecordType());
		newBuilder.addRow(dataSet.getRows());
		dsBuilder.add(newBuilder);
	}

	/**
	 * For internal use only!
	 * 
	 * @param dataSet
	 *            dataset to add
	 */
	protected void addDataSetWithoutChecks(final Dataset dataSet) {
		datasets.add(dataSet);
	}

	/**
	 * 
	 * @param dsCollection
	 *            datasets to add
	 */
	public void addDataSets(final Collection<Dataset> dsCollection) {
		for (final Dataset wds : dsCollection) {
			addDataSet(wds);
		}
	}

	/**
	 * 
	 * @param dsCollection
	 *            wrapped measurement data to add
	 */
	public void addDataSets(final DatasetCollection dsCollection) {
		for (final Dataset wds : dsCollection.getDataSets()) {
			addDataSet(wds);
		}
	}

	/**
	 * Builds a collection of datasets.
	 * 
	 * @return a new {@link DatasetCollection} or null if no datastes are
	 *         available to build the collection from
	 */
	public DatasetCollection build() {
		for (final DatasetBuilder dsb : dsBuilder) {
			final Dataset dataset = dsb.build();
			if (dataset != null) {
				datasets.add(dataset);
			}
		}

		if (datasets.isEmpty()) {
			return new DatasetCollection(Collections.<Dataset> emptyList());
		} else {
			return new DatasetCollection(datasets);
		}
	}
}
