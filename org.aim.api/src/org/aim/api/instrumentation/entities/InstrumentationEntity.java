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
package org.aim.api.instrumentation.entities;

/**
 * Represents an entity of the instrumentation state.
 * 
 * @author Alexander Wert
 * 
 */
public class InstrumentationEntity {
	private String method;
	private String probe;

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method
	 *            the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return the probe
	 */
	public String getProbe() {
		return probe;
	}

	/**
	 * @param probe
	 *            the probe to set
	 */
	public void setProbe(String probe) {
		this.probe = probe;
	}

}
