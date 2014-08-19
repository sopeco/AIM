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

import org.aim.api.exceptions.InstrumentationException;
import org.aim.description.InstrumentationDescription;
import org.aim.description.InstrumentationEntity;
import org.aim.description.builder.InstrumentationDescriptionBuilder;
import org.aim.description.builder.InstrumentationEntityBuilder;
import org.aim.description.builder.RestrictionBuilder;
import org.aim.description.restrictions.Restriction;
import org.aim.description.scopes.APIScope;
import org.aim.description.scopes.ConstructorScope;
import org.aim.description.scopes.CustomScope;
import org.aim.description.scopes.MethodScope;
import org.aim.description.scopes.MethodsEnclosingScope;
import org.aim.description.scopes.TraceScope;
import org.aim.mainagent.probes.IncrementalInstrumentationProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instrumentor for traces.
 * 
 * @author Alexander Wert
 * 
 */
public final class TraceInstrumentor implements IInstrumentor {
	private static final Logger LOGGER = LoggerFactory.getLogger(TraceInstrumentor.class);
	private static TraceInstrumentor instance;

	/**
	 * 
	 * @return singleton instance
	 */
	public static synchronized TraceInstrumentor getInstance() {
		if (instance == null) {
			instance = new TraceInstrumentor();
		}
		return instance;
	}

	private final Map<Long, Set<String>> incrementalInstrumentationProbes;
	private final Map<Long, Restriction> incrementalInstrumentationRestrictions;
	private final Set<String> instrumentationFlags;
	private volatile long idCounter = 0;

	private TraceInstrumentor() {
		incrementalInstrumentationProbes = new HashMap<>();
		incrementalInstrumentationRestrictions = new HashMap<>();
		instrumentationFlags = new HashSet<>();
	}

	/**
	 * Does an incremental step in instrumentation.
	 * 
	 * @param methodName
	 *            method to instrument
	 * @param jobID
	 *            incremental instrumentation job id identifying the
	 *            instrumentation details
	 */
	public void instrumentIncrementally(String methodName, long jobID) {
		LOGGER.info("Incrementally going to instrument method: {}", methodName);
		try {
			String keyString = methodName + "__" + jobID;
			if (!instrumentationFlags.contains(keyString)) {

				InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
				RestrictionBuilder<?> restrictionBuilder = idBuilder.newGlobalRestriction();
				for (String inc : incrementalInstrumentationRestrictions.get(jobID).getPackageIncludes()) {
					restrictionBuilder.includePackage(inc);
				}

				for (String exc : incrementalInstrumentationRestrictions.get(jobID).getPackageExcludes()) {
					restrictionBuilder.excludePackage(exc);
				}
				for (int modifier : incrementalInstrumentationRestrictions.get(jobID).getModifierIncludes()) {
					restrictionBuilder.includeModifier(modifier);
				}

				for (int modifier : incrementalInstrumentationRestrictions.get(jobID).getModifierExcludes()) {
					restrictionBuilder.excludeModifier(modifier);
				}
				restrictionBuilder.restrictionDone();

				InstrumentationEntityBuilder<MethodScope> ieBuilder = idBuilder.newMethodScopeEntityWithId(jobID,
						methodName);

				for (String probe : incrementalInstrumentationProbes.get(jobID)) {
					ieBuilder.addProbe(probe);
					ieBuilder.addProbe(IncrementalInstrumentationProbe.MODEL_PROBE);
				}
				ieBuilder.entityDone();
				InstrumentationDescription instDescr = idBuilder.build();
				AdaptiveInstrumentationFacade.getInstance().instrument(instDescr);
				instrumentationFlags.add(keyString);
			}
		} catch (Throwable e) {
			// Catch all exceptions and errors since this code is executed
			// directly from the target application
			LOGGER.error("Error during incremental instrumentation: {}", e);

		}
	}

