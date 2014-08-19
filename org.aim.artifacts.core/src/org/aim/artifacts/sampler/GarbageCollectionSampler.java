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

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

import org.aim.api.measurement.sampling.AbstractSampler;
import org.aim.artifacts.records.GCSamplingStatsRecord;
import org.lpe.common.extension.IExtension;

/**
 * Samples garbage collection statistics.
 * 
 * @author Alexander Wert
 * 
 */
public class GarbageCollectionSampler extends AbstractSampler {

	private static final String[] NEW_GEN_GC_NAMES = { "Copy", "PS Scavenge", "ParNew", "G1 Young Generation" };
	private static final String[] OLD_GEN_GC_NAMES = { "MarkSweepCompact", "PS MarkSweep", "ConcurrentMarkSweep",
			"G1 Old Generation" };

	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider
	 */
	public GarbageCollectionSampler(IExtension<?> provider) {
		super(provider);
	}

	@Override
	public void sample() {
		GCSamplingStatsRecord record = new GCSamplingStatsRecord();
		record.setTimeStamp(System.currentTimeMillis());
		for (GarbageCollectorMXBean mxBean : ManagementFactory.getGarbageCollectorMXBeans()) {

			boolean found = false;
			for (String newGenName : NEW_GEN_GC_NAMES) {
				if (mxBean.getName().equals(newGenName)) {
					found = true;
					break;
				}
			}

			if (found) {
				record.setGcNewGenCount(mxBean.getCollectionCount());
				record.setGcNewGenCPUTime(mxBean.getCollectionTime());
				continue;
			}

			found = false;
			for (String oldGenName : OLD_GEN_GC_NAMES) {
				if (mxBean.getName().equals(oldGenName)) {
					found = true;
					break;
				}
			}

			if (found) {
				record.setGcOldGenCount(mxBean.getCollectionCount());
				record.setGcOldGenCPUTime(mxBean.getCollectionTime());
			}
		}

		getDataCollector().newRecord(record);

	}

}
