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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aim.aiminterface.description.instrumentation.InstrumentationDescription;
import org.aim.aiminterface.description.instrumentation.InstrumentationEntity;
import org.aim.aiminterface.exceptions.InstrumentationException;
import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.AbstractEnclosingProbeExtension;
import org.aim.api.instrumentation.IScopeAnalyzer;
import org.aim.api.instrumentation.MethodsEnclosingScope;
import org.aim.api.instrumentation.description.internal.FlatInstrumentationEntity;
import org.aim.api.instrumentation.description.internal.FlatScopeEntity;
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
	private final InstrumentationDescription instrumentationDescription;

	/**
	 * Constructor.
	 * 
	 * @param instDescription
	 *            instrumentation description
	 */
	public ScopeAnalysisController(final InstrumentationDescription instDescription) {
		super();
		this.instrumentationDescription = instDescription;
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
	public Set<FlatInstrumentationEntity> resolveScopes(final Collection<Class<?>> allLoadedClasses) throws InstrumentationException {
		final Collection<Class<?>> filteredClasses = removeGlobalyExcludedClasses(allLoadedClasses);

		final Map<IScopeAnalyzer, Set<String>> scopeAnalyzersToProbesMap = createScopeAnalyzerToProbesMapping(allLoadedClasses);
		final Set<FlatInstrumentationEntity> instrumentationEntities = new HashSet<>();
		final Map<String, Class<? extends AbstractEnclosingProbe>> probeClasses = new HashMap<>();
		for (final Entry<IScopeAnalyzer, Set<String>> mapEntry : scopeAnalyzersToProbesMap.entrySet()) {
			final IScopeAnalyzer sAnalyzer = mapEntry.getKey();
			final Set<String> probes = mapEntry.getValue();
			final Set<FlatScopeEntity> scopeEntities = new HashSet<>();
			for (final Class<?> clazz : filteredClasses) {
				try {
					sAnalyzer.visitClass(clazz, scopeEntities);
				} catch (final Throwable t) {
					LOGGER.warn("failed to instrument class {}. Ignoring and resuming instrumentation.",
							clazz.getName());
				}

			}

			for (final FlatScopeEntity fse : scopeEntities) {
				for (final String probe : probes) {

					Class<? extends AbstractEnclosingProbe> probeClass = null;
					if (!probeClasses.containsKey(probe)) {
						final IExtension ext = ExtensionRegistry.getSingleton().getExtension(probe);
						if (ext == null) {
							throw new InstrumentationException("Failed loading Probe class " + probe);
						}
						final AbstractEnclosingProbeExtension probeExtension = (AbstractEnclosingProbeExtension) ext;
						probeClass = (Class<? extends AbstractEnclosingProbe>) probeExtension.getExtensionArtifactClass();
						probeClasses.put(probe, probeClass);
					} else {
						probeClass = probeClasses.get(probe);
					}
					final FlatInstrumentationEntity fiEntity = new FlatInstrumentationEntity(fse, probeClass);
					fiEntity.setScopeId(sAnalyzer.getScopeId());

					instrumentationEntities.add(fiEntity);
				}
			}
		}

		return instrumentationEntities;

	}

	@SuppressWarnings("rawtypes")
	private Collection<Class<?>> removeGlobalyExcludedClasses(final Collection<Class<?>> allLoadedClasses) {
		final List<Class<?>> toKeep = new ArrayList<>();

		for (final Class clazz : allLoadedClasses) {
			boolean invalidClass = false;
			try {
				final String className = clazz.getName();
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
					} catch (final ClassNotFoundException cnfe) {
						invalidClass = true;
					}
				}
			} catch (final Throwable t) {
				invalidClass = true;
			}
			if (!invalidClass) {
				toKeep.add(clazz);
			}

		}
		return toKeep;
	}

	private Map<IScopeAnalyzer, Set<String>> createScopeAnalyzerToProbesMapping(final Collection<Class<?>> allLoadedClasses)
			throws InstrumentationException {
		final Map<IScopeAnalyzer, Set<String>> mapping = new HashMap<>();
		for (final InstrumentationEntity mScopeEntity : instrumentationDescription.getInstrumentationEntities(MethodsEnclosingScope.class)) {
			Object[] args;
			if (mScopeEntity.getScopeDescription().getParameter().size() == 0) {
				args = new Object[]{};
			} else if (mScopeEntity.getScopeDescription().getParameter().size() == 1) {
				args = new Object[]{mScopeEntity.getScopeDescription().getParameter().get(0)};
			} else {
				args = new Object[]{mScopeEntity.getScopeDescription().getParameter().toArray(new String[]{})};
			}
			final MethodsEnclosingScope methodsEnclosingScope = ExtensionRegistry.getSingleton().getExtension(mScopeEntity.getScopeDescription().getName()).
					createExtensionArtifact(args);
			final IScopeAnalyzer scopeAnalyzer = methodsEnclosingScope.getScopeAnalyzer(allLoadedClasses);

			if (scopeAnalyzer != null) {
				scopeAnalyzer.setRestriction(mScopeEntity.getLocalRestriction().mergeWith(instrumentationDescription.getGlobalRestriction()));
				scopeAnalyzer.setScopeId(mScopeEntity.getScopeDescription().getId());
				mapping.put(scopeAnalyzer, mScopeEntity.getProbesAsStrings());
			}

		}
		return mapping;
	}
}
