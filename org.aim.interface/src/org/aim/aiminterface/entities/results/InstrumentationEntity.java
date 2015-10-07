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
package org.aim.aiminterface.entities.results;

import java.beans.ConstructorProperties;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Represents an entity of the instrumentation state.
 * 
 * @author Alexander Wert
 * 
 */
public final class InstrumentationEntity {
	private final String method;
	private final String probe;

	@ConstructorProperties({"method","probe"})
	@JsonCreator
	public InstrumentationEntity(@JsonProperty("method") final String method, @JsonProperty("probe") final String probe) {
		super();
		this.method = method;
		this.probe = probe;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @return the probe
	 */
	public String getProbe() {
		return probe;
	}

}
