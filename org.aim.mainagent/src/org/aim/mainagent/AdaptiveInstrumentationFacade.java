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

import org.aim.api.events.AbstractEventProbeExtension;
import org.aim.api.exceptions.InstrumentationException;
import org.aim.api.exceptions.MeasurementException;
import org.aim.api.instrumentation.AbstractCustomScopeExtension;
import org.aim.api.instrumentation.AbstractEnclosingProbeExtension;
import org.aim.api.instrumentation.AbstractInstApiScopeExtension;
import org.aim.api.instrumentation.InstrumentationUtilsController;
import org.aim.api.instrumentation.description.internal.FlatInstrumentationEntity;
import org.aim.api.instrumentation.description.internal.InstrumentationConstants;
import org.aim.api.instrumentation.entities.FlatInstrumentationState;
import org.aim.api.instrumentation.entities.SupportedExtensions;
import org.aim.api.measurement.sampling.AbstractSamplerExtension;
import org.aim.description.InstrumentationDescription;
import org.aim.description.scopes.AllocationScope;
import org.aim.description.scopes.ConstructorScope;
import org.aim.description.scopes.MemoryScope;
import org.aim.description.scopes.MethodScope;
import org.aim.description.scopes.SynchronizedScope;
import org.aim.description.scopes.TraceScope;
import org.aim.mainagent.probes.IncrementalProbeExtension;
import org.aim.mainagent.sampling.Sampling;
import org.lpe.common.extension.ExtensionRegistry;
import org.lpe.common.extension.IExtension;

/**
 * Coordinates the instrumentation process.
 * 
 * @author Alexander Wert
 * 
 */
public final class AdaptiveInstrumentationFacade {
	private static AdaptiveInstrumentationFacade instance;

	/**
	 * Singleton instance.
	 * 
	 * @return singleton instance
	 */
	public static synchronized AdaptiveInstrumentationFacade getInstance() {
		if (instance == null) {
			instance = new AdaptiveInstrumentationFacade();
		}
		return instance;
	}

	private MethodInstrumentor methodInstrumentor;
	private TraceInstrumentor traceInstrumentor;
	private EventInstrumentor eventInstrumentor;

	private SupportedExtensions extensions = null;

	private AdaptiveInstrumentationFacade() {
		methodInstrumentor = new MethodInstrumentor();

		traceInstrumentor = TraceInstrumentor.getInstance();
		eventInstrumentor = EventInstrumentor.getInstance();
	}

	/**
	 * Instruments the target application according to the passed
	 * instrumentation. Note, instrumentation is additive, thus, calling this
	 * method several times with different instrumentation descriptions results
	 * in a joined instrumentation.
	 * 
	 * @param instrumentationDescription
	 *            describes the instrumentation to be made
	 * @throws InstrumentationException
	 *             if instrumentation fails
	 */
	public synchronized void instrument(InstrumentationDescription instrumentationDescription)
			throws InstrumentationException {
		instrumentationDescription.getGlobalRestriction().addPackageExclude(InstrumentationConstants.AIM_PACKAGE);
		instrumentationDescription.getGlobalRestriction().addPackageExclude(InstrumentationConstants.JAVA_PACKAGE);
		instrumentationDescription.getGlobalRestriction().addPackageExclude(InstrumentationConstants.JAVASSIST_PACKAGE);
		instrumentationDescription.getGlobalRestriction().addPackageExclude(InstrumentationConstants.JAVAX_PACKAGE);
		instrumentationDescription.getGlobalRestriction()
				.addPackageExclude(InstrumentationConstants.LPE_COMMON_PACKAGE);

		methodInstrumentor.instrument(instrumentationDescription);
		traceInstrumentor.instrument(instrumentationDescription);
		eventInstrumentor.instrument(instrumentationDescription);

		// TODO: add Statement Instrumentation

		if (!instrumentationDescription.getSamplingDescriptions().isEmpty()) {
			try {
				Sampling.getInstance().addMonitoringJob(instrumentationDescription.getSamplingDescriptions());
			} catch (MeasurementException e) {
				throw new InstrumentationException(e);
			}
		}
	}

