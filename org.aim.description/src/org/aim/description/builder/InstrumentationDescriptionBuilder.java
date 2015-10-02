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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.aim.aiminterface.description.instrumentation.InstrumentationDescription;
import org.aim.aiminterface.description.instrumentation.InstrumentationEntity;
import org.aim.aiminterface.description.restriction.Restriction;
import org.aim.aiminterface.description.sampling.SamplingDescription;
import org.aim.aiminterface.description.scope.ScopeDescription;
import org.aim.artifacts.scopes.APIScope;
import org.aim.artifacts.scopes.AllocationScope;
import org.aim.artifacts.scopes.ConstructorScope;
import org.aim.artifacts.scopes.MemoryScope;
import org.aim.artifacts.scopes.MethodScope;
import org.aim.artifacts.scopes.SynchronizedScope;

/**
 * Builder for {@link InstrumentationDescription}s.
 * 
 * @author Henning Schulz, Steffen Becker
 * 
 */
public class InstrumentationDescriptionBuilder extends AbstractRestrictableBuilder {

	private final Set<InstrumentationEntity> instrumentationEntities = new HashSet<>();
	private final Set<SamplingDescription> samplingDescriptions = new HashSet<>();
	private Restriction globalRestriction;
	
	/**
	 * Constructor.
	 */
	public InstrumentationDescriptionBuilder() {
		super();
	}

	/**
	 * Adds an instrumentation entity.
	 * 
	 * @param entity
	 *            entity to add
	 */
	protected void addInstrumentationEntity(final InstrumentationEntity entity) {
		instrumentationEntities.add(entity);
	}

	/**
	 * Starts definition of the global restriction.
	 * 
	 * @return a {@link RestrictionBuilder}
	 */
	public RestrictionBuilder<InstrumentationDescriptionBuilder> newGlobalRestriction() {
		return new RestrictionBuilder<>(this);
	}

	@Override
	protected void setRestriction(final Restriction restriction) {
		this.globalRestriction = restriction;
	}

	/**
	 * Adds a new {@link SamplingDescription}.
	 * 
	 * @param resource
	 *            resource to be sampled
	 * @param delay
	 *            sampling delay
	 * @return this builder
	 */
	public InstrumentationDescriptionBuilder newSampling(final String resource, final long delay) {
		samplingDescriptions.add(new SamplingDescription(resource, delay));
		return this;
	}

	/**
	 * Builds and returns the instrumentation description.
	 * 
	 * @return the built instrumentation description.
	 */
	public InstrumentationDescription build() {
		return new InstrumentationDescription(
				instrumentationEntities, 
				samplingDescriptions, 
				globalRestriction == null ? Restriction.EMPTY_RESTRICTION : globalRestriction);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link MethodScope}.
	 * 
	 * @param patterns
	 *            methods in the method scope
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder newMethodScopeEntity(final String... patterns) {
		return new InstrumentationEntityBuilder(getScopeDescription(MethodScope.class, patterns),this);
	}

	private ScopeDescription getScopeDescription(final Class<?> scopeClass, final String... patterns) {
		return new ScopeDescription(scopeClass.getName(), 0, Arrays.asList(patterns));
	}

