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
package org.aim.description.probes;

import org.aim.description.scopes.Scope;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This class represents a measurement probe.
 * 
 * @author Henning Schulz
 * 
 * @param <S>
 *            type of scopes which can be used with this probe
 */
public class MeasurementProbe<S extends Scope> {

	private final String name;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            name of this probe
	 */
	@JsonCreator
	public MeasurementProbe(@JsonProperty("name") String name) {
		this.name = name;
	}

	/**
	 * @return the probeName
	 */
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}

		MeasurementProbe<?> other = (MeasurementProbe<?>) obj;

		return this.getName().equals(other.getName());
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

}