	/**
	 * Reverts all instrumentation steps.
	 * 
	 * @throws InstrumentationException
	 *             if instrumentation fails
	 */
	public synchronized void undoInstrumentation() throws InstrumentationException {
		methodInstrumentor.undoInstrumentation();
		traceInstrumentor.undoInstrumentation();
		eventInstrumentor.undoInstrumentation();

		Sampling.getInstance().clearMonitoringJobs();
		InstrumentationUtilsController.getInstance().clear();
	}

	/**
	 * Retrieves the flat instrumentation state.
	 * 
	 * @return flat instrumentation state
	 */
	public synchronized FlatInstrumentationState getInstrumentationState() {
		FlatInstrumentationState fmInstrumentation = new FlatInstrumentationState();
		for (FlatInstrumentationEntity fie : methodInstrumentor.getCurrentInstrumentationState()) {
			fmInstrumentation.addEntity(fie.getMethodSignature(), fie.getProbeType().getName());
		}
		return fmInstrumentation;
	}

	/**
	 * retrieve supported extensions.
	 * 
	 * @return Object wrapping all extensions supported by this agent.
	 * @throws InstrumentationException
	 *             thrown if extensions cannot be retrieved
	 */
	public synchronized SupportedExtensions getSupportedExtensions() throws InstrumentationException {
		if (extensions == null) {
			extensions = new SupportedExtensions();

			Set<Class<?>> knownConcreteScopeClasses = new HashSet<>();
			knownConcreteScopeClasses.add(AllocationScope.class);
			knownConcreteScopeClasses.add(ConstructorScope.class);
			knownConcreteScopeClasses.add(MemoryScope.class);
			knownConcreteScopeClasses.add(MethodScope.class);
			knownConcreteScopeClasses.add(SynchronizedScope.class);
			knownConcreteScopeClasses.add(TraceScope.class);

			Map<String, Set<Class<?>>> tempProbeScopeMapping = new HashMap<>();

			for (IExtension<?> extension : ExtensionRegistry.getSingleton().getExtensions()) {
				if (extension instanceof AbstractCustomScopeExtension) {
					extensions.getCustomScopeExtensions().add(extension.getName());
					knownConcreteScopeClasses.add(extension.createExtensionArtifact().getClass());
				} else if (extension instanceof AbstractEnclosingProbeExtension) {
					if (extension instanceof IncrementalProbeExtension) {
						continue;
					}
					tempProbeScopeMapping.put(extension.getName(),
							((AbstractEnclosingProbeExtension) extension).getScopeDependencies());
				} else if (extension instanceof AbstractEventProbeExtension) {
					tempProbeScopeMapping.put(extension.getName(),
							((AbstractEventProbeExtension) extension).getScopeDependencies());
				} else if (extension instanceof AbstractInstApiScopeExtension) {
					extensions.getApiScopeExtensions().add(extension.getName());
					knownConcreteScopeClasses.add(extension.createExtensionArtifact().getClass());
				} else if (extension instanceof AbstractSamplerExtension) {
					extensions.getSamplerExtensions().add(extension.getName());
				}
			}

			for (String probeName : tempProbeScopeMapping.keySet()) {
				Set<String> scopes = new HashSet<>();
				for (Class<?> concreteScope : knownConcreteScopeClasses) {
					supportedScopeLoop: for (Class<?> supportedScope : tempProbeScopeMapping.get(probeName)) {
						if (supportedScope.isAssignableFrom(concreteScope)) {
							scopes.add(concreteScope.getName());
							break supportedScopeLoop;
						}
					}
				}

				extensions.addProbeExtension(probeName, scopes);
			}
		}

		return extensions;
	}

}
