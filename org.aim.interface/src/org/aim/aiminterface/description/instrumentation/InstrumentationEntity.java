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

import org.aim.aiminterface.description.measurementprobe.MeasurementProbe;
import org.aim.aiminterface.description.restriction.Restriction;
import org.aim.aiminterface.description.scope.Scope;
import org.codehaus.jackson.annotate.JsonIgnore;

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
// @JsonDeserialize(using = InstrumentationEntityDeserializer.class)
// @JsonSerialize(using = InstrumentationEntitySerializer.class)
public class InstrumentationEntity {

	private final Scope scope;

	private final Set<MeasurementProbe> probes;

	private Restriction localRestriction;

	/**
	 * Constructor. Initializes the probe set with an empty one.
	 * 
	 * @param scope
	 *            scope to be set.
	 */
	public InstrumentationEntity(final Scope scope) {
		super();
		this.scope = scope;
		this.probes = new HashSet<>();
	}

	/**
	 * @return the scope
	 */
	public Scope getScope() {
		return scope;
	}

	/**
	 * Adds a new probe.
	 * 
	 * @param probe
	 *            new probe to be added
	 */
	public void addProbe(final MeasurementProbe probe) {
		if (probe == null) {
			throw new IllegalArgumentException("Invalid probe added to Instrumentation entity");
		}
		if (!probe.getRequiredScopeTypes().isEmpty() && !probe.getRequiredScopeTypes().contains(this.scope.getScopeType())) {
			throw new IllegalArgumentException("Probe added to instrumentation entity does not support the entitie's scope");
		}
		probes.add(probe);
	}

	/**
	 * @return the probes
	 */
	public Set<MeasurementProbe> getProbes() {
		return Collections.unmodifiableSet(probes);
	}

	/**
	 * Returns the probes as strings (names of the probes).
	 * 
	 * @return the probes as strings
	 */
	@JsonIgnore
	public Set<String> getProbesAsStrings() {
		final Set<String> stringSet = new HashSet<>();
		for (final MeasurementProbe mProbe : probes) {
			stringSet.add(mProbe.getName());

		}
		return stringSet;
	}

	/**
	 * Sets the local restriction.
	 * 
	 * @param restriction
	 *            local restriction to be set
	 */
	public void setLocalRestriction(final Restriction restriction) {
		this.localRestriction = restriction;
	}

	/**
	 * @return the local restriction
	 */
	@JsonIgnore
	public Restriction getLocalRestriction() {
		if (localRestriction == null) {
			localRestriction = new Restriction();
		}
		return localRestriction;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		boolean trailingComma = false;

		builder.append(scope.toString());

		if (!getLocalRestriction().isEmpty()) {
			builder.append(" (");
			builder.append(getLocalRestriction().toString());
			builder.append(")");
		}

		builder.append(": ");

		for (final MeasurementProbe probe : probes) {
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
		result = prime * result + ((probes == null) ? 0 : probes.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
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
		if (probes == null) {
			if (other.probes != null) {
				return false;
			}
		} else if (!probes.equals(other.probes)) {
			return false;
		}
		if (scope == null) {
			if (other.scope != null) {
				return false;
			}
		} else if (!scope.equals(other.scope)) {
			return false;
		}
		return true;
	}

}
