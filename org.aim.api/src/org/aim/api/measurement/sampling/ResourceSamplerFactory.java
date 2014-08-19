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
package org.aim.api.measurement.sampling;

import org.aim.api.exceptions.MeasurementException;
import org.aim.api.measurement.collector.IDataCollector;
import org.hyperic.sigar.Sigar;
import org.lpe.common.extension.ExtensionRegistry;

/**
 * Creates recorder for different sampling types.
 * 
 * @author Alexander Wert
 * 
 */
public final class ResourceSamplerFactory {
	private ResourceSamplerFactory() {
	}

	private static Sigar sigar;

	/**
	 * Getter for the Sigar singleton instance (required for resource
	 * information retrieval).
	 * 
	 * @return Sigar instance
	 */
	public static Sigar getSigar() {
		if (sigar == null) {
			sigar = new Sigar();
		}
		return sigar;
	}

	/**
	 * 
	 * @param sType
	 *            sampling type
	 * @param dataCollector
	 *            data collector to use
	 * @return sampler for the type
	 * @throws MeasurementException
	 *             thrown if a sampler extension cannot be found
	 */
	public static AbstractSampler getSampler(String sType, IDataCollector dataCollector) throws MeasurementException {

		AbstractSampler sampler = ExtensionRegistry.getSingleton().getExtensionArtifact(AbstractSamplerExtension.class,
				sType);
		if (sampler == null) {
			throw new MeasurementException("Invalid sampling resource identifier " + sType + "!");
		}

		sampler.setDataCollector(dataCollector);
		((AbstractResourceSampler) sampler).setSigar(getSigar());
		return sampler;

	}

}
