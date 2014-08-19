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
package org.aim.mainagent.sampling;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aim.api.exceptions.MeasurementException;
import org.aim.api.measurement.collector.AbstractDataSource;
import org.aim.api.measurement.collector.IDataCollector;
import org.aim.api.measurement.sampling.AbstractResourceSampler;
import org.aim.api.measurement.sampling.AbstractSampler;
import org.aim.api.measurement.sampling.AbstractSamplerExtension;
import org.aim.api.measurement.sampling.ResourceSamplerFactory;
import org.aim.api.measurement.sampling.SamplingExecuter;
import org.aim.description.sampling.SamplingDescription;
import org.lpe.common.extension.ExtensionRegistry;
import org.lpe.common.util.system.LpeSystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Samples resources of the application under test.
 * 
 * @author Alexander Wert
 * 
 */
public final class Sampling {
	private static final Logger LOGGER = LoggerFactory.getLogger(Sampling.class);
	private static Sampling instance;

	/**
	 * 
	 * @return singleton instance
	 */
	public static Sampling getInstance() {
		if (instance == null) {
			instance = new Sampling();
		}
		return instance;
	}

	private final Map<Long, SamplingExecuter> monitoringJobs;

	private final IDataCollector dataCollector;

	private Sampling() {
		monitoringJobs = new HashMap<Long, SamplingExecuter>();
		dataCollector = AbstractDataSource.getDefaultDataSource();
	}

	/**
	 * Initializes the system monitoring utility and all required recorders.
	 * When no delay is specified in the configuration a default delay of 100ms
	 * is chosen.
	 * 
	 * @param samplingDescriptions
	 *            a set of descriptions of the sampling jobs
	 * @throws MeasurementException
	 *             if sampler class cannot be found
	 */
	public void addMonitoringJob(Set<SamplingDescription> samplingDescriptions) throws MeasurementException {

		for (SamplingDescription samplingDescr : samplingDescriptions) {
			String resourceName = samplingDescr.getResourceName();
			Long delay = samplingDescr.getDelay();
			AbstractSampler sampler = ExtensionRegistry.getSingleton().getExtensionArtifact(
					AbstractSamplerExtension.class, resourceName);
			if (sampler == null) {
				throw new MeasurementException("Invalid sampling resource identifier!");
			}
			SamplingExecuter executer = monitoringJobs.get(delay);
			if (executer == null) {
				executer = new SamplingExecuter(delay);
				monitoringJobs.put(delay, executer);
			}

			if (AbstractResourceSampler.class.isAssignableFrom(sampler.getClass())) {
				((AbstractResourceSampler) sampler).setSigar(ResourceSamplerFactory.getSigar());
			}
			sampler.setDataCollector(dataCollector);

			if (sampler != null) {
				LOGGER.info("Added application resource sampling job!");
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
		LOGGER.info("Started Application Resource Sampling");
		for (SamplingExecuter samplingExecuter : monitoringJobs.values()) {
			LOGGER.info("Starting sampling executer with a delay of " + samplingExecuter.getDelay() + " ms");
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
		LOGGER.info("Stopped Application Resource Sampling");
	}

}
