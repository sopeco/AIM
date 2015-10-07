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
package org.aim.aiminterface.description.instrumentation;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.aim.aiminterface.description.restriction.Restriction;
import org.aim.aiminterface.description.sampling.SamplingDescription;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.lpe.common.extension.ExtensionRegistry;
import org.lpe.common.extension.IExtension;

/**
 * This class is a wrapper class for instrumentation descriptions.
 * 
 * 
 * @author Henning Schulz, Steffen Becker
 * 
 */
public class InstrumentationDescription {

	private final Set<InstrumentationEntity> instrumentationEntities;
	private final Set<SamplingDescription> samplingDescriptions;
	private final Restriction globalRestriction;

	/**
	 * Constructor. Initializes all sets with empty sets.
	 */
	@JsonCreator
	@ConstructorProperties({"instrumentationEntities","samplingDescriptions","globalRestriction"})
	public InstrumentationDescription(
			@JsonProperty("instrumentationEntities") final Set<InstrumentationEntity> instrumentationEntities, 
			@JsonProperty("samplingDescriptions") final Set<SamplingDescription> samplingDescriptions, 
			@JsonProperty("globalRestriction") final Restriction globalRestriction) {
		super();
		this.instrumentationEntities = new HashSet<>(instrumentationEntities);
		this.samplingDescriptions = new HashSet<>(samplingDescriptions);
		this.globalRestriction = globalRestriction;
	}

	/**
	 * Returns all instrumentation entities.
	 * 
	 * @return the instrumentation entities
	 */
	public Set<InstrumentationEntity> getInstrumentationEntities() {
		return Collections.unmodifiableSet(instrumentationEntities);
	}

	/**
	 * Returns all instrumentation entities of the given scope type.
	 * 
	 * @param type
	 *            class object of the scope
	 * @return the instrumentation entities of scope type {@code type}
	 * @param <S>
	 *            scope type
	 */
	@JsonIgnore
	public Set<InstrumentationEntity> getInstrumentationEntities(final Class<?> type) {
		final Set<InstrumentationEntity> sEntities = new HashSet<>();

		for (final InstrumentationEntity ie : instrumentationEntities) {
			if (type.getName().equals("org.aim.artifacts.scopes.TraceScope") && ie.isTraced()) {
				sEntities.add(ie);
				continue;
			}
			final IExtension scopeExtension = ExtensionRegistry.getSingleton().getExtension(ie.getScopeDescription().getName());
			if (scopeExtension == null) {
				throw new RuntimeException("Described scope is not loaded as scope extension!");
			}

			if (!ie.isTraced() && type.isAssignableFrom(scopeExtension.getExtensionArtifactClass())) {
				sEntities.add(ie);
			}
		}

		return sEntities;
	}

	/**
	 * Return the global restriction.
	 * 
	 * @return the global restriction
	 */
	public Restriction getGlobalRestriction() {
		return globalRestriction;
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
	public boolean containsScopeType(final Class<? /* extends Scope*/> scopeClass) {
		for (final InstrumentationEntity entity : instrumentationEntities) {
			if (scopeClass.getName().equals("org.aim.artifacts.scopes.TraceScope") && entity.isTraced()) {
				return true;
			}
			final IExtension scopeExtension = ExtensionRegistry.getSingleton().getExtension(entity.getScopeDescription().getName());
			if (scopeExtension == null) {
				throw new RuntimeException("Described scope "+entity.getScopeDescription().getName()+" is not loaded as scope extension!");
			}
			if (!entity.isTraced() && scopeClass.isAssignableFrom(scopeExtension.getExtensionArtifactClass())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Instrumentation Description:\n");
		builder.append("\tInstrumentation Entities:");

		for (final InstrumentationEntity entity : instrumentationEntities) {
			builder.append("\n\t\t* ");
			builder.append(entity.toString());
		}

		if (!getGlobalRestriction().isEmpty()) {
			builder.append("\n\tGlobal Restriction:\n\t\t");
			builder.append(
					getGlobalRestriction().toString().replace(", ", "\n\t\t").replace("+", "+ ").replace("-", "- "));
		}

		builder.append("\n\tSampling Descriptions:");

		for (final SamplingDescription sDesc : samplingDescriptions) {
			builder.append("\n\t\t* ");
			builder.append(sDesc.toString());
		}

		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((globalRestriction == null) ? 0 : globalRestriction.hashCode());
		result = prime * result + ((instrumentationEntities == null) ? 0 : instrumentationEntities.hashCode());
		result = prime * result + ((samplingDescriptions == null) ? 0 : samplingDescriptions.hashCode());
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
		final InstrumentationDescription other = (InstrumentationDescription) obj;
		if (globalRestriction == null) {
			if (other.globalRestriction != null) {
				return false;
			}
		} else if (!globalRestriction.equals(other.globalRestriction)) {
			return false;
		}
		if (instrumentationEntities == null) {
			if (other.instrumentationEntities != null) {
				return false;
			}
		} else if (!instrumentationEntities.equals(other.instrumentationEntities)) {
			return false;
		}
		if (samplingDescriptions == null) {
			if (other.samplingDescriptions != null) {
				return false;
			}
		} else if (!samplingDescriptions.equals(other.samplingDescriptions)) {
			return false;
		}
		return true;
	}
}
