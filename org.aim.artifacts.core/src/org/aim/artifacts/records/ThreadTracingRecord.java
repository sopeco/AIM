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
public class ThreadTracingRecord extends AbstractRecord {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4196075989077022331L;

	public static final String PAR_OPERATION = "operation";

	public static final String PAR_THREAD_ID = "threadId";

	public static final String PAR_EXIT_NANO_TIME = "exitNanoTime";

	public static final String PAR_ENTER_NANO_TIME = "enterNanoTime";

	/**
	 * Default constructor required for programmatic instantiation.
	 */
	public ThreadTracingRecord() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param operation
	 *            operation of which the response time has been monitored
	 * @param threadId
	 *            id of the thread created that record
	 * @param timeStamp
	 *            timestamp of record
	 */
	public ThreadTracingRecord(long timeStamp, String operation, long threadId) {
		super(timeStamp);
		this.operation = operation;
		this.threadId = threadId;
	}

	@RecordValue(name = PAR_OPERATION)
	String operation;

	@RecordValue(metric = true, name = PAR_THREAD_ID)
	long threadId;

	@RecordValue(metric = true, name = PAR_ENTER_NANO_TIME)
	long enterNanoTime;

	@RecordValue(metric = true, name = PAR_EXIT_NANO_TIME)
	long exitNanoTime;

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
	 * @return thread id
	 */
	public long getThreadId() {
		return threadId;
	}

	/**
	 * Sets the response time.
	 * 
	 * @param threadId
	 *            id of the thread created that record
	 */
	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	/**
	 * @return the exitNanoTime
	 */
	public long getExitNanoTime() {
		return exitNanoTime;
	}

	/**
	 * @param exitNanoTime
	 *            the exitNanoTime to set
	 */
	public void setExitNanoTime(long exitNanoTime) {
		this.exitNanoTime = exitNanoTime;
	}

	/**
	 * @return the enterNanoTime
	 */
	public long getEnterNanoTime() {
		return enterNanoTime;
	}

	/**
	 * @param enterNanoTime
	 *            the enterNanoTime to set
	 */
	public void setEnterNanoTime(long enterNanoTime) {
		this.enterNanoTime = enterNanoTime;
	}

}
