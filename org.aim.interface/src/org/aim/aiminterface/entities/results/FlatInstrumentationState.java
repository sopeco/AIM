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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Flat entity representing the instrumentation state.
 * 
 * @author Alexander Wert
 * 
 */
public final class FlatInstrumentationState {
	private final List<InstrumentationEntity> iEntities;
	
	@ConstructorProperties({"instrumentationEntities"})
	@JsonCreator
	public FlatInstrumentationState(@JsonProperty("instrumentationEntities") final List<InstrumentationEntity> iEntities) {
		super();
		this.iEntities = new ArrayList<>(iEntities);
	}

	/**
	 * @return the iEntity
	 */
	public List<InstrumentationEntity> getInstrumentationEntities() {
		return Collections.unmodifiableList(iEntities);
	}
}
