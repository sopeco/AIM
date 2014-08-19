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
import org.aim.artifacts.records.NetworkInterfaceInfoRecord;
import org.aim.artifacts.records.NetworkRecord;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.SigarException;
import org.lpe.common.extension.IExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for recording the network of the underlying system. Currently capable
 * of reading all received and transferred bytes on all network adapters,
 * reading all open TCP connections of a specific IP address and port.
 * Calculating the networkload is no longer supported as it was not supported
 * from SIGAR either. You have to evaluate these values inside another
 * application.
 * 
 * @author Henning Muszynski
 * 
 */
public class NetworkIOSampler extends AbstractResourceSampler {

	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkIOSampler.class);

	private static String[] networkInterfaces = null;

	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider
	 */
	public NetworkIOSampler(IExtension<?> provider) {
		super(provider);
	}

	/**
	 * Samples the network status and writes the result into a
	 * {@link NetworkRecord} which is passed to the data collector.
	 */
	@Override
	public void sample() {
		long timestamp = System.currentTimeMillis();
		synchronized (this) {
			if (networkInterfaces == null) {
				try {
					networkInterfaces = getSigar().getNetInterfaceList();
					for (String networkInterface : networkInterfaces) {
						NetInterfaceStat netStat = getSigar().getNetInterfaceStat(networkInterface);
						getDataCollector().newRecord(
								new NetworkInterfaceInfoRecord(System.currentTimeMillis(), networkInterface, netStat
										.getSpeed()));
					}
				} catch (SigarException e) {
					LOGGER.warn("Sigar Exception: {}", e);
					return;
				}
			}
		}

		for (String networkInterface : networkInterfaces) {
			try {
				NetInterfaceStat netStat = getSigar().getNetInterfaceStat(networkInterface);
				NetworkRecord record = new NetworkRecord(timestamp, networkInterface, netStat.getRxBytes(),
						netStat.getTxBytes());
				getDataCollector().newRecord(record);
			} catch (SigarException e) {
				LOGGER.warn("Sigar Exception: {}", e);
			}

		}

	}
}
