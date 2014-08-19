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
package org.aim.artifacts.records;

import org.aim.api.measurement.AbstractRecord;
import org.aim.api.measurement.RecordValue;

/**
 * Memory footprint record.
 * 
 * @author Alexander Wert
 * 
 */
public class MemoryFootprintRecord extends AbstractRecord {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7072923393754033588L;

	public static final String PAR_END_TIMESTAMP = "endTimestamp";

	public static final String PAR_OPERATION = "operation";

	public static final String PAR_MEMORY_USED_BEFORE = "memoryUsedBefore";
	public static final String PAR_MEMORY_USED_AFTER = "memoryUsedAfter";

	public static final String PAR_EDEN_SPACE_USED_BEFORE = "edenSpaceUsedBefore";
	public static final String PAR_EDEN_SPACE_USED_AFTER = "edenSpaceUsedAfter";

	public static final String PAR_SURVIVOR_SPACE_USED_BEFORE = "survivorSpaceUsedBefore";
	public static final String PAR_SURVIVOR_SPACE_USED_AFTER = "survivorSpaceUsedAfter";

	public static final String PAR_TENURED_SPACE_USED_BEFORE = "tenuredSpaceUsedBefore";
	public static final String PAR_TENURED_SPACE_USED_AFTER = "tenuredSpaceUsedAfter";

	@RecordValue(metric = true, name = PAR_END_TIMESTAMP, isTimestamp = true)
	long endTimestamp;

	@RecordValue(name = PAR_OPERATION)
	String operation;

	@RecordValue(metric = true, name = PAR_MEMORY_USED_BEFORE)
	long memoryUsedBefore;

	@RecordValue(metric = true, name = PAR_MEMORY_USED_AFTER)
	long memoryUsedAfter;

	@RecordValue(metric = true, name = PAR_EDEN_SPACE_USED_BEFORE)
	long edenSpaceUsedBefore;

	@RecordValue(metric = true, name = PAR_EDEN_SPACE_USED_AFTER)
	long edenSpaceUsedAfter;

	@RecordValue(metric = true, name = PAR_SURVIVOR_SPACE_USED_BEFORE)
	long survivorSpaceUsedBefore;

	@RecordValue(metric = true, name = PAR_SURVIVOR_SPACE_USED_AFTER)
	long survivorSpaceUsedAfter;

	@RecordValue(metric = true, name = PAR_TENURED_SPACE_USED_BEFORE)
	long tenuredSpaceUsedBefore;

	@RecordValue(metric = true, name = PAR_TENURED_SPACE_USED_AFTER)
	long tenuredSpaceUsedAfter;

	/**
	 * Default constructor required for JSON serialization.
	 */
	public MemoryFootprintRecord() {
		super();
	}

	/**
	 * Getter for endTimestamp.
	 * 
	 * @return endTimestamp in ms
	 */
	public long getEndTimestamp() {
		return endTimestamp;
	}

	/**
	 * Setter for endTimestamp.
	 * 
	 * @param endTimestamp
	 *            start timestamps in ms
	 */
	public void setEndTimestamp(long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * @param operation
	 *            the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}

	/**
	 * @return the memoryUsedBefore
	 */
	public long getMemoryUsedBefore() {
		return memoryUsedBefore;
	}

	/**
	 * @param memoryUsedBefore
	 *            the memoryUsedBefore to set
	 */
	public void setMemoryUsedBefore(long memoryUsedBefore) {
		this.memoryUsedBefore = memoryUsedBefore;
	}

	/**
	 * @return the memoryUsedAfter
	 */
	public long getMemoryUsedAfter() {
		return memoryUsedAfter;
	}

	/**
	 * @param memoryUsedAfter
	 *            the memoryUsedAfter to set
	 */
	public void setMemoryUsedAfter(long memoryUsedAfter) {
		this.memoryUsedAfter = memoryUsedAfter;
	}

	/**
	 * @return the edenSpaceUsedBefore
	 */
	public long getEdenSpaceUsedBefore() {
		return edenSpaceUsedBefore;
	}

	/**
	 * @param edenSpaceUsedBefore
	 *            the edenSpaceUsedBefore to set
	 */
	public void setEdenSpaceUsedBefore(long edenSpaceUsedBefore) {
		this.edenSpaceUsedBefore = edenSpaceUsedBefore;
	}

	/**
	 * @return the edenSpaceUsedAfter
	 */
	public long getEdenSpaceUsedAfter() {
		return edenSpaceUsedAfter;
	}

	/**
	 * @param edenSpaceUsedAfter
	 *            the edenSpaceUsedAfter to set
	 */
	public void setEdenSpaceUsedAfter(long edenSpaceUsedAfter) {
		this.edenSpaceUsedAfter = edenSpaceUsedAfter;
	}

	/**
	 * @return the survivorSpaceUsedBefore
	 */
	public long getSurvivorSpaceUsedBefore() {
		return survivorSpaceUsedBefore;
	}

	/**
	 * @param survivorSpaceUsedBefore
	 *            the survivorSpaceUsedBefore to set
	 */
	public void setSurvivorSpaceUsedBefore(long survivorSpaceUsedBefore) {
		this.survivorSpaceUsedBefore = survivorSpaceUsedBefore;
	}

	/**
	 * @return the survivorSpaceUsedAfter
	 */
	public long getSurvivorSpaceUsedAfter() {
		return survivorSpaceUsedAfter;
	}

	/**
	 * @param survivorSpaceUsedAfter
	 *            the survivorSpaceUsedAfter to set
	 */
	public void setSurvivorSpaceUsedAfter(long survivorSpaceUsedAfter) {
		this.survivorSpaceUsedAfter = survivorSpaceUsedAfter;
	}

	/**
	 * @return the tenuredSpaceUsedBefore
	 */
	public long getTenuredSpaceUsedBefore() {
		return tenuredSpaceUsedBefore;
	}

	/**
	 * @param tenuredSpaceUsedBefore
	 *            the tenuredSpaceUsedBefore to set
	 */
	public void setTenuredSpaceUsedBefore(long tenuredSpaceUsedBefore) {
		this.tenuredSpaceUsedBefore = tenuredSpaceUsedBefore;
	}

	/**
	 * @return the tenuredSpaceUsedAfter
	 */
	public long getTenuredSpaceUsedAfter() {
		return tenuredSpaceUsedAfter;
	}

	/**
	 * @param tenuredSpaceUsedAfter
	 *            the tenuredSpaceUsedAfter to set
	 */
	public void setTenuredSpaceUsedAfter(long tenuredSpaceUsedAfter) {
		this.tenuredSpaceUsedAfter = tenuredSpaceUsedAfter;
	}

}
