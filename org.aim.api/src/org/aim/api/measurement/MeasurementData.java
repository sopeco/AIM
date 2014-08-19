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
package org.aim.api.measurement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * The {@link MeasurementData} is a wrapper for a list of records, to be used
 * for JSON interfaces.
 * 
 * @author Alexander Wert
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MeasurementData {
	private List<AbstractRecord> records;

	/**
	 * Public default constructor.
	 */
	public MeasurementData() {
		records = new LinkedList<AbstractRecord>();
	}

	/**
	 * 
	 * @return the list of records.
	 */
	public List<AbstractRecord> getRecords() {
		return records;
	}

	/**
	 * Sets the list of records.
	 * 
	 * @param records
	 *            new list of records
	 */
	public void setRecords(List<AbstractRecord> records) {
		this.records = records;
	}

	/**
	 * Provides a sublist of all records which match the passed record type.
	 * 
	 * @param <T>
	 *            desired record type
	 * @param type
	 *            class of the desired record type
	 * @return a list of records matching the passed type
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractRecord> List<T> getRecords(Class<T> type) {
		List<T> result = new ArrayList<T>();
		for (AbstractRecord record : records) {
			if (record.getClass().equals(type)) {
				result.add((T) record);
			}
		}
		return result;
	}

	/**
	 * 
	 * @return set of classes representing the different record types contained
	 *         in this measurement data collection
	 */
	@JsonIgnore
	public Set<Class<? extends AbstractRecord>> getDifferentRecordTypes() {
		Set<Class<? extends AbstractRecord>> recordClasses = new HashSet<Class<? extends AbstractRecord>>();
		for (AbstractRecord record : records) {
			if (!recordClasses.contains(record.getClass())) {
				recordClasses.add(record.getClass());
			}
		}
		return recordClasses;
	}
}
