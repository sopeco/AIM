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
 * Record capturing database caracteristics.
 * 
 * @author Alexander Wert
 * 
 */
public class DBStatisticsRecrod extends AbstractRecord {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1292041871894283042L;
	public static final String PAR_NUM_QUEUERIES = "numQueueries";
	public static final String PAR_NUM_LOCK_WAITS = "numLockWaits";
	public static final String PAR_LOCK_TIME = "lockTime";

	/**
	 * Constructor.
	 */
	public DBStatisticsRecrod() {
		super();
	}

	@RecordValue(metric = true, name = PAR_NUM_QUEUERIES)
	long numQueueries;
	@RecordValue(metric = true, name = PAR_NUM_LOCK_WAITS)
	long numLockWaits;
	@RecordValue(metric = true, name = PAR_LOCK_TIME)
	long lockTime;

	/**
	 * @return the numQueueries
	 */
	public long getNumQueueries() {
		return numQueueries;
	}

	/**
	 * @return the numLockWaits
	 */
	public long getNumLockWaits() {
		return numLockWaits;
	}

	/**
	 * @param numQueueries
	 *            the numQueueries to set
	 */
	public void setNumQueueries(long numQueueries) {
		this.numQueueries = numQueueries;
	}

	/**
	 * @param numLockWaits
	 *            the numQueueries to set
	 */
	public void setNumLockWaits(long numLockWaits) {
		this.numLockWaits = numLockWaits;
	}

	/**
	 * @return the lockTime
	 */
	public long getLockTime() {
		return lockTime;
	}

	/**
	 * @param lockTime
	 *            the lockTime to set
	 */
	public void setLockTime(long lockTime) {
		this.lockTime = lockTime;
	}

}
