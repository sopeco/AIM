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
package org.aim.description;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.aim.description.restrictions.Restriction;
import org.aim.description.sampling.SamplingDescription;
import org.aim.description.scopes.Scope;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * This class is a wrapper class for instrumentation descriptions.
 * 
 * 
 * @author Henning Schulz
 * 
 */
@XmlRootElement
public class InstrumentationDescription {
	
	private static final int HASH_PRIME = 31;

	private final Set<InstrumentationEntity<?>> instrumentationEntities;

	private Restriction globalRestriction;

	private final Set<SamplingDescription> samplingDescriptions;

	/**
	 * Constructor. Initializes all sets with empty sets.
	 */
	@JsonCreator
	public InstrumentationDescription() {
		this.instrumentationEntities = new HashSet<>();
		this.samplingDescriptions = new HashSet<>();
	}

	/**
	 * Adds a new instrumentation entity to the description.
	 * 
	 * @param entity
	 *            instrumentation entity to be added
	 */
	public void addInstrumentationEntity(InstrumentationEntity<?> entity) {
		instrumentationEntities.add(entity);
	}

	/**
	 * Returns all instrumentation entities.
	 * 
	 * @return the instrumentation entities
	 */
	public Set<InstrumentationEntity<?>> getInstrumentationEntities() {
		return instrumentationEntities;
	}

	/**
	 * Returns all instrumentation entities of the given scope type.
	 * 
	 * @param type
	 *            class object of the scope
	 * @return the instrumentation entities of scope type {@code type}
	 * @param <S> scope type
	 */
	@SuppressWarnings("unchecked")
	@JsonIgnore
	public <S extends Scope> Set<InstrumentationEntity<S>> getInstrumentationEntities(Class<S> type) {
		Set<InstrumentationEntity<S>> sEntities = new HashSet<>();

		for (InstrumentationEntity<?> ie : instrumentationEntities) {
			if (type.isAssignableFrom(ie.getScope().getClass())) {
				sEntities.add((InstrumentationEntity<S>) ie);
			}
		}

		return sEntities;
	}

	/**
	 * Sets the global restriction.
	 * 
	 * @param restriction
	 *            global restriction to be set
	 */
	public void setGlobalRestriction(Restriction restriction) {
		this.globalRestriction = restriction;
	}

	/**
	 * Return the global restriction.
	 * 
	 * @return the global restriction
	 */
	public Restriction getGlobalRestriction() {
		if (globalRestriction == null) {
			globalRestriction = new Restriction();
		}
		return globalRestriction;
	}

	/**
	 * Adds a sampling description.
	 * 
	 * @param description
	 *            sampling description to be added.
	 */
	public void addSamplingDescription(SamplingDescription description) {
		samplingDescriptions.add(description);
	}

	/**
	 * Returns all sampling descriptions.
	 * 
	 * @return the samplingDescriptions
	 */
	public Set<SamplingDescription> getSamplingDescriptions() {
		return samplingDescriptions;
	}

	/**
	 * Returns, if this instrumentation description contains an entity of the
	 * given scope type.
	 * 
	 * @param scopeClass
	 *            scope class which is to be searched for
	 * @return {@code true}, if there is an entity of the {@code scopeClass}
	 *         type, of {@code false} otherwise
	 */
	public boolean containsScopeType(Class<? extends Scope> scopeClass) {
		for (InstrumentationEntity<?> entity : instrumentationEntities) {
			if (scopeClass.isAssignableFrom(entity.getScope().getClass())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Instrumentation Description:\n");
		builder.append("\tInstrumentation Entities:");

		for (InstrumentationEntity<?> entity : instrumentationEntities) {
			builder.append("\n\t\t* ");
			builder.append(entity.toString());
		}

		if (!getGlobalRestriction().isEmpty()) {
			builder.append("\n\tGlobal Restriction:\n\t\t");
			builder.append(getGlobalRestriction().toString().replace(", ", "\n\t\t").replace("+", "+ ")
					.replace("-", "- "));
		}

		builder.append("\n\tSampling Descriptions:");

		for (SamplingDescription sDesc : samplingDescriptions) {
			builder.append("\n\t\t* ");
			builder.append(sDesc.toString());
		}

		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}

		InstrumentationDescription other = (InstrumentationDescription) obj;
		return this.getGlobalRestriction().equals(other.getGlobalRestriction())
				&& this.getInstrumentationEntities().equals(other.getInstrumentationEntities())
				&& this.getSamplingDescriptions().equals(other.getSamplingDescriptions());
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * HASH_PRIME + getInstrumentationEntities().hashCode();
		hash = hash * HASH_PRIME + getSamplingDescriptions().hashCode();
		hash = hash * HASH_PRIME + getGlobalRestriction().hashCode();
		return hash;
	}

}
