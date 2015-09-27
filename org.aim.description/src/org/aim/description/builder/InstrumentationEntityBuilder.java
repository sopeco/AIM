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

import org.aim.aiminterface.description.instrumentation.InstrumentationEntity;
import org.aim.aiminterface.description.measurementprobe.MeasurementProbe;
import org.aim.aiminterface.description.restriction.Restriction;
import org.aim.aiminterface.description.scope.Scope;
import org.aim.aiminterface.description.scope.ScopeType;

/**
 * Builder for an {@link InstrumentationEntity}.
 * 
 * @author Henning Schulz, Steffen Becker
 * 
 */
public class InstrumentationEntityBuilder extends AbstractRestrictableBuilder {

	private final InstrumentationEntity entity;
	private final InstrumentationDescriptionBuilder parentBuilder;

	/**
	 * Constructor.
	 * 
	 * @param scope
	 *            scope of the instrumentation entity
	 * @param parentBuilder
	 *            builder which called this constructor
	 */
	public InstrumentationEntityBuilder(final Scope scope, final InstrumentationDescriptionBuilder parentBuilder) {
		super();
		entity = new InstrumentationEntity(scope);
		this.parentBuilder = parentBuilder;
	}

	/**
	 * Adds a new probe to the entity.
	 * 
	 * @param probe
	 *            probe to be added
	 * @return this builder
	 * @see ProbePredefinitions
	 */
	public InstrumentationEntityBuilder addProbe(final MeasurementProbe probe) {
		entity.addProbe(probe);
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
	public InstrumentationEntityBuilder addProbe(final String probeName, final ScopeType scopeType) {
		entity.addProbe(new MeasurementProbe(probeName,scopeType));
		return this;
	}

	/**
	 * Sets the local restriction.
	 * 
	 * @return this builder
	 */
	public RestrictionBuilder<InstrumentationEntityBuilder> newLocalRestriction() {
		return new RestrictionBuilder<>(this, entity.getLocalRestriction());
	}

	@Override
	protected void setRestriction(final Restriction restriction) {
		entity.setLocalRestriction(restriction);
	}
	
	/**
	 * Finishes entity building and returns to the parent builder.
	 * 
	 * @return the parent builder
	 */
	public InstrumentationDescriptionBuilder entityDone() {
		parentBuilder.addInstrumentationEntity(entity);
		return parentBuilder;
	}

	public InstrumentationEntityBuilder addProbe(final String probeName) {
		entity.addProbe(new MeasurementProbe(probeName));
		return this;
	}

}
