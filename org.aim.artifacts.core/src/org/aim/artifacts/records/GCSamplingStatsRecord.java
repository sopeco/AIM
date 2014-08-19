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
 * Record for garbage collection statistics.
 * 
 * @author Alexander Wert
 * 
 */
public class GCSamplingStatsRecord extends AbstractRecord {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5614682608153672878L;


	public static final String PAR_GC_NEW_GEN_COUNT = "gcNewGenCount";
	public static final String PAR_GC_OLD_GEN_COUNT = "gcOldGenCount";

	public static final String PAR_GC_NEW_GEN_CPU_TIME = "gcNewGenCPUTime";
	public static final String PAR_GC_OLD_GEN_CPU_TIME = "gcOldGenCPUTime";

	
	@RecordValue(metric = true, name = PAR_GC_NEW_GEN_COUNT)
	long gcNewGenCount;

	@RecordValue(metric = true, name = PAR_GC_OLD_GEN_COUNT)
	long gcOldGenCount;

	@RecordValue(metric = true, name = PAR_GC_NEW_GEN_CPU_TIME)
	long gcNewGenCPUTime;

	@RecordValue(metric = true, name = PAR_GC_OLD_GEN_CPU_TIME)
	long gcOldGenCPUTime;

	/**
	 * Public constructor required for JSON serialization.
	 */
	public GCSamplingStatsRecord() {
	}

	/**
	 * @return the gcNewGenCount
	 */
	public long getGcNewGenCount() {
		return gcNewGenCount;
	}

	/**
	 * @param gcNewGenCount
	 *            the gcNewGenCount to set
	 */
	public void setGcNewGenCount(long gcNewGenCount) {
		this.gcNewGenCount = gcNewGenCount;
	}

	/**
	 * @return the gcOldGenCount
	 */
	public long getGcOldGenCount() {
		return gcOldGenCount;
	}

	/**
	 * @param gcOldGenCount
	 *            the gcOldGenCount to set
	 */
	public void setGcOldGenCount(long gcOldGenCount) {
		this.gcOldGenCount = gcOldGenCount;
	}

	/**
	 * @return the gcNewGenCPUTime
	 */
	public long getGcNewGenCPUTime() {
		return gcNewGenCPUTime;
	}

	/**
	 * @param gcNewGenCPUTime
	 *            the gcNewGenCPUTime to set
	 */
	public void setGcNewGenCPUTime(long gcNewGenCPUTime) {
		this.gcNewGenCPUTime = gcNewGenCPUTime;
	}

	/**
	 * @return the gcOldGenCPUTime
	 */
	public long getGcOldGenCPUTime() {
		return gcOldGenCPUTime;
	}

	/**
	 * @param gcOldGenCPUTime
	 *            the gcOldGenCPUTime to set
	 */
	public void setGcOldGenCPUTime(long gcOldGenCPUTime) {
		this.gcOldGenCPUTime = gcOldGenCPUTime;
	}

}
