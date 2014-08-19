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

import org.aim.description.json.InstrumentationEntityDeserializer;
import org.aim.description.json.InstrumentationEntitySerializer;
import org.aim.description.probes.MeasurementProbe;
import org.aim.description.restrictions.Restriction;
import org.aim.description.scopes.Scope;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * This is a wrapper class for instrumentation entities, composed of one scope
 * and several probes.
 * 
 * @param <S>
 *            scope type
 * 
 * @author Henning Schulz
 * 
 */
@JsonDeserialize(using = InstrumentationEntityDeserializer.class)
@JsonSerialize(using = InstrumentationEntitySerializer.class)
public class InstrumentationEntity<S extends Scope> {

	private static final int HASH_PRIME = 31;

	private final S scope;

	private Set<MeasurementProbe<? super S>> probes;

	private Restriction localRestriction;

	/**
	 * Constructor. Initializes the probe set with an empty one.
	 * 
	 * @param scope
	 *            scope to be set.
	 */
	public InstrumentationEntity(S scope) {
		this.scope = scope;
		this.probes = new HashSet<>();
	}

	/**
	 * @return the scope
	 */
	public S getScope() {
		return scope;
	}

	/**
	 * Adds a new probe.
	 * 
	 * @param probe
	 *            new probe to be added
	 */
	public void addProbe(MeasurementProbe<? super S> probe) {
		probes.add(probe);
	}

	/**
	 * @return the probes
	 */
	public Set<MeasurementProbe<? super S>> getProbes() {
		return probes;
	}

	/**
	 * Returns the probes as strings (names of the probes).
	 * 
	 * @return the probes as strings
	 */
	@JsonIgnore
	public Set<String> getProbesAsStrings() {
		Set<String> stringSet = new HashSet<>();
		for (MeasurementProbe<?> mProbe : probes) {
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
	public void setLocalRestriction(Restriction restriction) {
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
		StringBuilder builder = new StringBuilder();
		boolean trailingComma = false;

		builder.append(scope.toString());

		if (!getLocalRestriction().isEmpty()) {
			builder.append(" (");
			builder.append(getLocalRestriction().toString());
			builder.append(")");
		}

		builder.append(": ");

		for (MeasurementProbe<? super S> probe : probes) {
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
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}

		InstrumentationEntity<?> other = (InstrumentationEntity<?>) obj;
		return this.getScope().equals(other.getScope()) && this.getProbes().equals(other.getProbes())
				&& this.getLocalRestriction().equals(other.getLocalRestriction());
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * HASH_PRIME + getLocalRestriction().hashCode();
		hash = hash * HASH_PRIME + getProbes().hashCode();
		hash = hash * HASH_PRIME + getScope().hashCode();
		return hash;
	}

}
