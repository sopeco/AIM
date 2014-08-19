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
package org.aim.api.measurement.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.aim.api.measurement.AbstractRecord;
import org.aim.api.measurement.MeasurementData;
import org.aim.api.measurement.dataset.DatasetCollection;
import org.aim.api.measurement.dataset.DatasetCollectionBuilder;
import org.aim.api.measurement.dataset.Parameter;

/**
 * Utility functions for measurement data, records, etc.
 * 
 * @author Alexander Wert
 * 
 */
public final class MeasurementDataUtils {

	/**
	 * Private construcotr due to utility class.
	 */
	private MeasurementDataUtils() {
	}

	/**
	 * Sorts the given records increasingly by the values of the parameters
	 * specified by the parameter names in the order the parameters are passed.
	 * 
	 * @param records
	 *            records to sort
	 * @param parameterNames
	 *            names of the parameter to sort by
	 */
	public static void sortRecordsAscending(final List<? extends AbstractRecord> records,
			final String... parameterNames) {
		if (records == null) {
			throw new IllegalArgumentException("Cannot sort a list which is null!");
		}

		Collections.sort(records, new Comparator<AbstractRecord>() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public int compare(AbstractRecord record1, AbstractRecord record2) {
				for (String parName : parameterNames) {
					Comparable value1 = (Comparable) record1.getValue(parName);
					Comparable value2 = (Comparable) record2.getValue(parName);
					int compareValue = 0;
					if (value1 == null && value2 != null) {
						compareValue = -1;
					} else if (value1 != null && value2 == null) {
						compareValue = 1;
					} else if (value1 != null && value2 != null) {
						compareValue = value1.compareTo(value2);
					}

					if (compareValue != 0) {
						return compareValue;
					}
				}
				return 0;
			}
		});
	}

	/**
	 * Sorts the given records increasingly by the values of the parameters
	 * specified by the parameter names in the order the parameters are passed.
	 * 
	 * @param records
	 *            records to sort
	 * @param parameterNames
	 *            names of the parameter to sort by
	 */
	public static void sortRecordsDescending(final List<? extends AbstractRecord> records,
			final String... parameterNames) {
		if (records == null) {
			throw new IllegalArgumentException("Cannot sort a list which is null!");
		}

		Collections.sort(records, new Comparator<AbstractRecord>() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public int compare(AbstractRecord record1, AbstractRecord record2) {
				for (String parName : parameterNames) {
					Comparable value1 = (Comparable) record1.getValue(parName);
					Comparable value2 = (Comparable) record2.getValue(parName);
					int compareValue = 0;
					if (value1 == null && value2 != null) {
						compareValue = -1;
					} else if (value1 != null && value2 == null) {
						compareValue = 1;
					} else if (value1 != null && value2 != null) {
						compareValue = value1.compareTo(value2);
					}

					if (compareValue != 0) {
						return -1 * compareValue;
					}
				}
				return 0;
			}
		});
	}

	/**
	 * Converts the measurement data to a dataset collection.
	 * 
	 * @param data
	 *            data to convert
	 * @param parameters
	 *            additional parameters to add
	 * @return a {@link DatasetCollection} or null if a collection could not be
	 *         built
	 */
	public static DatasetCollection convertToDatasetCollection(MeasurementData data, Collection<Parameter> parameters) {
		Set<Parameter> parameterSet;
		if (parameters instanceof Set) {
			parameterSet = (Set<Parameter>) parameters;
		} else {
			parameterSet = new TreeSet<>();
			parameterSet.addAll(parameters);
		}
		DatasetCollectionBuilder dscBuilder = new DatasetCollectionBuilder();
		for (AbstractRecord record : data.getRecords()) {
			dscBuilder.addRecord(record, parameterSet);
		}
		return dscBuilder.build();
	}

}
