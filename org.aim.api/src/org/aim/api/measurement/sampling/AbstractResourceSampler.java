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

import org.hyperic.sigar.Sigar;
import org.lpe.common.extension.IExtension;

/**
 * The {@link AbstractResourceSampler} provides a common constructor for all
 * recorder classes. A recorder class is responsible for sampling a specific
 * system recource.
 * 
 * @author Alexander Wert
 * 
 */
public abstract class AbstractResourceSampler extends AbstractSampler {

	protected static final String RESOURCE_NAME_SEPARATOR = ":";
	private Sigar sigar;

	/**
	 * Constructor.
	 * @param provider extension provider
	 */
	public AbstractResourceSampler(IExtension<?> provider) {
		super(provider);
	}

	/**
	 * @return the sigar
	 */
	public Sigar getSigar() {
		return sigar;
	}

	/**
	 * @param sigar
	 *            the sigar to set
	 */
	public void setSigar(Sigar sigar) {
		this.sigar = sigar;
	}
}
