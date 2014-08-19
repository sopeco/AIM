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
 * Record for capturing JMS server statistics.
 * 
 * @author Alexander Wert
 * 
 */
public class JmsServerRecord extends AbstractRecord {

	/** */
	private static final long serialVersionUID = 1L;

	public static final String PAR_QUEUE_NAME = "queueName";

	public static final String PAR_MEMORY_USAGE = "memoryUsage";

	public static final String PAR_DEQUEUE_COUNT = "dequeueCount";

	public static final String PAR_ENQUEUE_COUNT = "enqueueCount";

	public static final String PAR_DISPATCH_COUNT = "dispatchCount";

	public static final String PAR_AVERAGE_ENQUEUE_TIME = "averageEnqueueTime";

	public static final String PAR_MEMORY_PERCANTAGE_USAGE = "memoryPercentUsage";

	public static final String PAR_QUEUE_SIZE = "queueSize";

	private static final String PAR_CPU_UTIL = "cpuUtil";

	/**
	 * Constructor.
	 */
	public JmsServerRecord() {
		super();
	}

	@RecordValue(metric = false, name = PAR_QUEUE_NAME)
	String queueName;

	@RecordValue(metric = true, name = PAR_MEMORY_USAGE)
	long memoryUsage;

	@RecordValue(metric = true, name = PAR_DEQUEUE_COUNT)
	long dequeueCount;

	@RecordValue(metric = true, name = PAR_ENQUEUE_COUNT)
	long enqueueCount;

	@RecordValue(metric = true, name = PAR_DISPATCH_COUNT)
	long dispatchCount;

	@RecordValue(metric = true, name = PAR_AVERAGE_ENQUEUE_TIME)
	double averageEnqueueTime;

	@RecordValue(metric = true, name = PAR_MEMORY_PERCANTAGE_USAGE)
	int memoryPercentUsage;

	@RecordValue(metric = true, name = PAR_QUEUE_SIZE)
	long queueSize;

	@RecordValue(metric = true, name = PAR_CPU_UTIL)
	double cpuUtil;

	/**
	 * @return the cpuUtil
	 */
	public double getCpuUtil() {
		return cpuUtil;
	}

	/**
	 * @param cpuUtil
	 *            the cpuUtil to set
	 */
	public void setCpuUtil(double cpuUtil) {
		this.cpuUtil = cpuUtil;
	}

	/**
	 * @return the queueName
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * @param queueName
	 *            the queueName to set
	 */
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	/**
	 * @return the memoryUsage
	 */
	public long getMemoryUsage() {
		return memoryUsage;
	}

	/**
	 * @return the queueSize
	 */
	public long getQueueSize() {
		return queueSize;
	}

	/**
	 * @param queueSize
	 *            the queueSize to set
	 */
	public void setQueueSize(long queueSize) {
		this.queueSize = queueSize;
	}

	/**
	 * @param memoryUsage
	 *            the memoryUsage to set
	 */
	public void setMemoryUsage(long memoryUsage) {
		this.memoryUsage = memoryUsage;
	}

	/**
	 * @return the dequeueCount
	 */
	public long getDequeueCount() {
		return dequeueCount;
	}

	/**
	 * @param dequeueCount
	 *            the dequeueCount to set
	 */
	public void setDequeueCount(long dequeueCount) {
		this.dequeueCount = dequeueCount;
	}

	/**
	 * @return the enqueueCount
	 */
	public long getEnqueueCount() {
		return enqueueCount;
	}

	/**
	 * @param enqueueCount
	 *            the enqueueCount to set
	 */
	public void setEnqueueCount(long enqueueCount) {
		this.enqueueCount = enqueueCount;
	}

	/**
	 * @return the dispatchCount
	 */
	public long getDispatchCount() {
		return dispatchCount;
	}

	/**
	 * @param dispatchCount
	 *            the dispatchCount to set
	 */
	public void setDispatchCount(long dispatchCount) {
		this.dispatchCount = dispatchCount;
	}

	/**
	 * @return the averageEnqueueTime
	 */
	public double getAverageEnqueueTime() {
		return averageEnqueueTime;
	}

	/**
	 * @param averageEnqueueTime
	 *            the averageEnqueueTime to set
	 */
	public void setAverageEnqueueTime(double averageEnqueueTime) {
		this.averageEnqueueTime = averageEnqueueTime;
	}

	/**
	 * @return the memoryPercentUsage
	 */
	public double getMemoryPercentUsage() {
		return memoryPercentUsage;
	}

	/**
	 * @param memoryPercentUsage
	 *            the memoryPercentUsage to set
	 */
	public void setMemoryPercentUsage(int memoryPercentUsage) {
		this.memoryPercentUsage = memoryPercentUsage;
	}

}
