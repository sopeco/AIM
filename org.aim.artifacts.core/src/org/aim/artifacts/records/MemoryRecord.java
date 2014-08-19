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
 * The {@link MemoryRecord} is a record class for monitoring of the memory
 * state.
 * 
 * @author Alexander Wert
 * 
 */
public class MemoryRecord extends AbstractRecord {

	/**
	 * 
	 */
	private static final long serialVersionUID = -105849316479609569L;

	public static final String PAR_MEMORY_USAGE = "memoryUsage";

	/**
	 * Default constructor required for programmatic instantiation.
	 */
	public MemoryRecord() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param timeStamp
	 *            timestamp
	 * @param memoryUsage
	 *            the memory usage in bytes
	 */
	public MemoryRecord(long timeStamp, double memoryUsage) {
		super();
		this.setTimeStamp(timeStamp);
		this.setMemoryUsage(memoryUsage);
	}

	@RecordValue(metric = true, name = PAR_MEMORY_USAGE)
	double memoryUsage;

	/**
	 * Getter for memory usage.
	 * 
	 * @return memory used in bates
	 */
	public double getMemoryUsage() {
		return memoryUsage;
	}

	/**
	 * Setter for memory usage.
	 * 
	 * @param memoryUsage
	 *            memory used in bytes
	 */
	public void setMemoryUsage(double memoryUsage) {
		this.memoryUsage = memoryUsage;
	}
}
