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
import org.aim.artifacts.records.DiskRecord;
import org.hyperic.sigar.DiskUsage;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.SigarException;
import org.lpe.common.extension.IExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for recording the disk of the underlying system. Currently capable of
 * measuring all writes and reads on the disk.
 * 
 * @author Henning Muszynski
 * 
 */
public class DiskIOSampler extends AbstractResourceSampler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DiskIOSampler.class);
	private static final String READS = "reads";
	private static final String WRITES = "writes";

	private long previousReads;
	private long previousWrites;

	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider
	 */
	public DiskIOSampler(IExtension<?> provider) {
		super(provider);
		previousReads = getDiskReads();
		previousWrites = getDiskWrites();
	}

	/**
	 * Samples the diesk status and passes the number of bytes read and written
	 * in a {@link DiskRecord} which is passed to the data collector.
	 */
	@Override
	public void sample() {
		long timestamp = System.currentTimeMillis();
		DiskRecord record = new DiskRecord(timestamp, getDiskReads(), getDiskWrites());
		getDataCollector().newRecord(record);
	}

	/**
	 * Reads all reads made on all disks of the underlying system. Returns the
	 * delta since the last call of this method. When an error occurs -1 is
	 * returned.
	 * 
	 * @return all reads made on all disks
	 */
	private long getDiskReads() {
		long currentReads = getDiskActions(READS);
		if (currentReads == -1) {
			return -1;
		}

		long difference = currentReads - previousReads;
		previousReads = currentReads;
		return difference;
	}

	/**
	 * Reads all writes made on all disks of the underlying system. Returns the
	 * delta since the last call of this method. When an error occurs -1 is
	 * returned.
	 * 
	 * @return all writes made on all disks
	 */
	private long getDiskWrites() {
		long currentWrites = getDiskActions(WRITES);
		if (currentWrites == -1) {
			return -1;
		}

		long difference = currentWrites - previousWrites;
		previousWrites = currentWrites;
		return difference;
	}

	private long getDiskActions(String action) {
		try {
			long actions = 0;
			for (FileSystem fileSystem : getSigar().getFileSystemList()) {
				if (fileSystem.getType() == FileSystem.TYPE_LOCAL_DISK) {
					DiskUsage usage = getSigar().getDiskUsage(fileSystem.getDirName());

					if (READS.equals(action)) {
						actions += usage.getReads();
					} else if (WRITES.equals(action)) {
						actions += usage.getWriteBytes();
					} else {
						LOGGER.warn("No proper action was passed while using disk recorder");
						return -1;
					}
				}
			}

			return actions;
		} catch (SigarException se) {
			LOGGER.warn("SigarException occured in Disk Recorder: {}", se.getMessage());
		}

		return -1;
	}
}
