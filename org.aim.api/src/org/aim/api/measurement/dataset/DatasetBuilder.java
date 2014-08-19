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
import java.util.List;
import java.util.Set;

import org.aim.api.measurement.AbstractRecord;

/**
 * Used to build datasets.
 * 
 * @author Alexander Wert
 * 
 */
public class DatasetBuilder {
	private final List<DatasetRowBuilder> rowBuilder;
	private final List<DatasetRow> rows;

	/**
	 * Constructor.
	 * 
	 * @param recordType
	 *            type of the underlying records
	 */
	public DatasetBuilder(Class<? extends AbstractRecord> recordType) {
		rowBuilder = new ArrayList<>();
		rows = new ArrayList<>();
	}

	/**
	 * Adds a row to the dataset. If a row with the same input parameters
	 * already exists the given row will be merged into the other one.
	 * 
	 * @param row
	 *            row to add
	 */
	public void addRow(DatasetRow row) {
		if (row == null || row.size() <= 0) {
			return;
		}
		for (DatasetRowBuilder wrb : rowBuilder) {
			if (wrb.areValidParameters(row.getParameters()) && wrb.isValidRecord(row.getRecords().get(0))) {
				wrb.addRecords(row.getRecords());
				return;
			}
		}

		DatasetRowBuilder newBuilder = new DatasetRowBuilder(row.getRecordType());
		for (Parameter par : row.getParameters()) {
			newBuilder.addInputParameter(par);
		}
		newBuilder.addRecords(row.getRecords());

		rowBuilder.add(newBuilder);

	}

	/**
	 * For internal use only! Adds a row to the dataset. Assumes that no row
	 * with same input parameters exist yet.
	 * 
	 * @param row
	 *            row to add
	 */
	protected void addRowWithoutChecks(DatasetRow row) {
		if (row == null || row.size() <= 0) {
			return;
		}
		rows.add(row);
	}

	/**
	 * Adds a colelction of rows to the dataset. If a row with the same input
	 * parameters already exists in the dataset the given row will be merged
	 * into the other one.
	 * 
	 * @param dsRows
	 *            rows to add
	 */
	public void addRow(Collection<DatasetRow> dsRows) {
		for (DatasetRow wr : dsRows) {
			addRow(wr);
		}
	}

	/**
	 * adds abstract record.
	 * 
	 * @param record
	 *            record to add
	 * @param parameterNames
	 *            input parameter names
	 * @param parameterValues
	 *            input parameter values
	 */
	public void addRecord(AbstractRecord record, String[] parameterNames, Object[] parameterValues) {

		for (DatasetRowBuilder wrb : rowBuilder) {
			if (wrb.areValidParameters(parameterNames, parameterValues) && wrb.isValidRecord(record)) {
				wrb.addRecord(record);
				return;
			}
		}

		DatasetRowBuilder newBuilder = new DatasetRowBuilder(record.getClass());
		for (int i = 0; i < parameterNames.length; i++) {
			newBuilder.addInputParameter(parameterNames[i], parameterValues[i]);
		}
		newBuilder.addRecord(record);

		rowBuilder.add(newBuilder);

	}

	/**
	 * adds abstract record.
	 * 
	 * @param record
	 *            record to add
	 * @param parameters
	 *            input parameters
	 */
	public void addRecord(AbstractRecord record, Set<Parameter> parameters) {

		for (DatasetRowBuilder wrb : rowBuilder) {
			if (wrb.areValidParameters(parameters) && wrb.isValidRecord(record)) {
				wrb.addRecord(record);
				return;
			}
		}

		DatasetRowBuilder newBuilder = new DatasetRowBuilder(record.getClass());
		for (Parameter par : parameters) {
			newBuilder.addInputParameter(par);
		}

		newBuilder.addRecord(record);

		rowBuilder.add(newBuilder);

	}

	/**
	 * Builds a dataset.
	 * 
	 * @return new {@link Dataset} or null if no rows are available to build a
	 *         dataset
	 */
	public Dataset build() {
		for (DatasetRowBuilder rb : rowBuilder) {
			DatasetRow row = rb.build();
			if (row != null) {
				rows.add(row);
			}

		}
		if (rows.isEmpty()) {
			return null;
		} else {
			return new Dataset(rows);
		}

	}

	/**
	 * @return the recordStructureHash
	 */
	protected int getRecordStructureHash() {
		if (!rowBuilder.isEmpty()) {
			return rowBuilder.get(0).getRecordStructureHash();
		} else if (!rows.isEmpty()) {
			rows.get(0).getRecordStructureHash();
		}
		return 0;

	}
}
