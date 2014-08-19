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
package org.aim.description.sampling;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This class represents a sampling description.
 * 
 * @author Henning Schulz
 * 
 */
public class SamplingDescription {

	private static final int HASH_PRIME = 31;

	private final long delay;

	private final String resourceName;

	/**
	 * Constructor.
	 * 
	 * @param resourceName
	 *            resource to be sampled
	 * @param delay
	 *            sampling delay
	 */
	@JsonCreator
	public SamplingDescription(@JsonProperty("resourceName") String resourceName, @JsonProperty("delay") long delay) {
		if (resourceName == null) {
			this.resourceName = "";
		} else {
			this.resourceName = resourceName;
		}
		this.delay = delay;
	}

	/**
	 * @return the delay
	 */
	public long getDelay() {
		return delay;
	}

	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}

	@Override
	public String toString() {
		return resourceName + " +" + delay;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}

		SamplingDescription other = (SamplingDescription) obj;

		return this.getResourceName().equals(other.getResourceName()) && this.getDelay() == other.getDelay();
	}

	@Override
	public int hashCode() {
		return getResourceName().hashCode() * HASH_PRIME + (int) getDelay();
	}

}
