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
package org.aim.description.builder;

import java.util.HashSet;
import java.util.Set;

import org.aim.aiminterface.description.instrumentation.InstrumentationEntity;
import org.aim.aiminterface.description.measurementprobe.MeasurementProbeDescription;
import org.aim.aiminterface.description.restriction.Restriction;
import org.aim.aiminterface.description.scope.ScopeDescription;

/**
 * Builder for an {@link InstrumentationEntity}.
 * 
 * @author Henning Schulz, Steffen Becker
 * 
 */
public class InstrumentationEntityBuilder extends AbstractRestrictableBuilder {

	private final InstrumentationDescriptionBuilder parentBuilder;
	private final ScopeDescription scopeDescription;
	private Restriction localRestriction;
	private final Set<MeasurementProbeDescription> probes = new HashSet<>();
	private boolean isTraced = false;

	/**
	 * Constructor.
	 * 
	 * @param scope
	 *            scope of the instrumentation entity
	 * @param parentBuilder
	 *            builder which called this constructor
	 */
	public InstrumentationEntityBuilder(final ScopeDescription scopeDescription, final InstrumentationDescriptionBuilder parentBuilder) {
		super();
		this.parentBuilder = parentBuilder;
		this.scopeDescription = scopeDescription;
	}

	/**
	 * Adds a new probe to the entity.
	 * 
	 * @param probe
	 *            probe to be added
	 * @return this builder
	 * @see ProbePredefinitions
	 */
	public InstrumentationEntityBuilder addProbe(final MeasurementProbeDescription probe) {
		probes.add(probe);
		return this;
	}

	/**
	 * Adds a new probe to the entity. We assume that the specified probe can be
	 * used with the given scope. Thus, the type of the probe is set to the
	 * given scope. (e.g. if this scope is a {@code MethodScope}, the probe type
	 * is set to {@code MethodScope}).
	 * 
	 * @param probeName
	 *            name of the probe to be added
	 * @return this builder
	 */
	public InstrumentationEntityBuilder addProbe(final String probeName) {
		probes.add(new MeasurementProbeDescription(probeName));
		return this;
	}

	/**
	 * Sets the local restriction.
	 * 
	 * @return this builder
	 */
	public RestrictionBuilder<InstrumentationEntityBuilder> newLocalRestriction() {
		return new RestrictionBuilder<>(this);
	}

	@Override
	protected void setRestriction(final Restriction restriction) {
		this.localRestriction = restriction;
	}
	
	public InstrumentationEntityBuilder enableTrace() {
		this.isTraced = true;
		return this;
	}
	
	/**
	 * Finishes entity building and returns to the parent builder.
	 * 
	 * @return the parent builder
	 */
	public InstrumentationDescriptionBuilder entityDone() {
		parentBuilder.addInstrumentationEntity(
				new InstrumentationEntity(scopeDescription, probes, localRestriction == null ? Restriction.EMPTY_RESTRICTION : localRestriction, isTraced));
		return parentBuilder;
	}

}
