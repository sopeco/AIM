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
package org.aim.aiminterface.description.measurementprobe;

import java.beans.ConstructorProperties;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This class represents an immutable measurement probe.
 * 
 * @author Henning Schulz, Steffen Becker
 * 
 */
public final class MeasurementProbeDescription {

	private final String name;
	
	/**
	 * Constructor.
	 * 
	 * @param name
	 *            name of this probe
	 */
	@ConstructorProperties("name")
	@JsonCreator
	public MeasurementProbeDescription(@JsonProperty("name") final String name) {
		super();
		this.name = name;
	}

	/**
	 * @return the probeName
	 */
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final MeasurementProbeDescription other = (MeasurementProbeDescription) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

}
