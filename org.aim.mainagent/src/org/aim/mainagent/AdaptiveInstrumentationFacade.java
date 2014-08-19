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

import java.lang.reflect.Modifier;

import org.aim.api.exceptions.InstrumentationException;
import org.aim.api.exceptions.MeasurementException;
import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.AbstractInstAPIScope;
import org.aim.api.instrumentation.IScopeAnalyzer;
import org.aim.api.instrumentation.InstrumentationUtilsController;
import org.aim.api.instrumentation.description.internal.FlatInstrumentationEntity;
import org.aim.api.instrumentation.description.internal.InstrumentationConstants;
import org.aim.api.instrumentation.entities.FlatInstrumentationState;
import org.aim.api.instrumentation.entities.SupportedExtensions;
import org.aim.api.measurement.sampling.ISampler;
import org.aim.description.InstrumentationDescription;
import org.aim.mainagent.instrumentor.JInstrumentation;
import org.aim.mainagent.sampling.Sampling;

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
			for (Class<?> clazz : JInstrumentation.getInstance().getjInstrumentation().getAllLoadedClasses()) {
				if (!Modifier.isAbstract(clazz.getModifiers()) && !Modifier.isInterface(clazz.getModifiers())) {
					if (ISampler.class.isAssignableFrom(clazz)) {
						extensions.getSamplerExtensions().add(clazz.getName());
					} else if (AbstractInstAPIScope.class.isAssignableFrom(clazz)) {
						extensions.getApiScopeExtensions().add(clazz.getName());
					} else if (AbstractEnclosingProbe.class.isAssignableFrom(clazz)) {
						extensions.getEnclosingProbeExtensions().add(clazz.getName());
					} else if (IScopeAnalyzer.class.isAssignableFrom(clazz)) {
						extensions.getCustomScopeExtensions().add(clazz.getName());
					}
					// TODO: add singlePointProbe case
				}

			}
		}

		return extensions;
	}

}
