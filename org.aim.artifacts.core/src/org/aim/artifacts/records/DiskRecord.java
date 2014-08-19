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
 * The {@link DiskRecord} is a record class for monitoring of the disk
 * operations.
 * 
 * @author Alexander Wert
 * 
 */
public class DiskRecord extends AbstractRecord {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3748723739016236645L;

	public static final String PAR_DISK_READS = "diskReads";
	public static final String PAR_DISK_WRITES = "diskWrites";



	@RecordValue(metric = true, name = PAR_DISK_READS)
	long diskReads;

	@RecordValue(metric = true, name = PAR_DISK_WRITES)
	long diskWrites;

	/**
	 * Default constructor required for programmatic instantiation.
	 */
	public DiskRecord() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param timeStamp
	 *            timestamp of the record
	 * @param diskReads
	 *            number of bytes read
	 * @param diskWrites
	 *            number of bytes written
	 */
	public DiskRecord(long timeStamp, long diskReads, long diskWrites) {
		super(timeStamp);
		this.diskReads = diskReads;
		this.diskWrites = diskWrites;
	}



	

	/**
	 * Getter for disk reads.
	 * 
	 * @return number of bytes read
	 */
	public long getDiskReads() {
		return diskReads;
	}

	/**
	 * Setter for disk reads.
	 * 
	 * @param diskReads
	 *            number of bytes read
	 */
	public void setDiskReads(long diskReads) {
		this.diskReads = diskReads;
	}

	/**
	 * Getter for disk writes.
	 * 
	 * @return number of bytes written
	 */
	public long getDiskWrites() {
		return diskWrites;
	}

	/**
	 * Setter for for disk writes.
	 * 
	 * @param diskWrites
	 *            number of bytes written
	 */
	public void setDiskWrites(long diskWrites) {
		this.diskWrites = diskWrites;
	}

}
