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
 * The {@link CPUUtilizationRecord} is a record class for monitoring of the CPU
 * utilization.
 * 
 * @author Alexander Wert
 * 
 */
public class CPUUtilizationRecord extends AbstractRecord {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2989285799326073280L;

	public static final String PAR_CPU_ID = "cpuId";
	public static final String PAR_UTILIZATION = "utilization";

	public static final String RES_CPU_AGGREGATED = "cpu-agg";
	public static final String RES_CPU_PREFIX = "cpu-";

	@RecordValue(name = PAR_CPU_ID)
	String cpuId;

	@RecordValue(metric = true, name = PAR_UTILIZATION)
	double utilization;



	/**
	 * Default constructor required for programmatic instantiation.
	 */
	public CPUUtilizationRecord() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param cpuID
	 *            ID of the CPU which has been recorded
	 * @param utilization
	 *            utilization of the CPU
	 * @param timeStamp
	 *            timestamp of the record
	 */
	public CPUUtilizationRecord(long timeStamp, String cpuID, double utilization) {
		super(timeStamp);
		this.setCpuId(cpuID);
		this.utilization = utilization;
	}

	

	/**
	 * Getter for utilization.
	 * 
	 * @return cpu utilization
	 */
	public double getUtilization() {
		return utilization;
	}

	/**
	 * Setter for utilization.
	 * 
	 * @param utilization
	 *            cpu utilization
	 */
	public void setUtilization(double utilization) {
		this.utilization = utilization;
	}

	/**
	 * Getter for cpu id.
	 * 
	 * @return cpu id
	 */
	public String getCpuId() {
		return cpuId;
	}

	/**
	 * Setter for CPU id.
	 * 
	 * @param cpuId
	 *            cpu id
	 */
	public void setCpuId(String cpuId) {
		this.cpuId = cpuId;
	}

}
