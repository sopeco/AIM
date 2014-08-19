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
package org.aim.artifacts.sampler;

import org.aim.api.measurement.sampling.AbstractResourceSampler;
import org.aim.artifacts.records.CPUUtilizationRecord;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.SigarException;
import org.lpe.common.extension.IExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for recording the cpu of the underlying system. Currently capable of
 * measuring the cpu utilization in percent.
 * 
 * @author Henning Muszynski
 * 
 */
public class CPUSampler extends AbstractResourceSampler {

	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider
	 */
	public CPUSampler(IExtension<?> provider) {
		super(provider);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CPUSampler.class);

	/**
	 * Fetches the current cpu utilization and passes it in a CPUUtilization
	 * record to the data collector. When an error occurs -1 is returned.
	 * 
	 */
	@Override
	public void sample() {
		try {

			CpuPerc cpu = getSigar().getCpuPerc();
			long timestamp = System.currentTimeMillis();
			double utilization = cpu.getCombined();
			CPUUtilizationRecord record = null;
			if (!Double.isNaN(utilization) && !Double.isInfinite(utilization)) {
				record = new CPUUtilizationRecord(timestamp, CPUUtilizationRecord.RES_CPU_AGGREGATED, cpu.getCombined());
				getDataCollector().newRecord(record);
			}

			int i = 0;
			for (CpuPerc cpuPerc : getSigar().getCpuPercList()) {
				utilization = cpuPerc.getCombined();
				if (!Double.isNaN(utilization) && !Double.isInfinite(utilization)) {
					record = new CPUUtilizationRecord(timestamp, CPUUtilizationRecord.RES_CPU_PREFIX + i, utilization);
					getDataCollector().newRecord(record);
				}
				i++;
			}

		} catch (SigarException se) {
			LOGGER.warn("SigarException occured in CPU Recorder: {}", se.getMessage());
		}

	}

}