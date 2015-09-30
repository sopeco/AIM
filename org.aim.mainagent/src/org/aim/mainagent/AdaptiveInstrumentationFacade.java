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
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.aim.aiminterface.description.instrumentation.InstrumentationDescription;
import org.aim.aiminterface.entities.results.FlatInstrumentationState;
import org.aim.aiminterface.entities.results.SupportedExtensions;
import org.aim.aiminterface.exceptions.InstrumentationException;
import org.aim.api.events.AbstractEventProbeExtension;
import org.aim.api.instrumentation.AbstractCustomScopeExtension;
import org.aim.api.instrumentation.AbstractEnclosingProbeExtension;
import org.aim.api.instrumentation.AbstractInstApiScopeExtension;
import org.aim.api.instrumentation.InstrumentationUtilsController;
import org.aim.api.instrumentation.description.internal.FlatInstrumentationEntity;
import org.aim.api.measurement.sampling.AbstractSamplerExtension;
import org.aim.artifacts.scopes.AllocationScope;
import org.aim.artifacts.scopes.ConstructorScope;
import org.aim.artifacts.scopes.MemoryScope;
import org.aim.artifacts.scopes.MethodScope;
import org.aim.artifacts.scopes.SynchronizedScope;
import org.aim.artifacts.scopes.TraceScope;
import org.aim.mainagent.probes.IncrementalProbeExtension;
import org.lpe.common.extension.ExtensionRegistry;
import org.lpe.common.extension.IExtension;

/**
 * Coordinates the instrumentation process.
 * 
 * @author Alexander Wert
 * 
 */
public final class AdaptiveInstrumentationFacade implements AdaptiveInstrumentationFacadeMBean {
	private static AdaptiveInstrumentationFacade instance;

	/**
	 * Singleton instance.
	 * 
	 * @return singleton instance
	 */
	public static synchronized AdaptiveInstrumentationFacadeMBean getInstance() {
		if (instance == null) {
			instance = new AdaptiveInstrumentationFacade();
		}
		return instance;
	}

	private final MethodInstrumentor methodInstrumentor;
	private final TraceInstrumentor traceInstrumentor;
	private final EventInstrumentor eventInstrumentor;

	private SupportedExtensions extensions = null;