	@Override
	public void instrument(InstrumentationDescription descr) throws InstrumentationException {
		if (!descr.containsScopeType(TraceScope.class)) {
			return;
		}

		for (InstrumentationEntity<TraceScope> instrumentationEntity : descr
				.getInstrumentationEntities(TraceScope.class)) {
			TraceScope tScope = instrumentationEntity.getScope();
			long scopeId = idCounter++;
			incrementalInstrumentationProbes.put(scopeId, instrumentationEntity.getProbesAsStrings());

			Restriction restriction = new Restriction();
			restriction.getPackageExcludes().addAll(descr.getGlobalRestriction().getPackageExcludes());
			restriction.getPackageIncludes().addAll(descr.getGlobalRestriction().getPackageIncludes());
			restriction.getModifierExcludes().addAll(descr.getGlobalRestriction().getModifierExcludes());
			restriction.getModifierIncludes().addAll(descr.getGlobalRestriction().getModifierIncludes());

			incrementalInstrumentationRestrictions.put(scopeId, restriction);
			InstrumentationDescription extendedDescr = getExtendedInstrumentationDescription(descr, tScope,
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

	private InstrumentationDescription getExtendedInstrumentationDescription(InstrumentationDescription descr,
			TraceScope tScope, InstrumentationEntity<TraceScope> eiEntity, Long scopeId)
			throws InstrumentationException {

		MethodsEnclosingScope initialScopes = tScope.getSubScope();
		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		RestrictionBuilder<?> restrictionBuilder = idBuilder.newGlobalRestriction();
		for (String inc : descr.getGlobalRestriction().getPackageIncludes()) {
			restrictionBuilder.includePackage(inc);
		}
		for (String exc : descr.getGlobalRestriction().getPackageExcludes()) {
			restrictionBuilder.excludePackage(exc);
		}
		for (int modifier : descr.getGlobalRestriction().getModifierIncludes()) {
			restrictionBuilder.includeModifier(modifier);
		}
		for (int modifier : descr.getGlobalRestriction().getModifierExcludes()) {
			restrictionBuilder.excludeModifier(modifier);
		}
		restrictionBuilder.restrictionDone();

		if (initialScopes instanceof MethodScope) {
			buildMethodInstEntity(eiEntity, scopeId, idBuilder, initialScopes);
		} else if (initialScopes instanceof ConstructorScope) {
			buildConstructorInstEntity(eiEntity, scopeId, idBuilder, initialScopes);
		} else if (initialScopes instanceof APIScope) {
			buildAPIInstEntity(eiEntity, scopeId, idBuilder, initialScopes);
		} else if (initialScopes instanceof CustomScope) {
			buildCustomInstEntity(eiEntity, scopeId, idBuilder, initialScopes);
		} else {
			throw new InstrumentationException("Invalid sub scope type for full trace instrumentation scope: "
					+ initialScopes.getClass().getName());
		}

		return idBuilder.build();
	}

	private void buildCustomInstEntity(InstrumentationEntity<TraceScope> eiEntity, Long scopeId,
			InstrumentationDescriptionBuilder idBuilder, MethodsEnclosingScope iScope) {
		CustomScope cScope = (CustomScope) iScope;
		InstrumentationEntityBuilder<CustomScope> csBuilder = idBuilder.newCustomScopeEntityWithId(scopeId,
				cScope.getScopeName());

		csBuilder.addProbe(IncrementalInstrumentationProbe.MODEL_PROBE);
		for (String probe : eiEntity.getProbesAsStrings()) {
			csBuilder.addProbe(probe);
		}
		RestrictionBuilder<?> restrictionBuilder = csBuilder.newLocalRestriction();
		for (String exclusion : eiEntity.getLocalRestriction().getPackageExcludes()) {
			restrictionBuilder.excludePackage(exclusion);
		}
		for (String inclusion : eiEntity.getLocalRestriction().getPackageIncludes()) {
			restrictionBuilder.includePackage(inclusion);
		}
		for (int modifier : eiEntity.getLocalRestriction().getModifierExcludes()) {
			restrictionBuilder.excludeModifier(modifier);
		}
		for (int modifier : eiEntity.getLocalRestriction().getModifierIncludes()) {
			restrictionBuilder.includeModifier(modifier);
		}

		restrictionBuilder.restrictionDone();

		csBuilder.entityDone();
	}

	private void buildAPIInstEntity(InstrumentationEntity<TraceScope> eiEntity, Long scopeId,
			InstrumentationDescriptionBuilder idBuilder, MethodsEnclosingScope iScope) {
		APIScope apiScope = (APIScope) iScope;
		InstrumentationEntityBuilder<APIScope> apisBuilder = idBuilder.newAPIScopeEntityWithId(scopeId,
				apiScope.getApiName());
		apisBuilder.addProbe(IncrementalInstrumentationProbe.MODEL_PROBE);
		for (String probe : eiEntity.getProbesAsStrings()) {
			apisBuilder.addProbe(probe);
		}
		RestrictionBuilder<?> restrictionBuilder = apisBuilder.newLocalRestriction();
		for (String exclusion : eiEntity.getLocalRestriction().getPackageExcludes()) {
			restrictionBuilder.excludePackage(exclusion);
		}
		for (String inclusion : eiEntity.getLocalRestriction().getPackageIncludes()) {
			restrictionBuilder.includePackage(inclusion);
		}
		for (int modifier : eiEntity.getLocalRestriction().getModifierExcludes()) {
			restrictionBuilder.excludeModifier(modifier);
		}
		for (int modifier : eiEntity.getLocalRestriction().getModifierIncludes()) {
			restrictionBuilder.includeModifier(modifier);
		}

		restrictionBuilder.restrictionDone();

		apisBuilder.entityDone();
	}

	private void buildConstructorInstEntity(InstrumentationEntity<TraceScope> eiEntity, Long scopeId,
			InstrumentationDescriptionBuilder idBuilder, MethodsEnclosingScope iScope) {
		ConstructorScope cScope = (ConstructorScope) iScope;

		InstrumentationEntityBuilder<ConstructorScope> csBuilder = idBuilder.newConstructorScopeEntityWithId(scopeId,
				cScope.getTargetClasses());
		csBuilder.addProbe(IncrementalInstrumentationProbe.MODEL_PROBE);
		for (String probe : eiEntity.getProbesAsStrings()) {
			csBuilder.addProbe(probe);
		}

		RestrictionBuilder<?> restrictionBuilder = csBuilder.newLocalRestriction();
		for (String exclusion : eiEntity.getLocalRestriction().getPackageExcludes()) {
			restrictionBuilder.excludePackage(exclusion);
		}
		for (String inclusion : eiEntity.getLocalRestriction().getPackageIncludes()) {
			restrictionBuilder.includePackage(inclusion);
		}
		for (int modifier : eiEntity.getLocalRestriction().getModifierExcludes()) {
			restrictionBuilder.excludeModifier(modifier);
		}
		for (int modifier : eiEntity.getLocalRestriction().getModifierIncludes()) {
			restrictionBuilder.includeModifier(modifier);
		}

		restrictionBuilder.restrictionDone();

		csBuilder.entityDone();
	}

	private void buildMethodInstEntity(InstrumentationEntity<TraceScope> eiEntity, Long scopeId,
			InstrumentationDescriptionBuilder idBuilder, MethodsEnclosingScope iScope) {
		MethodScope mScope = (MethodScope) iScope;
		InstrumentationEntityBuilder<MethodScope> msBuilder = idBuilder.newMethodScopeEntityWithId(scopeId,
				mScope.getMethods());
		msBuilder.addProbe(IncrementalInstrumentationProbe.MODEL_PROBE);
		for (String probe : eiEntity.getProbesAsStrings()) {
			msBuilder.addProbe(probe);
		}

		RestrictionBuilder<?> restrictionBuilder = msBuilder.newLocalRestriction();
		for (String exclusion : eiEntity.getLocalRestriction().getPackageExcludes()) {
			restrictionBuilder.excludePackage(exclusion);
		}
		for (String inclusion : eiEntity.getLocalRestriction().getPackageIncludes()) {
			restrictionBuilder.includePackage(inclusion);
		}
		for (int modifier : eiEntity.getLocalRestriction().getModifierExcludes()) {
			restrictionBuilder.excludeModifier(modifier);
		}
		for (int modifier : eiEntity.getLocalRestriction().getModifierIncludes()) {
			restrictionBuilder.includeModifier(modifier);
		}

		restrictionBuilder.restrictionDone();

		msBuilder.entityDone();
	}

}
