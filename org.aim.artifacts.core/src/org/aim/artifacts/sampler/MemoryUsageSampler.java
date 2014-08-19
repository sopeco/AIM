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
import org.aim.artifacts.records.MemoryRecord;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.SigarException;
import org.lpe.common.extension.IExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for recording the memory of the underlying system. Currently capable of
 * calculating the memory usage in percent.
 * 
 * @author Henning Muszynski
 * 
 */
public class MemoryUsageSampler extends AbstractResourceSampler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemoryUsageSampler.class);

	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider
	 */
	public MemoryUsageSampler(IExtension<?> provider) {
		super(provider);
	}

	/**
	 * Fetches the currently free and used memory of the system. Computes the
	 * used percentage by calculating: used memory / (used + free memory) When
	 * an error occurs -1 is returned. Writes the memory usage in a
	 * {@link MemoryRecord} which is passed to the data collector.
	 * 
	 */
	@Override
	public void sample() {
		Mem memory;
		try {
			long timestamp = System.currentTimeMillis();
			memory = getSigar().getMem();
			long used = memory.getActualUsed();
			long free = memory.getActualFree();

			double memoryUsage = (double) used / (used + free);
			MemoryRecord record = new MemoryRecord(timestamp, memoryUsage);

			getDataCollector().newRecord(record);
		} catch (SigarException se) {
			LOGGER.warn("SigarException occured in Memory Recorder: {}", se.getMessage());
		}
	}
}