	private ScopeDescription getScopeDescription(final String scopeClass, final String... patterns) {
		return new ScopeDescription(scopeClass, 0, Arrays.asList(patterns));
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link MemoryScope}.
	 * 
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder newMemoryScopeEntity() {
		return new InstrumentationEntityBuilder(getScopeDescription(MemoryScope.class),this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with an
	 * {@link AllocationScope}.
	 * 
	 * @param classes
	 *            classes of the allocation scope
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder newAllocationScopeEntity(final String... classes) {
		return new InstrumentationEntityBuilder(getScopeDescription(AllocationScope.class, classes), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link ConstructorScope}.
	 * 
	 * @param classes
	 *            classes of the constructor scope
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder newConstructorScopeEntity(final String... classes) {
		return new InstrumentationEntityBuilder(getScopeDescription(ConstructorScope.class, classes), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link SynchronizedScope}.
	 * 
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder newSynchronizedScopeEntity() {
		return new InstrumentationEntityBuilder(getScopeDescription(SynchronizedScope.class), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with an
	 * {@link APIScope}.
	 * 
	 * @param apiName
	 *            name of the API
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder newAPIScopeEntity(final String apiName) {
		return new InstrumentationEntityBuilder(getScopeDescription(apiName), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link MethodScope}.
	 * 
	 * @param id
	 *            scope id
	 * @param patterns
	 *            methods in the method scope
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder newMethodScopeEntityWithId(final long id, final String... patterns) {
		return new InstrumentationEntityBuilder(getScopeDescription(MemoryScope.class, patterns), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link MemoryScope}.
	 * 
	 * @param id
	 *            scope id
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder newMemoryScopeEntityWithId(final long id) {
		return new InstrumentationEntityBuilder(getScopeDescription(MemoryScope.class), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with an
	 * {@link AllocationScope}.
	 * 
	 * @param id
	 *            scope id
	 * @param classes
	 *            classes of the allocation scope
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder newAllocationScopeEntityWithId(final long id, final String... classes) {
		return new InstrumentationEntityBuilder(getScopeDescription(AllocationScope.class, classes), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link ConstructorScope}.
	 * 
	 * @param id
	 *            scope id
	 * @param classes
	 *            classes of the constructor scope
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder newConstructorScopeEntityWithId(final long id, final String... classes) {
		return new InstrumentationEntityBuilder(getScopeDescription(ConstructorScope.class, classes), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with a
	 * {@link SynchronizedScope}.
	 * 
	 * @param id
	 *            scope id
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder newSynchronizedScopeEntityWithId(final long id) {
		return new InstrumentationEntityBuilder(getScopeDescription(SynchronizedScope.class), this);
	}

	/**
	 * Starts definition of an {@link InstrumentationEntity} with an
	 * {@link APIScope}.
	 * 
	 * @param id
	 *            scope id
	 * @param apiName
	 *            name of the API
	 * @return an {@link InstrumentationEntityBuilder}
	 */
	public InstrumentationEntityBuilder newAPIScopeEntityWithId(final long id, final String apiName) {
		return new InstrumentationEntityBuilder(getScopeDescription(APIScope.class, apiName), this);
	}

	/**
	 * Appends the passed instrumentation description to the current
	 * instrumentation description represented by this builder. Does nothing if
	 * passed instrumentation description is null.
	 * 
	 * @param instDescription
	 *            instrumentation description to append
	 */
	public void appendOtherDescription(final InstrumentationDescription instDescription) {
		if (instDescription == null) {
			return;
		}
		final Restriction globalRestriction = instDescription.getGlobalRestriction();
		final RestrictionBuilder<InstrumentationDescriptionBuilder> globalRestrictionBuilder = newGlobalRestriction();
		for (final String inc : globalRestriction.getPackageIncludes()) {
			globalRestrictionBuilder.includePackage(inc);
		}
		for (final String exc : globalRestriction.getPackageExcludes()) {
			globalRestrictionBuilder.excludePackage(exc);
		}
		for (final int modifier : globalRestriction.getModifierIncludes()) {
			globalRestrictionBuilder.includeModifier(modifier);
		}
		for (final int modifier : globalRestriction.getModifierExcludes()) {
			globalRestrictionBuilder.excludeModifier(modifier);
		}
		globalRestrictionBuilder.setGranularity(globalRestriction.getGranularity());
		globalRestrictionBuilder.restrictionDone();

		for (final InstrumentationEntity entity : instDescription.getInstrumentationEntities()) {
			addInstrumentationEntity(entity);
		}

		for (final SamplingDescription sDescr : instDescription.getSamplingDescriptions()) {
			newSampling(sDescr.getResourceName(), sDescr.getDelay());
		}

	}
}
