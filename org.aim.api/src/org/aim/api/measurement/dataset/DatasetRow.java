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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.aim.api.measurement.AbstractRecord;
import org.lpe.common.util.LpeSupportedTypes;

/**
 * A Wrapped record wraps a set of records which have the same input parameter
 * configuration values.
 * 
 * @author Alexander Wert
 */
public class DatasetRow implements IDatasetElement, Iterable<String[]> {

	private final TreeSet<Parameter> parameters;
	private int recordStructureHash;
	private final List<AbstractRecord> records;
	private final Class<? extends AbstractRecord> recordType;

	/**
	 * Constructor. For internal use only
	 * 
	 * @param recordType
	 *            type of the underlying records of the created wrappedRecord
	 * @param parameters
	 *            additional input parameters for this wrapped record
	 * @param records
	 *            records to add
	 */
	protected DatasetRow(final Class<? extends AbstractRecord> recordType, final TreeSet<Parameter> parameters,
			final List<AbstractRecord> records) {
		this.recordType = recordType;
		this.parameters = parameters;
		if (records != null && !records.isEmpty()) {
			this.records = records;
			AbstractRecord record = records.get(0);

			List<String> parNames = record.getNonMetricParameterNames();
			List<Object> parValues = record.getNonMetricValues();
			for (int i = 0; i < parNames.size(); i++) {
				addParameter(new Parameter(parNames.get(i), parValues.get(i)));
			}
		} else {
			this.records = new ArrayList<AbstractRecord>();
		}
		setRecordStructureHash(getInputParameterNames().hashCode() + recordType.hashCode());

	}

	@Override
	public Set<ParameterSelection> getAllParameterConfigurations() {
		Set<ParameterSelection> result = new HashSet<ParameterSelection>();
		result.add(new ParameterSelection().select(getParameters()));
		return result;
	}

	@Override
	public Set<String> getInputParameterNames() {
		Set<String> result = new HashSet<String>();
		for (Parameter p : getParameters()) {
			result.add(p.getName());
		}
		return result;
	}

	@Override
	public Class<? extends AbstractRecord> getRecordType() {
		return recordType;
	}

