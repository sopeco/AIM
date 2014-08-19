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
package org.lpe.common.resourcemonitoring;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aim.api.exceptions.MeasurementException;
import org.aim.api.measurement.collector.AbstractDataSource;
import org.aim.api.measurement.sampling.AbstractSampler;
import org.aim.api.measurement.sampling.ResourceSamplerFactory;
import org.aim.api.measurement.sampling.SamplingExecuter;
import org.aim.description.sampling.SamplingDescription;
import org.lpe.common.util.system.LpeSystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * System Monitoring Utility. Once configured it manages all recorders and
 * collects their results.
 * 
 * @author Henning Muszynski
 * 
 */
public final class SystemMonitor {
	private static final Logger LOGGER = LoggerFactory.getLogger(SystemMonitor.class);

	private static SystemMonitor instance;

	/**
	 * 
	 * @return singleton instance
	 */
	public static SystemMonitor getInstance() {
		if (instance == null) {
			instance = new SystemMonitor();
		}
		return instance;
	}

	private final Map<Long, SamplingExecuter> monitoringJobs;

	/**
	 * private construcor due to singleton class
	 */
	private SystemMonitor() {
		monitoringJobs = new HashMap<Long, SamplingExecuter>();

	}

	/**
	 * Initializes the system monitoring utility and all required recorders.
	 * When no delay is specified in the configuration a default delay of 100ms
	 * is chosen.
	 * 
	 * @param samplingDescriptions
	 *            a set of sampling descriptions
	 * @throws MeasurementException
	 *             if a sampling extension cannot be found
	 */
	public void addMonitoringJob(Set<SamplingDescription> samplingDescriptions) throws MeasurementException {

		for (SamplingDescription sampDescr : samplingDescriptions) {
			Long delay = sampDescr.getDelay();
			SamplingExecuter executer = monitoringJobs.get(delay);
			if (executer == null) {
				executer = new SamplingExecuter(delay);
				monitoringJobs.put(delay, executer);
			}

			AbstractSampler sampler = ResourceSamplerFactory.getSampler(sampDescr.getResourceName(),
					AbstractDataSource.getDefaultDataSource());
			if (sampler != null) {
				executer.addSampler(sampler);
			}
		}

	}

	/**
	 * Removes all monitoring jobs.
	 */
	public void clearMonitoringJobs() {
		monitoringJobs.clear();
	}

	/**
	 * Enables resource monitoring.
	 */
	public void start() {
		LOGGER.info("System monitoring started");
		try {
			AbstractDataSource.getDefaultDataSource().enable();
		} catch (MeasurementException e) {
			LOGGER.warn("Were not able to enable data collector!");
		}

		for (SamplingExecuter samplingExecuter : monitoringJobs.values()) {
			LpeSystemUtils.submitTask(samplingExecuter);
		}
	}

	/**
	 * Stops the system monitor and all recorders.
	 */
	public void stop() {

		for (SamplingExecuter samplingExecuter : monitoringJobs.values()) {
			samplingExecuter.stop();
		}
		for (SamplingExecuter samplingExecuter : monitoringJobs.values()) {
			samplingExecuter.waitForTermination();
		}

		AbstractDataSource.getDefaultDataSource().disable();
		LOGGER.info("System monitoring stopped");
	}

	/**
	 * 
	 * @param oStream
	 *            stream where to pipe the results to
	 */
	public void pipeResultToOutputStream(OutputStream oStream) {
		try {
			AbstractDataSource.getDefaultDataSource().pipeToOutputStream(oStream);
		} catch (MeasurementException e) {
			throw new RuntimeException(e);
		}

	}
}
