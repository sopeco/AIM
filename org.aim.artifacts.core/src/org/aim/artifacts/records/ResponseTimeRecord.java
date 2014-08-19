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
 * The {@link NetworkRecord} is a record class for monitoring of operations
 * response time.
 * 
 * @author Alexander Wert
 * 
 */
public class ResponseTimeRecord extends AbstractRecord {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4196075989077022331L;

	public static final String PAR_OPERATION = "operation";

	public static final String PAR_RESPONSE_TIME = "responseTime";

	/**
	 * Default constructor required for programmatic instantiation.
	 */
	public ResponseTimeRecord() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param operation
	 *            operation of which the response time has been monitored
	 * @param responseTime
	 *            response time
	 * @param timeStamp
	 *            timestamp of record
	 */
	public ResponseTimeRecord(long timeStamp, String operation, long responseTime) {
		super(timeStamp);
		this.operation = operation;
		this.responseTime = responseTime;
	}

	@RecordValue(name = PAR_OPERATION)
	String operation;

	@RecordValue(metric = true, name = PAR_RESPONSE_TIME)
	long responseTime;

	/**
	 * 
	 * @return full name of the monitored operation
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * Sets the full name of the monitored operation.
	 * 
	 * @param operation
	 *            full name of the operation
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}

	/**
	 * 
	 * @return monitored response time
	 */
	public long getResponseTime() {
		return responseTime;
	}

	/**
	 * Sets the response time.
	 * 
	 * @param responseTime
	 *            response time
	 */
	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

}
