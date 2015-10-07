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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.aim.aiminterface.entities.measurements.AbstractRecord;

/**
 * Builder for wrapped records.
 * 
 * @author Alexander Wert
 * 
 */
public class DatasetRowBuilder {

	private List<AbstractRecord> records;
	private TreeSet<Parameter> parameters;
	private Class<? extends AbstractRecord> recordType;
	private List<String> recordNonMetricParameterNames;
	private int recordStructureHash;

	/**
	 * Construcotr.
	 * 
	 * @param recordType
	 *            type of the records for which the wrapped record should be
	 *            built.
	 */
	public DatasetRowBuilder(Class<? extends AbstractRecord> recordType) {
		this.recordType = recordType;
		records = new ArrayList<>();
		parameters = new TreeSet<>();
		updateStructureHash();
	}

	/**
	 * Adds a new input parameter to the wrapped record.
	 * 
	 * @param name
	 *            parameter name
	 * @param value
	 *            parameter value
	 */
	public void addInputParameter(String name, Object value) {
		addInputParameter(new Parameter(name, value));
	}

	/**
	 * Adds a new input parameter to the wrapped record.
	 * 
	 * @param parameter
	 *            parameter to add
	 */
	public void addInputParameter(Parameter parameter) {
		if (!validParameter(parameter.getName(), parameter.getValue())) {
			throw new IllegalArgumentException("Invalid input parameter!");
		}
		parameters.add(parameter);

		updateStructureHash();
	}

	/**
	 * For internal use only. Adds an input parameter without checking its
	 * validity.
	 * 
	 * @param parameter
	 *            parameter to add
	 */
	protected void addInputParameterWithoutChecks(Parameter parameter) {
		parameters.add(parameter);
		updateStructureHash();
	}

	private void updateStructureHash() {
		Set<String> names = new HashSet<>();
		for (Parameter p : parameters) {
			names.add(p.getName());
		}
		setRecordStructureHash(names.hashCode() + recordType.hashCode());
	}

	/**
	 * Adds a record to this wrapped record. Note: this method assumes that all
	 * non metric parameter values match the one of this wrapped record. If this
	 * is not the case the behaviour of this wrapped record is not predictible.
	 * Use the method checkParameters(...) to check whethr your record is
	 * conform to this wrapped record.
	 * 
	 * @param record
	 *            the record to add to the wrapper
	 */
	public void addRecord(final AbstractRecord record) {
		if (record == null) {
			throw new IllegalArgumentException("Passed record is null!");
		} else if (!recordType.equals(record.getClass())) {
			throw new IllegalArgumentException("Passed Record type " + record.getClass().getName()
					+ " is not compatibel with the wrapper record type " + recordType.getName());
		}

		if (records.isEmpty()) {

			List<String> parNames = record.getNonMetricParameterNames();
			List<Object> parValues = record.getNonMetricValues();
			for (int i = 0; i < parNames.size(); i++) {
				addInputParameter(parNames.get(i), parValues.get(i));
			}

		}

		records.add(record);
	}

	/**
	 * FOr internal use only! Adds a record without checking its validity for
	 * this dataset row.
	 * 
	 * @param record
	 *            record to add
	 */
	protected void addRecordWithoutChecks(final AbstractRecord record) {
		records.add(record);
	}

	/**
	 * checks whether this record is valid for the wrapped record.
	 * 
	 * @param record
	 *            record to check
	 * @return true if valid
	 */
	public boolean isValidRecord(final AbstractRecord record) {
		if (records.isEmpty() && recordType.equals(record.getClass())) {
			return true;
		}
		if (recordNonMetricParameterNames == null) {
			recordNonMetricParameterNames = record.getNonMetricParameterNames();
		}

		List<Object> parValues = record.getNonMetricValues();
		for (int i = 0; i < recordNonMetricParameterNames.size(); i++) {
			if (!validExistingParameter(recordNonMetricParameterNames.get(i), parValues.get(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * checks parameters.
	 * 
	 * @param parameterNames
	 *            names
	 * @param parameterValues
	 *            values
	 * @return true if parameters are valid for this wrapped record
	 */
	public boolean areValidParameters(String[] parameterNames, Object[] parameterValues) {
		if (parameterNames.length != parameterValues.length) {
			throw new IllegalArgumentException("Number of parameter names must match number of parameter values!");
		}
		for (int i = 0; i < parameterNames.length; i++) {
			if (!validExistingParameter(parameterNames[i], parameterValues[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * checks parameters.
	 * 
	 * @param parameters
	 *            parameters
	 * @return true if parameters are valid for this wrapped record
	 */
	public boolean areValidParameters(Set<Parameter> parameters) {
		for (Parameter par : parameters) {
			if (!validExistingParameter(par.getName(), par.getValue())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Adds records to the wrapped record.
	 * 
	 * @param records
	 *            records to add
	 */
	public void addRecords(final Collection<AbstractRecord> records) {
		for (AbstractRecord rec : records) {
			addRecord(rec);
		}
	}

	/**
	 * 
	 * @return a new {@link DatasetRow} or null if no records are available to
	 *         build a row
	 */
	public DatasetRow build() {
		if (records.isEmpty()) {
			return null;
		} else {
			return new DatasetRow(recordType, parameters, records);
		}

	}

	/**
	 * 
	 * @param parameter
	 *            parameter to check
	 * @return true, if parameter with that name does not exist yet in the
	 *         wrapped record to build or if the parameter with the same value
	 *         already eists.
	 */
	private boolean validParameter(final String name, final Object value) {
		for (Parameter p : parameters) {
			if (p.getName().equals(name) && !p.getValue().equals(value)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param parameter
	 *            parameter to check
	 * @return true, if parameter with that name already exists and has the same
	 *         value
	 */
	private boolean validExistingParameter(final String name, final Object value) {
		for (Parameter p : parameters) {
			if (p.getName().equals(name)) {
				return p.getValue().equals(value);

			}
		}
		return false;
	}

	/**
	 * @return the recordStructureHash
	 */
	protected int getRecordStructureHash() {
		return recordStructureHash;
	}

	/**
	 * @param recordStructureHash
	 *            the recordStructureHash to set
	 */
	protected void setRecordStructureHash(int recordStructureHash) {
		this.recordStructureHash = recordStructureHash;
	}

}
