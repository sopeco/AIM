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

import org.aim.aiminterface.entities.measurements.AbstractRecord;
import org.aim.aiminterface.entities.measurements.RecordValue;

/**
 * The StackTraceRecord is a record class for monitoring of stack traces.
 * 
 * @author Henning Schulz
 * 
 */
public class StackTraceRecord extends AbstractRecord {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3822643520667565209L;

	public static final String PAR_OPERATION = "operation";

	public static final String PAR_STACK_TRACE = "stackTrace";

	/**
	 * Default constructor required for programmatic instantiation.
	 */
	public StackTraceRecord() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param timestamp
	 *            timestamp of record
	 * @param operation
	 *            operation where stack trace has been monitored
	 * @param stackTrace
	 *            stack trace
	 */
	public StackTraceRecord(long timestamp, String operation, String stackTrace) {
		super(timestamp);
		this.operation = operation;
		this.stackTrace = stackTrace;
	}

	@RecordValue(name = PAR_OPERATION)
	private String operation;

	@RecordValue(name = PAR_STACK_TRACE)
	private String stackTrace;

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
	 * @return the stackTrace
	 */
	public String getStackTrace() {
		return stackTrace;
	}

	/**
	 * @param stackTrace
	 *            the stackTrace to set
	 */
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

}
