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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aim.aiminterface.entities.measurements.AbstractRecord;

/**
 * Wraps a set of wrapped records forming a dataset.
 * 
 * @author Alexander Wert
 * 
 */
public class Dataset implements IDatasetElement, Iterable<String[]> {
	private final List<DatasetRow> rows;
	private final int size;

	/**
	 * Constructor.
	 * 
	 * @param wrappedRecords
	 *            records to wrap
	 */
	protected Dataset(List<DatasetRow> wrappedRecords) {
		this.rows = wrappedRecords;
		int tempSize = 0;
		for (DatasetRow wr : wrappedRecords) {
			tempSize += wr.size();
		}
		size = tempSize;
	}

	/**
	 * @return the wrappedRecords
	 */
	public List<DatasetRow> getRows() {
		return rows;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getInputParameterNames() {
		if (rows.isEmpty()) {
			return Collections.EMPTY_SET;
		} else {
			return rows.get(0).getInputParameterNames();
		}
	}

	@Override
	public Class<? extends AbstractRecord> getRecordType() {
		if (rows.isEmpty()) {
			return null;
		} else {
			return rows.get(0).getRecordType();
		}
	}

	@Override
	public List<Object> getValues(String name) {
		List<Object> result = new ArrayList<Object>();
		for (DatasetRow rec : rows) {
			result.addAll(rec.getValues(name));
		}
		return result;
	}

	@Override
	public <T> List<T> getValues(String name, Class<T> type) {
		List<T> result = new ArrayList<T>();
		for (DatasetRow rec : rows) {
			result.addAll(rec.getValues(name, type));
		}
		return result;
	}

	@Override
	public List<AbstractRecord> getRecords() {
		List<AbstractRecord> result = new ArrayList<AbstractRecord>();

		for (DatasetRow rec : rows) {
			result.addAll(rec.getRecords());
		}
		return result;
	}

	@Override
	public <T extends AbstractRecord> java.util.List<T> getRecords(Class<T> recordType) {
		List<T> result = new ArrayList<T>();

		for (DatasetRow rec : rows) {
			result.addAll(rec.getRecords(recordType));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getObservationPropertiesNames() {
		if (rows.isEmpty()) {
			return Collections.EMPTY_SET;
		} else {
			return rows.get(0).getObservationPropertiesNames();
		}
	}

	@Override
	public Set<ParameterSelection> getAllParameterConfigurations() {
		Set<ParameterSelection> result = new HashSet<ParameterSelection>();
		for (DatasetRow wr : rows) {
			result.addAll(wr.getAllParameterConfigurations());
		}
		return result;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public String[] getHeader() {
		if (rows.isEmpty()) {
			return null;
		}
		for (DatasetRow wr : rows) {
			String[] header = wr.getHeader();
			if (header != null) {
				return header;
			}
		}
		return null;

	}

	@Override
	public String[] getTypes() {
		if (rows.isEmpty()) {
			return null;
		}
		for (DatasetRow wr : rows) {
			String[] types = wr.getTypes();
			if (types != null) {
				return types;
			}
		}
		return null;

	}

	@Override
	public Set<Object> getValueSet(String name) {
		return new HashSet<Object>(getValues(name));
	}

	@Override
	public <T> Set<T> getValueSet(String name, Class<T> type) {
		return new HashSet<T>(getValues(name, type));
	}

	/**
	 * @return the recordStructureHash
	 */
	protected int getRecordStructureHash() {
		if (rows.isEmpty()) {
			return 0;
		} else {
			return rows.get(0).getRecordStructureHash();
		}
	}

	@Override
	public StringArrayIterator iterator() {
		return new StringArrayIterator();
	}

	/**
	 * Iterates the wrapped record returning string array representations.
	 * 
	 * @author Alexander Wert
	 * 
	 */
	public class StringArrayIterator implements Iterator<String[]> {
		int index = 0;
		DatasetRow.StringArrayIterator recordIterator = null;

		/**
		 * Constructor.
		 */
		public StringArrayIterator() {
			if (!rows.isEmpty()) {
				recordIterator = rows.get(0).iterator();
			}
		}

		@Override
		public boolean hasNext() {
			return recordIterator != null && recordIterator.hasNext();
		}

		@Override
		public String[] next() {
			if (!hasNext()) {
				return null;
			}
			String[] result = recordIterator.next();

			if (!recordIterator.hasNext()) {
				index++;
				if (index < rows.size()) {
					recordIterator = rows.get(index).iterator();
				} else {
					recordIterator = null;
				}
			}

			return result;

		}

		@Override
		public void remove() {
			// not supported
		}

	}

}
