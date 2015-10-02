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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aim.aiminterface.description.instrumentation.InstrumentationDescription;
import org.aim.aiminterface.description.restriction.Restriction;
import org.aim.aiminterface.entities.results.FlatInstrumentationState;
import org.aim.aiminterface.entities.results.InstrumentationEntity;
import org.aim.aiminterface.entities.results.OverheadData;
import org.aim.aiminterface.entities.results.OverheadRecord;
import org.aim.aiminterface.entities.results.SupportedExtensions;
import org.aim.aiminterface.exceptions.InstrumentationException;
import org.aim.aiminterface.exceptions.MeasurementException;
import org.aim.api.events.AbstractEventProbeExtension;
import org.aim.api.instrumentation.AbstractCustomScopeExtension;
import org.aim.api.instrumentation.AbstractEnclosingProbeExtension;
import org.aim.api.instrumentation.AbstractInstApiScopeExtension;
import org.aim.api.instrumentation.InstrumentationUtilsController;
import org.aim.api.instrumentation.description.internal.FlatInstrumentationEntity;
import org.aim.api.instrumentation.description.internal.InstrumentationConstants;
import org.aim.api.measurement.collector.AbstractDataSource;
import org.aim.api.measurement.collector.IDataCollector;
import org.aim.api.measurement.sampling.AbstractSamplerExtension;
import org.aim.mainagent.probes.IncrementalProbeExtension;
import org.aim.mainagent.sampling.Sampling;
import org.lpe.common.extension.ExtensionRegistry;
import org.lpe.common.extension.IExtension;
import org.overhead.OverheadEstimator;

/**
 * Coordinates the instrumentation process.
 * 
 * @author Alexander Wert
 * 
 */
public final class AdaptiveInstrumentationFacade implements AdaptiveInstrumentationFacadeMXBean {
	private static AdaptiveInstrumentationFacade instance;

	/**
	 * Singleton instance.
	 * 
	 * @return singleton instance
	 */
	public static synchronized AdaptiveInstrumentationFacadeMXBean getInstance() {
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
		final Set<String> newPackageExcludes = new HashSet<>(instrumentationDescription.getGlobalRestriction().getPackageExcludes());
		newPackageExcludes.add(InstrumentationConstants.AIM_PACKAGE);
		newPackageExcludes.add(InstrumentationConstants.JAVA_PACKAGE);
		newPackageExcludes.add(InstrumentationConstants.JAVASSIST_PACKAGE);
		newPackageExcludes.add(InstrumentationConstants.JAVAX_PACKAGE);
		newPackageExcludes.add(InstrumentationConstants.LPE_COMMON_PACKAGE);
		final Restriction adaptedRestriction = new Restriction(
				instrumentationDescription.getGlobalRestriction().getPackageIncludes(), 
				newPackageExcludes, 
				instrumentationDescription.getGlobalRestriction().getModifierIncludes(), 
				instrumentationDescription.getGlobalRestriction().getModifierExcludes(),
				instrumentationDescription.getGlobalRestriction().getGranularity());
		final InstrumentationDescription adaptedDescription = new InstrumentationDescription(
				instrumentationDescription.getInstrumentationEntities(), 
				instrumentationDescription.getSamplingDescriptions(), 
				adaptedRestriction);

		methodInstrumentor.instrument(adaptedDescription);
		traceInstrumentor.instrument(adaptedDescription);
		eventInstrumentor.instrument(adaptedDescription);

		// TODO: add Statement Instrumentation
		if (!instrumentationDescription.getSamplingDescriptions().isEmpty()) {
			try {
				Sampling.getInstance().addMonitoringJob(instrumentationDescription.getSamplingDescriptions());
			} catch (final MeasurementException e) {
				throw new InstrumentationException(e);
			}
		}
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
		Sampling.getInstance().clearMonitoringJobs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aim.mainagent.IAdaptiveInstrumentation#getInstrumentationState()
	 */
	@Override
	public synchronized FlatInstrumentationState getInstrumentationState() {
		final List<InstrumentationEntity> iEntities = new LinkedList<InstrumentationEntity>();
		for (final FlatInstrumentationEntity fie : methodInstrumentor.getCurrentInstrumentationState()) {
			iEntities.add(new InstrumentationEntity(fie.getMethodSignature(), fie.getProbeType().getName()));
		}
		return new FlatInstrumentationState(iEntities);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aim.mainagent.IAdaptiveInstrumentation#getSupportedExtensions()
	 */
	@Override
	public synchronized SupportedExtensions getSupportedExtensions()  {
		if (extensions != null) {
			return extensions;
		}

		
		final Set<Class<?>> knownConcreteScopeClasses = new HashSet<>();

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

	@Override
	public void enableMonitoring() throws MeasurementException {
		final IDataCollector collector = AbstractDataSource.getDefaultDataSource();

		collector.enable();
		Sampling.getInstance().start();
	}

	@Override
	public void disableMonitoring() throws MeasurementException {
		final IDataCollector collector = AbstractDataSource.getDefaultDataSource();

		collector.disable();
		Sampling.getInstance().stop();
	}

	@Override
	public byte[] getMeasurementData() throws MeasurementException {
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		AbstractDataSource.getDefaultDataSource().pipeToOutputStream(outputStream);
		return outputStream.toByteArray();
	}

	@Override
	public long getCurrentTime() {
		return System.currentTimeMillis();
	}

	@Override
	public OverheadData measureProbeOverhead(final String probeClassName) throws InstrumentationException {
		final OverheadData oData = new OverheadData();

		final List<OverheadRecord> records = OverheadEstimator.measureOverhead(probeClassName);
		oData.setoRecords(records);

		return oData;
	}

}
