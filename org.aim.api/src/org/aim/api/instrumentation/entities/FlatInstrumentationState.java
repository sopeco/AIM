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

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Flat entity representing the instrumentation state.
 * 
 * @author Alexander Wert
 * 
 */
@XmlRootElement
public class FlatInstrumentationState {
	private List<InstrumentationEntity> iEntities;

	/**
	 * @return the iEntity
	 */
	public List<InstrumentationEntity> getiEntites() {
		return iEntities;
	}

	/**
	 * @param iEntities
	 *            the entities to set
	 */
	public void setiEntities(List<InstrumentationEntity> iEntities) {
		this.iEntities = iEntities;
	}

	/**
	 * Adds an instrumentation entity tot the state.
	 * 
	 * @param method
	 *            instrumented method
	 * @param probe
	 *            injected probe
	 */
	@JsonIgnore
	public void addEntity(String method, String probe) {
		InstrumentationEntity ie = new InstrumentationEntity();
		ie.setMethod(method);
		ie.setProbe(probe);
		iEntities.add(ie);
	}

}
