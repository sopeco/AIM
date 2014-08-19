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

import java.util.List;
import java.util.Set;

import org.aim.api.measurement.AbstractRecord;

/**
 * Wrappes a set of data records.
 * 
 * @author Alexander Wert
 * 
 */
public interface IDatasetElement {
	/**
	 * Returns the values for the given field/property name.
	 * 
	 * @param name
	 *            name of the property of interest
	 * @return all values for the specified field.
	 */
	List<Object> getValues(String name);

	/**
	 * Returns the different values for the given field/property name.
	 * 
	 * @param name
	 *            name of the property of interest
	 * @return all different values for the specified field.
	 */
	Set<Object> getValueSet(String name);

	/**
	 * Returns the values for the given field/property name.
	 * 
	 * @param name
	 *            name of the property of interest
	 * @param type
	 *            type to cast the values to
	 * @param <S>
	 *            type to cast the values to
	 * @return all values for the specified field.
	 */
	<S> List<S> getValues(String name, Class<S> type);

	/**
	 * Returns the different values for the given field/property name.
	 * 
	 * @param name
	 *            name of the property of interest
	 * @param type
	 *            type to cast the values to
	 * @param <S>
	 *            type to cast the values to
	 * @return all different values for the specified field.
	 */
	<S> Set<S> getValueSet(String name, Class<S> type);

	
	
	/**
	 * 
	 * @return the names of all observation properties for that wrapped record
	 */
	Set<String> getObservationPropertiesNames();

	/**
	 * @return parameter names
	 */
	Set<String> getInputParameterNames();

	/**
	 * 
	 * @return the type of the underlying records, null if no type is specified yet
	 */
	Class<? extends AbstractRecord> getRecordType();

	/**
	 * 
	 * @return all possible parameter configurations
	 */
	Set<ParameterSelection> getAllParameterConfigurations();

	/**
	 * 
	 * @return all records
	 */
	List<AbstractRecord> getRecords();

	/**
	 * @param type
	 *            class of the records to return
	 * @param <T>
	 *            type of the record types to return
	 * @return all records of the given type
	 * 
	 */
	<T extends AbstractRecord> List<T> getRecords(Class<T> type);

	/**
	 * 
	 * @return header (parameter names) of the underlying record
	 */
	String[] getHeader();

	/**
	 * 
	 * @return the type names of the parameters of the underlying records
	 */
	String[] getTypes();

	/**
	 * 
	 * @return number of actual records
	 */
	int size();
}
