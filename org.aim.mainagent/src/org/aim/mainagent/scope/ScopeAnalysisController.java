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
package org.aim.mainagent.scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aim.api.exceptions.InstrumentationException;
import org.aim.api.instrumentation.AbstractCustomScopeExtension;
import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.AbstractEnclosingProbeExtension;
import org.aim.api.instrumentation.AbstractInstAPIScope;
import org.aim.api.instrumentation.AbstractInstApiScopeExtension;
import org.aim.api.instrumentation.IScopeAnalyzer;
import org.aim.api.instrumentation.description.internal.FlatInstrumentationEntity;
import org.aim.api.instrumentation.description.internal.FlatScopeEntity;
import org.aim.description.InstrumentationDescription;
import org.aim.description.InstrumentationEntity;
import org.aim.description.restrictions.Restriction;
import org.aim.description.scopes.APIScope;
import org.aim.description.scopes.ConstructorScope;
import org.aim.description.scopes.CustomScope;
import org.aim.description.scopes.MethodScope;
import org.aim.description.scopes.MethodsEnclosingScope;
import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.lpe.common.extension.ExtensionRegistry;
import org.lpe.common.extension.IExtension;

/**
 * Analyzes the whole scope.
 * 
 * @author Alexander Wert
 * 
 */
public class ScopeAnalysisController {
	private static final AIMLogger LOGGER = AIMLoggerFactory.getLogger(ScopeAnalysisController.class);
	private InstrumentationDescription instrumentationDescription;

	/**
	 * Constructor.
	 * 
	 * @param instDescription
	 *            instrumentation description
	 */
	public ScopeAnalysisController(InstrumentationDescription instDescription) {
		instrumentationDescription = instDescription;
	}

	/**
	 * Creates flat internal instrumentation description.
	 * 
	 * @param allLoadedClasses
	 *            all classes loaded in the JVM
	 * @return set of flat instrumentation entities
	 * @throws InstrumentationException
	 *             if scope cannot be resolved
	 */
	@SuppressWarnings({ "rawtypes" })
	public Set<FlatInstrumentationEntity> resolveScopes(List<Class> allLoadedClasses) throws InstrumentationException {
		List<Class> filteredClasses = removeGlobalyExcludedClasses(allLoadedClasses);

		Map<IScopeAnalyzer, Set<String>> scopeAnalyzersToProbesMap = createScopeAnalyzerToProbesMapping(allLoadedClasses);
		Set<FlatInstrumentationEntity> instrumentationEntities = new HashSet<>();
		Map<String, Class<? extends AbstractEnclosingProbe>> probeClasses = new HashMap<>();
		for (Entry<IScopeAnalyzer, Set<String>> mapEntry : scopeAnalyzersToProbesMap.entrySet()) {
			IScopeAnalyzer sAnalyzer = mapEntry.getKey();
			Set<String> probes = mapEntry.getValue();
			Set<FlatScopeEntity> scopeEntities = new HashSet<>();
			for (Class<?> clazz : filteredClasses) {
				try {
					sAnalyzer.visitClass(clazz, scopeEntities);
				} catch (Throwable t) {
					LOGGER.warn("failed to instrument class {}. Ignoring and resuming instrumentation.",
							clazz.getName());
				}

			}

			for (FlatScopeEntity fse : scopeEntities) {
				for (String probe : probes) {

					Class<? extends AbstractEnclosingProbe> probeClass = null;
					if (!probeClasses.containsKey(probe)) {
						IExtension<?> ext = ExtensionRegistry.getSingleton().getExtension(probe);
						if (ext == null) {
							throw new InstrumentationException("Failed loading Probe class " + probe);
						}
						AbstractEnclosingProbeExtension probeExtension = (AbstractEnclosingProbeExtension) ext;
						probeClass = probeExtension.getProbeClass();
						probeClasses.put(probe, probeClass);
					} else {
						probeClass = probeClasses.get(probe);
					}
					FlatInstrumentationEntity fiEntity = new FlatInstrumentationEntity(fse, probeClass);
					fiEntity.setScopeId(sAnalyzer.getScopeId());

					instrumentationEntities.add(fiEntity);
				}
			}
		}

		return instrumentationEntities;

	}