	private AdaptiveInstrumentationFacade() {
		methodInstrumentor = new MethodInstrumentor();

		traceInstrumentor = TraceInstrumentor.getInstance();
		eventInstrumentor = EventInstrumentor.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.aim.mainagent.IAdaptiveInstrumentation#instrument(org.aim.description
	 * .InstrumentationDescription)
	 */
	@Override
	public synchronized void instrument(final InstrumentationDescription instrumentationDescription)
			throws InstrumentationException {
		
		// TODO FIXME
//		instrumentationDescription.getGlobalRestriction().addPackageExclude(InstrumentationConstants.AIM_PACKAGE);
//		instrumentationDescription.getGlobalRestriction().addPackageExclude(InstrumentationConstants.JAVA_PACKAGE);
//		instrumentationDescription.getGlobalRestriction().addPackageExclude(InstrumentationConstants.JAVASSIST_PACKAGE);
//		instrumentationDescription.getGlobalRestriction().addPackageExclude(InstrumentationConstants.JAVAX_PACKAGE);
//		instrumentationDescription.getGlobalRestriction()
//				.addPackageExclude(InstrumentationConstants.LPE_COMMON_PACKAGE);

		methodInstrumentor.instrument(instrumentationDescription);
		traceInstrumentor.instrument(instrumentationDescription);
		eventInstrumentor.instrument(instrumentationDescription);

		// TODO: add Statement Instrumentation

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aim.mainagent.IAdaptiveInstrumentation#undoInstrumentation()
	 */
	@Override
	public synchronized void undoInstrumentation() {
		try {
			methodInstrumentor.undoInstrumentation();
			traceInstrumentor.undoInstrumentation();
			eventInstrumentor.undoInstrumentation();
		} catch (final InstrumentationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		InstrumentationUtilsController.getInstance().clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aim.mainagent.IAdaptiveInstrumentation#getInstrumentationState()
	 */
	@Override
	public synchronized FlatInstrumentationState getInstrumentationState() {
		final FlatInstrumentationState fmInstrumentation = new FlatInstrumentationState();
		for (final FlatInstrumentationEntity fie : methodInstrumentor.getCurrentInstrumentationState()) {
			fmInstrumentation.addEntity(fie.getMethodSignature(), fie.getProbeType().getName());
		}
		return fmInstrumentation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aim.mainagent.IAdaptiveInstrumentation#getSupportedExtensions()
	 */
	@Override
	public synchronized SupportedExtensions getSupportedExtensions() throws InstrumentationException {
		if (extensions != null) {
			return extensions;
		}

		
		final Set<Class<?>> knownConcreteScopeClasses = new HashSet<>();
		knownConcreteScopeClasses.add(AllocationScope.class);
		knownConcreteScopeClasses.add(ConstructorScope.class);
		knownConcreteScopeClasses.add(MemoryScope.class);
		knownConcreteScopeClasses.add(MethodScope.class);
		knownConcreteScopeClasses.add(SynchronizedScope.class);
		knownConcreteScopeClasses.add(TraceScope.class);

		final Map<String, Set<Class<?>>> probeToSupportedScopesMapping = new HashMap<>();
		final LinkedList<String> customScopes = new LinkedList<>();
		final LinkedList<String> apiScopes = new LinkedList<>();
		final LinkedList<String> sampler = new LinkedList<>();
		
		for (final IExtension extension : ExtensionRegistry.getSingleton().getExtensions()) {
			if (extension instanceof AbstractCustomScopeExtension) {
				customScopes.add(extension.getName());
				knownConcreteScopeClasses.add(extension.createExtensionArtifact().getClass());
			} else if (extension instanceof AbstractEnclosingProbeExtension) {
				if (extension instanceof IncrementalProbeExtension) {
					continue;
				}
				probeToSupportedScopesMapping.put(extension.getName(),
						((AbstractEnclosingProbeExtension) extension).getScopeDependencies());
			} else if (extension instanceof AbstractEventProbeExtension) {
				probeToSupportedScopesMapping.put(extension.getName(),
						((AbstractEventProbeExtension) extension).getScopeDependencies());
			} else if (extension instanceof AbstractInstApiScopeExtension) {
				apiScopes.add(extension.getName());
				knownConcreteScopeClasses.add(extension.createExtensionArtifact().getClass());
			} else if (extension instanceof AbstractSamplerExtension) {
				sampler.add(extension.getName());
			}
		}

		final Map<String, Set<String>> probeToRegisteredScopesMapping = filterProbeScopeMappingToExistingScopes(knownConcreteScopeClasses,
				probeToSupportedScopesMapping);

		return extensions = new SupportedExtensions(sampler, apiScopes, probeToRegisteredScopesMapping, customScopes);
	}

	private Map<String, Set<String>> filterProbeScopeMappingToExistingScopes(
			final Set<Class<?>> knownConcreteScopeClasses, final Map<String, Set<Class<?>>> tempProbeScopeMapping) {
		final Map<String,Set<String>> probeMap = new HashMap<String, Set<String>>();
		for (final String probeName : tempProbeScopeMapping.keySet()) {
			final Set<String> scopes = new HashSet<>();
			for (final Class<?> concreteScope : knownConcreteScopeClasses) {
				supportedScopeLoop: for (final Class<?> supportedScope : tempProbeScopeMapping.get(probeName)) {
					if (supportedScope.isAssignableFrom(concreteScope)) {
						scopes.add(concreteScope.getName());
						break supportedScopeLoop;
					}
				}
			}

			probeMap.put(probeName, scopes);
		}
		return probeMap;
	}

}
