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

import org.aim.api.measurement.RecordValue;

/**
 * The {@link NetworkRecord} is a record class for monitoring of operations
 * response time.
 * 
 * @author Alexander Wert
 * 
 */
public class NanoResponseTimeRecord extends ResponseTimeRecord {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4196075989077022331L;

	public static final String PAR_NANO_TIMESTAMP = "nanoTimestamp";

	@RecordValue(metric = true, name = PAR_NANO_TIMESTAMP)
	long nanoTimestamp;

	/**
	 * @return the nanoTimestamp
	 */
	public long getNanoTimestamp() {
		return nanoTimestamp;
	}

	/**
	 * @param nanoTimestamp
	 *            the nanoTimestamp to set
	 */
	public void setNanoTimestamp(long nanoTimestamp) {
		this.nanoTimestamp = nanoTimestamp;
	}

}