	@Override
	public List<Object> getValues(String name) {
		List<Object> result = new ArrayList<Object>();
		for (Parameter par : getParameters()) {
			if (par.getName().equals(name)) {
				result.add(par.getValue());
			}
		}
		if (result.isEmpty()) {
			for (AbstractRecord rec : getRecords()) {
				result.add(rec.getValue(name));
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S> List<S> getValues(String name, Class<S> type) {
		List<S> result = new ArrayList<S>();
		boolean first = true;

		for (Parameter par : getParameters()) {
			if (par.getName().equals(name)) {
				result.add(par.getValue(type));
			}
		}
		if (result.isEmpty()) {
			for (AbstractRecord rec : getRecords()) {
				if (first) {
					String typeStr = rec.getType(name).getSimpleName();
					if (!LpeSupportedTypes.get(typeStr).equals(LpeSupportedTypes.get(type.getSimpleName()))) {
						throw new IllegalArgumentException("Invalid type for observation property " + name);
					}
					first = false;
				}
				result.add((S) rec.getValue(name));
			}
		}
		return result;
	}

	@Override
	public Set<String> getObservationPropertiesNames() {
		try {
			if (getRecords().isEmpty()) {
				return recordType.newInstance().getMetricParameterNames();
			} else {
				return getRecords().get(0).getMetricParameterNames();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<AbstractRecord> getRecords() {
		return records;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractRecord> List<T> getRecords(Class<T> type) {
		if (!type.equals(getRecordType())) {
			return new ArrayList<T>();
		}
		List<T> resultList = new ArrayList<>();
		for (AbstractRecord record : getRecords()) {
			resultList.add((T) record);
		}
		return resultList;
	}

	@Override
	public int size() {
		return records.size();
	}

	@Override
	public String[] getHeader() {
		if (records.isEmpty()) {
			return null;
		}
		return getHeader(records.get(0), getParameters());

	}

	@Override
	public String[] getTypes() {
		if (records.isEmpty()) {
			return null;
		}
		return getTypes(records.get(0), getParameters());
	}

	@Override
	public Set<Object> getValueSet(String name) {
		return new HashSet<Object>(getValues(name));
	}

	@Override
	public <S> Set<S> getValueSet(String name, Class<S> type) {
		return new HashSet<S>(getValues(name, type));
	}

	@Override
	public StringArrayIterator iterator() {
		return new StringArrayIterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((recordType == null) ? 0 : recordType.hashCode());
		result = prime * result + ((records == null) ? 0 : records.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DatasetRow other = (DatasetRow) obj;

		if (recordType == null) {
			if (other.recordType != null) {
				return false;
			}
		} else if (!recordType.equals(other.recordType)) {
			return false;
		}
		if (records == null) {
			if (other.records != null) {
				return false;
			}
		} else if (!records.equals(other.records)) {
			return false;
		}
		return true;
	}

	/**
	 * @param parameter
	 *            the parameter to add to the wrapper
	 */
	private void addParameter(final Parameter parameter) {
		getParameters().add(parameter);

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

	/**
	 * @return the input parameters
	 */
	public Set<Parameter> getParameters() {
		return parameters;
	}

	/**
	 * Iterates the wrapped record returning string array representations.
	 * 
	 * @author Alexander Wert
	 * 
	 */
	public class StringArrayIterator implements Iterator<String[]> {
		int index = 0;
		String[] extParValues = null;

		@Override
		public boolean hasNext() {
			return index + 1 <= records.size();
		}

		@Override
		public String[] next() {
			if (!hasNext()) {
				return null;
			}
			AbstractRecord abstRec = records.get(index);
			String[] abstRecArray = abstRec.toStringArray();

			if (extParValues == null) {
				extParValues = getExternalParValues(abstRec, parameters);
			}
			index++;
			return concatArrays(abstRecArray, extParValues);
		}

		@Override
		public void remove() {
			// not supported
		}

	}

	/**
	 * Creates the String array header for the given record and parameters.
	 * 
	 * @param record
	 *            record
	 * @param parameters
	 *            set of parameters
	 * @return string array representing the header
	 */
	public static String[] getHeader(AbstractRecord record, Set<Parameter> parameters) {

		List<String> recParNames = record.getNonMetricParameterNames();
		List<String> extParNamesList = new ArrayList<String>();
		for (Parameter par : parameters) {
			if (!recParNames.contains(par.getName())) {
				extParNamesList.add(par.getName());
			}
		}

		return concatArrays(record.getAllParameterNames().toArray(new String[0]),
				extParNamesList.toArray(new String[0]));
	}

	/**
	 * Creates the String array types header for the given record and
	 * parameters.
	 * 
	 * @param record
	 *            record
	 * @param parameters
	 *            set of parameters
	 * @return string array representing the type header
	 */
	public static String[] getTypes(AbstractRecord record, Set<Parameter> parameters) {
		List<String> recParTypes = new ArrayList<String>();

		for (String parName : record.getAllParameterNames()) {
			recParTypes.add(LpeSupportedTypes.get(record.getType(parName).getSimpleName()).toString());
		}

		List<String> recParNames = record.getNonMetricParameterNames();
		List<String> extParTypeList = new ArrayList<String>();
		for (Parameter par : parameters) {
			if (!recParNames.contains(par.getName())) {
				extParTypeList.add(par.getValue().getClass().getSimpleName());
			}
		}

		return concatArrays(recParTypes.toArray(new String[0]), extParTypeList.toArray(new String[0]));
	}

	/**
	 * 
	 * @param record
	 *            record
	 * @param parameters
	 *            parameter set
	 * @return string array representation of the values
	 */
	public static String[] getValueArray(AbstractRecord record, Set<Parameter> parameters) {

		String[] abstRecArray = record.toStringArray();

		String[] extParValues = getExternalParValues(record, parameters);

		return concatArrays(abstRecArray, extParValues);

	}

	private static String[] concatArrays(String[] array1, String[] array2) {

		int aLen = array1.length;
		int bLen = array2.length;
		String[] result = new String[aLen + bLen];
		System.arraycopy(array1, 0, result, 0, aLen);
		System.arraycopy(array2, 0, result, aLen, bLen);
		return result;

	}

	private static String[] getExternalParValues(AbstractRecord abstRec, Set<Parameter> parameters) {
		List<String> recParNames = abstRec.getNonMetricParameterNames();
		List<String> extParValuesList = new ArrayList<String>();
		for (Parameter par : parameters) {
			if (!recParNames.contains(par.getName())) {
				extParValuesList.add(par.getValue().toString());
			}
		}
		return extParValuesList.toArray(new String[0]);
	}

}
