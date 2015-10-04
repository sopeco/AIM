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
package org.aim.mainagent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.aim.aiminterface.description.instrumentation.InstrumentationDescription;
import org.aim.aiminterface.description.instrumentation.InstrumentationEntity;
import org.aim.aiminterface.description.restriction.Restriction;
import org.aim.aiminterface.exceptions.InstrumentationException;
import org.aim.api.instrumentation.MethodsEnclosingScope;
import org.aim.api.instrumentation.Scope;
import org.aim.artifacts.scopes.APIScope;
import org.aim.artifacts.scopes.ConstructorScope;
import org.aim.artifacts.scopes.MethodScope;
import org.aim.artifacts.scopes.TraceScope;
import org.aim.description.builder.InstrumentationDescriptionBuilder;
import org.aim.description.builder.InstrumentationEntityBuilder;
import org.aim.description.builder.RestrictionBuilder;
import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.aim.mainagent.probes.IncrementalInstrumentationProbe;
import org.lpe.common.extension.ExtensionRegistry;

/**
 * Instrumentor for traces using Java 7 enum-based singleton pattern to avoid class loader issues.
 * 
 * @author Alexander Wert
 * 
 */
public enum TraceInstrumentor implements IInstrumentor {
	INSTANCE;
	
	private static final AIMLogger LOGGER = AIMLoggerFactory.getLogger(TraceInstrumentor.class);

	/**
	 * 
	 * @return singleton instance
	 */
	public static synchronized TraceInstrumentor getInstance() {
		return INSTANCE;
	}

	private final Map<Long, Set<String>> incrementalInstrumentationProbes = new HashMap<>();
	private final Map<Long, Restriction> incrementalInstrumentationRestrictions = new HashMap<>();
	private final Set<String> instrumentationFlags = new HashSet<>();
	private final AtomicLong idCounter = new AtomicLong(1);

