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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.aim.aiminterface.description.measurementprobe.MeasurementProbeDescription;
import org.aim.aiminterface.description.restriction.Restriction;
import org.aim.aiminterface.description.scope.ScopeDescription;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This is a wrapper class for instrumentation entities, composed of one scope
 * and several probes.
 * 
 * @param <S>
 *            scope type
 * 
 * @author Henning Schulz, Steffen Becker
 * 
 */
public class InstrumentationEntity {

	private final ScopeDescription scopeDescription;

	private final Set<MeasurementProbeDescription> probeDescriptions;

	private final Restriction localRestriction;
	
	private final boolean isTraced;

	/**
	 * Constructor. Initializes the probe set with an empty one.
	 * 
	 * @param scopeDescription
	 *            scope to be set.
	 */
	@JsonCreator
	public InstrumentationEntity(
			@JsonProperty("scopeDescription") final ScopeDescription scopeDescription,
			@JsonProperty("probeDescriptions") final Set<MeasurementProbeDescription> probeDescriptions,
			@JsonProperty("localRestriction") final Restriction localRestriction,
			@JsonProperty("traced") final boolean isTraced) {
		super();
		if (scopeDescription == null || localRestriction == null) {
			throw new IllegalArgumentException();
		}
		this.scopeDescription = scopeDescription;
		this.probeDescriptions = new HashSet<>(probeDescriptions == null ? Collections.<MeasurementProbeDescription> emptySet() : probeDescriptions);
		this.localRestriction = localRestriction;
		this.isTraced = isTraced;
	}

	public boolean isTraced() {
		return isTraced;
	}

	/**
	 * @return the scope
	 */
	public ScopeDescription getScopeDescription() {
		return scopeDescription;
	}

	/**
	 * @return the probes
	 */
	public Set<MeasurementProbeDescription> getProbeDescriptions() {
		return Collections.unmodifiableSet(probeDescriptions);
	}

	/**
	 * Returns the probes as strings (names of the probes).
	 * 
	 * @return the probes as strings
	 */
	@JsonIgnore
	public Set<String> getProbesAsStrings() {
		final Set<String> stringSet = new HashSet<>();
		for (final MeasurementProbeDescription mProbe : probeDescriptions) {
			stringSet.add(mProbe.getName());

		}
		return stringSet;
	}

	/**
	 * @return the local restriction
	 */
	public Restriction getLocalRestriction() {
		return localRestriction;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		boolean trailingComma = false;

		if (isTraced) {
			builder.append("TraceScope [");
		}
		builder.append(scopeDescription.toString());
		if (isTraced) {
			builder.append("]");
		}

		if (!getLocalRestriction().isEmpty()) {
			builder.append(" (");
			builder.append(getLocalRestriction().toString());
			builder.append(")");
		}

		builder.append(": ");

		for (final MeasurementProbeDescription probe : probeDescriptions) {
			builder.append(probe.getName());
			builder.append(", ");
			trailingComma = true;
		}

		if (trailingComma) {
			builder.deleteCharAt(builder.length() - 1);
			builder.deleteCharAt(builder.length() - 1);
		}

		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((localRestriction == null) ? 0 : localRestriction.hashCode());
		result = prime * result + ((probeDescriptions == null) ? 0 : probeDescriptions.hashCode());
		result = prime * result + ((scopeDescription == null) ? 0 : scopeDescription.hashCode());
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
		final InstrumentationEntity other = (InstrumentationEntity) obj;
		if (localRestriction == null) {
			if (other.localRestriction != null) {
				return false;
			}
		} else if (!localRestriction.equals(other.localRestriction)) {
			return false;
		}
		if (probeDescriptions == null) {
			if (other.probeDescriptions != null) {
				return false;
			}
		} else if (!probeDescriptions.equals(other.probeDescriptions)) {
			return false;
		}
		if (scopeDescription == null) {
			if (other.scopeDescription != null) {
				return false;
			}
		} else if (!scopeDescription.equals(other.scopeDescription)) {
			return false;
		}
		return true;
	}

}