	@SuppressWarnings("rawtypes")
	private List<Class> removeGlobalyExcludedClasses(List<Class> allLoadedClasses) {
		List<Class> toKeep = new ArrayList<>();

		for (Class clazz : allLoadedClasses) {
			boolean invalidClass = false;
			try {
				String className = clazz.getName();
				if (instrumentationDescription.getGlobalRestriction().isExcluded(className)) { // TODO:
																								// use
																								// IDM
					invalidClass = true;
				} else if (clazz.getClassLoader() == null) {
					invalidClass = true;
				} else if (clazz.isInterface() || clazz.isPrimitive() || clazz.isArray() || clazz.isAnnotation()
						|| clazz.isAnonymousClass() || clazz.isEnum() || clazz.isSynthetic() || clazz.isLocalClass()) {
					invalidClass = true;
				} else {
					try {
						clazz.getClassLoader().loadClass(this.getClass().getName());
					} catch (ClassNotFoundException cnfe) {
						invalidClass = true;
					}
				}
			} catch (Throwable t) {
				invalidClass = true;
			}
			if (!invalidClass) {
				toKeep.add(clazz);
			}

		}
		return toKeep;
	}

	@SuppressWarnings("rawtypes")
	private Map<IScopeAnalyzer, Set<String>> createScopeAnalyzerToProbesMapping(List<Class> allLoadedClasses)
			throws InstrumentationException {
		Map<IScopeAnalyzer, Set<String>> mapping = new HashMap<>();
		for (InstrumentationEntity<MethodsEnclosingScope> mScopeEntity : instrumentationDescription
				.getInstrumentationEntities(MethodsEnclosingScope.class)) {
			IScopeAnalyzer scopeAnalyzer = null;
			if (mScopeEntity.getScope() instanceof MethodScope) {
				scopeAnalyzer = new MethodScopeAnalyzer(((MethodScope) mScopeEntity.getScope()).getMethods());
			} else if (mScopeEntity.getScope() instanceof ConstructorScope) {
				scopeAnalyzer = new ConstructorScopeAnalyzer(
						((ConstructorScope) mScopeEntity.getScope()).getTargetClasses());
			} else if (mScopeEntity.getScope() instanceof CustomScope) {
				String scopeName = ((CustomScope) mScopeEntity.getScope()).getScopeName();
				scopeAnalyzer = ExtensionRegistry.getSingleton().getExtensionArtifact(
						AbstractCustomScopeExtension.class, scopeName);
				if (scopeAnalyzer == null) {
					throw new InstrumentationException("Unable to instantiate scope analyzer for custom scope type "
							+ scopeName);
				}
			} else if (mScopeEntity.getScope() instanceof APIScope) {
				String apiName = ((APIScope) mScopeEntity.getScope()).getApiName();

				AbstractInstAPIScope apiScopeInstance = ExtensionRegistry.getSingleton().getExtensionArtifact(
						AbstractInstApiScopeExtension.class, apiName);

				if (apiScopeInstance == null) {
					throw new InstrumentationException("Unable to instantiate scope analyzer for API scope type "
							+ apiName);
				}

				scopeAnalyzer = new APIScopeAnalyzer(apiScopeInstance, allLoadedClasses);
			} else {
				continue;
			}

			Restriction combinedRestriction = new Restriction();

			combinedRestriction.getModifierIncludes().addAll(mScopeEntity.getLocalRestriction().getModifierIncludes());
			combinedRestriction.getModifierIncludes().addAll(
					instrumentationDescription.getGlobalRestriction().getModifierIncludes());
			combinedRestriction.getModifierExcludes().addAll(mScopeEntity.getLocalRestriction().getModifierExcludes());
			combinedRestriction.getModifierExcludes().addAll(
					instrumentationDescription.getGlobalRestriction().getModifierExcludes());

			combinedRestriction.getPackageIncludes().addAll(mScopeEntity.getLocalRestriction().getPackageIncludes());
			combinedRestriction.getPackageIncludes().addAll(
					instrumentationDescription.getGlobalRestriction().getPackageIncludes());
			combinedRestriction.getPackageExcludes().addAll(mScopeEntity.getLocalRestriction().getPackageExcludes());
			combinedRestriction.getPackageExcludes().addAll(
					instrumentationDescription.getGlobalRestriction().getPackageExcludes());

			scopeAnalyzer.setRestriction(combinedRestriction);
			scopeAnalyzer.setScopeId(mScopeEntity.getScope().getId());
			mapping.put(scopeAnalyzer, mScopeEntity.getProbesAsStrings());

		}
		return mapping;
	}
}