	/**
	 * Does an incremental step in instrumentation.
	 * 
	 * @param methodName
	 *            method to instrument
	 * @param jobID
	 *            incremental instrumentation job id identifying the
	 *            instrumentation details
	 */
	public void instrumentIncrementally(final String methodName, final long jobID) {
		
		try {
			final String keyString = methodName + "__" + jobID;
			if (!instrumentationFlags.contains(keyString)) {
				LOGGER.info("Incrementally going to instrument method: {}", methodName);
				final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
				final RestrictionBuilder<?> restrictionBuilder = idBuilder.newGlobalRestriction();
				final Restriction incRestriction = incrementalInstrumentationRestrictions.get(jobID);
				if (incRestriction == null) {
					throw new RuntimeException("Expected incremental restriction not found for ID "+jobID);
				}
				for (final String inc : incRestriction.getPackageIncludes()) {
					restrictionBuilder.includePackage(inc);
				}

				for (final String exc : incRestriction.getPackageExcludes()) {
					restrictionBuilder.excludePackage(exc);
				}
				for (final int modifier : incRestriction.getModifierIncludes()) {
					restrictionBuilder.includeModifier(modifier);
				}

				for (final int modifier : incRestriction.getModifierExcludes()) {
					restrictionBuilder.excludeModifier(modifier);
				}
				restrictionBuilder.restrictionDone();

				final InstrumentationEntityBuilder ieBuilder = idBuilder.newMethodScopeEntityWithId(jobID,
						methodName);

				for (final String probe : incrementalInstrumentationProbes.get(jobID)) {
					ieBuilder.addProbe(probe);
				}
				ieBuilder.addProbe(IncrementalInstrumentationProbe.MODEL_PROBE).entityDone();
				final InstrumentationDescription instDescr = idBuilder.build();
				AdaptiveInstrumentationFacade.getInstance().instrument(instDescr);
				instrumentationFlags.add(keyString);
			}
		} catch (final Throwable e) {
			// Catch all exceptions and errors since this code is executed
			// directly from the target application
			LOGGER.error("Error during incremental instrumentation: {}", e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void instrument(final InstrumentationDescription descr) throws InstrumentationException {
		if (!descr.containsScopeType(TraceScope.class)) {
			return;
		}

		for (final InstrumentationEntity instrumentationEntity : descr
				.getInstrumentationEntities(TraceScope.class)) {
			final TraceScope tScope = new TraceScope(
					(MethodsEnclosingScope) ExtensionRegistry.getSingleton().
						getExtension(instrumentationEntity.getScopeDescription().getName()).
						createExtensionArtifact(new Object[]{instrumentationEntity.getScopeDescription()})
					);
			final long scopeId = idCounter.getAndIncrement();
			incrementalInstrumentationProbes.put(scopeId, instrumentationEntity.getProbesAsStrings());

			incrementalInstrumentationRestrictions.put(scopeId, descr.getGlobalRestriction());
			final InstrumentationDescription extendedDescr = getExtendedInstrumentationDescription(descr, tScope,
					instrumentationEntity, scopeId);
			AdaptiveInstrumentationFacade.getInstance().instrument(extendedDescr);
		}

	}

	@Override
	public void undoInstrumentation() throws InstrumentationException {
		incrementalInstrumentationProbes.clear();
		incrementalInstrumentationRestrictions.clear();
		instrumentationFlags.clear();

	}

	private InstrumentationDescription getExtendedInstrumentationDescription(final InstrumentationDescription descr,
			final TraceScope tScope, final InstrumentationEntity eiEntity, final long scopeId)
			throws InstrumentationException {

		final Scope initialScopes = tScope.getSubScope();
		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		final RestrictionBuilder<?> restrictionBuilder = idBuilder.newGlobalRestriction();
		for (final String inc : descr.getGlobalRestriction().getPackageIncludes()) {
			restrictionBuilder.includePackage(inc);
		}
		for (final String exc : descr.getGlobalRestriction().getPackageExcludes()) {
			restrictionBuilder.excludePackage(exc);
		}
		for (final int modifier : descr.getGlobalRestriction().getModifierIncludes()) {
			restrictionBuilder.includeModifier(modifier);
		}
		for (final int modifier : descr.getGlobalRestriction().getModifierExcludes()) {
			restrictionBuilder.excludeModifier(modifier);
		}
		restrictionBuilder.restrictionDone();

		if (initialScopes instanceof MethodScope) {
			buildMethodInstEntity(eiEntity, scopeId, idBuilder, (MethodsEnclosingScope) initialScopes);
		} else if (initialScopes instanceof ConstructorScope) {
			buildConstructorInstEntity(eiEntity, scopeId, idBuilder, (ConstructorScope) initialScopes);
		} else if (initialScopes instanceof APIScope) {
			buildAPIInstEntity(eiEntity, scopeId, idBuilder, (MethodsEnclosingScope) initialScopes);
		} else {
			throw new InstrumentationException("Invalid sub scope type for full trace instrumentation scope: "
					+ initialScopes.getClass().getName());
		}

		return idBuilder.build();
	}

	private void buildAPIInstEntity(final InstrumentationEntity eiEntity, final Long scopeId,
			final InstrumentationDescriptionBuilder idBuilder, final MethodsEnclosingScope iScope) {
		final APIScope apiScope = (APIScope) iScope;
		final InstrumentationEntityBuilder apisBuilder = idBuilder.newAPIScopeEntityWithId(scopeId,
				apiScope.getApiName());
		apisBuilder.addProbe(IncrementalInstrumentationProbe.MODEL_PROBE);
		for (final String probe : eiEntity.getProbesAsStrings()) {
			apisBuilder.addProbe(probe);
		}
		final RestrictionBuilder<?> restrictionBuilder = apisBuilder.newLocalRestriction();
		for (final String exclusion : eiEntity.getLocalRestriction().getPackageExcludes()) {
			restrictionBuilder.excludePackage(exclusion);
		}
		for (final String inclusion : eiEntity.getLocalRestriction().getPackageIncludes()) {
			restrictionBuilder.includePackage(inclusion);
		}
		for (final int modifier : eiEntity.getLocalRestriction().getModifierExcludes()) {
			restrictionBuilder.excludeModifier(modifier);
		}
		for (final int modifier : eiEntity.getLocalRestriction().getModifierIncludes()) {
			restrictionBuilder.includeModifier(modifier);
		}

		restrictionBuilder.restrictionDone();

		apisBuilder.entityDone();
	}

	private void buildConstructorInstEntity(final InstrumentationEntity eiEntity, final Long scopeId,
			final InstrumentationDescriptionBuilder idBuilder, final ConstructorScope cScope) {

		final InstrumentationEntityBuilder csBuilder = idBuilder.newConstructorScopeEntityWithId(scopeId,
				cScope.getTargetClasses());
		csBuilder.addProbe(IncrementalInstrumentationProbe.MODEL_PROBE);
		for (final String probe : eiEntity.getProbesAsStrings()) {
			csBuilder.addProbe(probe);
		}

		final RestrictionBuilder<?> restrictionBuilder = csBuilder.newLocalRestriction();
		for (final String exclusion : eiEntity.getLocalRestriction().getPackageExcludes()) {
			restrictionBuilder.excludePackage(exclusion);
		}
		for (final String inclusion : eiEntity.getLocalRestriction().getPackageIncludes()) {
			restrictionBuilder.includePackage(inclusion);
		}
		for (final int modifier : eiEntity.getLocalRestriction().getModifierExcludes()) {
			restrictionBuilder.excludeModifier(modifier);
		}
		for (final int modifier : eiEntity.getLocalRestriction().getModifierIncludes()) {
			restrictionBuilder.includeModifier(modifier);
		}

		restrictionBuilder.restrictionDone();

		csBuilder.entityDone();
	}

	private void buildMethodInstEntity(final InstrumentationEntity eiEntity, final long scopeId,
			final InstrumentationDescriptionBuilder idBuilder, final MethodsEnclosingScope iScope) {
		final MethodScope mScope = (MethodScope) iScope;
		final InstrumentationEntityBuilder msBuilder = idBuilder.newMethodScopeEntityWithId(scopeId,
				mScope.getMethods());
		msBuilder.addProbe(IncrementalInstrumentationProbe.MODEL_PROBE);
		for (final String probe : eiEntity.getProbesAsStrings()) {
			msBuilder.addProbe(probe);
		}

		final RestrictionBuilder<?> restrictionBuilder = msBuilder.newLocalRestriction();
		for (final String exclusion : eiEntity.getLocalRestriction().getPackageExcludes()) {
			restrictionBuilder.excludePackage(exclusion);
		}
		for (final String inclusion : eiEntity.getLocalRestriction().getPackageIncludes()) {
			restrictionBuilder.includePackage(inclusion);
		}
		for (final int modifier : eiEntity.getLocalRestriction().getModifierExcludes()) {
			restrictionBuilder.excludeModifier(modifier);
		}
		for (final int modifier : eiEntity.getLocalRestriction().getModifierIncludes()) {
			restrictionBuilder.includeModifier(modifier);
		}

		restrictionBuilder.restrictionDone();

		msBuilder.entityDone();
	}

}
