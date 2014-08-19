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
package org.aim.api.measurement.collector;

import org.aim.api.exceptions.MeasurementException;
import org.aim.api.measurement.AbstractRecord;

/**
 * The {@link IDataCollector} is an interface for all data collectors which are
 * used to collect monitoring data.
 * 
 * @author Alexander Wert
 * 
 */
public interface IDataCollector {

	/**
	 * Collects a new record. This method is called whenever a new record needs
	 * to be recorded.
	 * 
	 * @param record
	 *            Record to be collected.
	 */
	void newRecord(AbstractRecord record);

	/**
	 * Enables collecting.
	 * 
	 * @throws MeasurementException
	 *             is thrown if enabling collecting fails.
	 */
	void enable() throws MeasurementException;

	/**
	 * Disables collecting. New records will be ignored as long as collecting is
	 * disabled.
	 */
	void disable();
}
