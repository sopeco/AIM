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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aim.aiminterface.description.instrumentation.InstrumentationDescription;
import org.aim.aiminterface.entities.measurements.MeasurementData;
import org.aim.aiminterface.entities.results.FlatInstrumentationState;
import org.aim.aiminterface.entities.results.InstrumentationEntity;
import org.aim.aiminterface.entities.results.OverheadData;
import org.aim.aiminterface.entities.results.OverheadRecord;
import org.aim.aiminterface.entities.results.SupportedExtensions;
import org.aim.aiminterface.exceptions.InstrumentationException;
import org.aim.aiminterface.exceptions.MeasurementException;
import org.aim.api.events.AbstractEventProbeExtension;
import org.aim.api.instrumentation.AbstractEnclosingProbeExtension;
import org.aim.api.instrumentation.AbstractInstApiScopeExtension;
import org.aim.api.instrumentation.AbstractScopeExtension;
import org.aim.api.instrumentation.InstrumentationUtilsController;
import org.aim.api.instrumentation.Scope;
import org.aim.api.instrumentation.description.internal.FlatInstrumentationEntity;
import org.aim.api.instrumentation.description.internal.InstrumentationConstants;
import org.aim.api.measurement.collector.AbstractDataSource;
import org.aim.api.measurement.collector.IDataCollector;
import org.aim.api.measurement.sampling.AbstractSamplerExtension;
import org.aim.description.builder.InstrumentationDescriptionBuilder;
import org.aim.description.builder.RestrictionBuilder;
import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.aim.logging.LoggingLevel;
import org.aim.mainagent.instrumentor.EventInstrumentor;
import org.aim.mainagent.instrumentor.IInstrumentor;
import org.aim.mainagent.instrumentor.MethodInstrumentor;
import org.aim.mainagent.instrumentor.TraceInstrumentor;
import org.aim.mainagent.probes.IncrementalProbeExtension;
import org.aim.mainagent.sampling.Sampling;
import org.codehaus.jackson.map.ObjectMapper;
import org.lpe.common.extension.ExtensionRegistry;
import org.lpe.common.extension.IExtension;
import org.overhead.OverheadEstimator;

/**
 * Coordinates the instrumentation process.
 * 
 * @author Alexander Wert
 * 
 */
public enum AdaptiveInstrumentationFacade implements AdaptiveInstrumentationFacadeMXBean {
	INSTANCE;

	private final static AIMLogger LOGGER = AIMLoggerFactory.getLogger(AdaptiveInstrumentationFacade.class);
	
	/**
	 * Singleton instance.
	 * 
	 * @return singleton instance
	 */
	public static synchronized AdaptiveInstrumentationFacadeMXBean getInstance() {
		return INSTANCE;
	}

	private final Collection<IInstrumentor> knownInstrumentors = new LinkedList<>();
	private SupportedExtensions extensions = null;
	private final MethodInstrumentor methodInstrumentor = new MethodInstrumentor();

	private AdaptiveInstrumentationFacade() {
		this.knownInstrumentors.add(methodInstrumentor);
		this.knownInstrumentors.add(TraceInstrumentor.INSTANCE);
		this.knownInstrumentors.add(EventInstrumentor.getInstance());
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
		if (LOGGER.isLogLevelEnabled(LoggingLevel.DEBUG)) {
			LOGGER.debug("Requesting instrumentation. Instrumen tation description {}", instrumentationDescription.toString());
		}
		
		for (final IInstrumentor instrumentor : knownInstrumentors) {
			instrumentor.instrument(adaptInstrumentationDescription(instrumentationDescription));
		}

		// TODO: add Statement Instrumentation
		if (!instrumentationDescription.getSamplingDescriptions().isEmpty()) {
			try {
				Sampling.getInstance().addMonitoringJob(instrumentationDescription.getSamplingDescriptions());
			} catch (final MeasurementException e) {
				LOGGER.error("Failed to configure sampling. Exception {}", e.toString());
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
		LOGGER.debug("Requesting instrumentation revert");
		try {
			for (final IInstrumentor instrumentor : knownInstrumentors) {
				instrumentor.undoInstrumentation();
			}
		} catch (final InstrumentationException e) {
			LOGGER.error("Undo instrumentation failed. Exception was {}", e.toString());
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

		final Map<String, Set<Class<? extends Scope>>> probeToSupportedScopesMapping = new HashMap<>();
		final LinkedList<String> customScopes = new LinkedList<>();
		final LinkedList<String> apiScopes = new LinkedList<>();
		final LinkedList<String> sampler = new LinkedList<>();
		final Set<Class<?>> knownConcreteScopeClasses = new HashSet<>();
		
		for (final IExtension extension : ExtensionRegistry.getSingleton().getExtensions()) {
			if (extension instanceof AbstractEnclosingProbeExtension) {
				if (extension instanceof IncrementalProbeExtension) {
					continue;
				}
				probeToSupportedScopesMapping.put(extension.getName(),
						((AbstractEnclosingProbeExtension) extension).getScopeDependencies());
			} else if (extension instanceof AbstractEventProbeExtension) {
				probeToSupportedScopesMapping.put(extension.getName(),
						((AbstractEventProbeExtension) extension).getScopeDependencies());
			} else if (extension instanceof AbstractScopeExtension) {
				customScopes.add(extension.getName());
				knownConcreteScopeClasses.add(extension.getExtensionArtifactClass());
			} else if (extension instanceof AbstractInstApiScopeExtension) {
				apiScopes.add(extension.getName());
				knownConcreteScopeClasses.add(extension.getExtensionArtifactClass());
			} else if (extension instanceof AbstractSamplerExtension) {
				sampler.add(extension.getName());
			}
		}

		final Map<String, Set<String>> probeToRegisteredScopesMapping = filterProbeScopeMappingToExistingScopes(knownConcreteScopeClasses,
				probeToSupportedScopesMapping);

		return extensions = new SupportedExtensions(sampler, apiScopes, probeToRegisteredScopesMapping, customScopes);
	}

	private Map<String, Set<String>> filterProbeScopeMappingToExistingScopes(
			final Set<Class<?>> knownConcreteScopeClasses, final Map<String, Set<Class<? extends Scope>>> probeToSupportedScopesMapping) {
		final Map<String,Set<String>> probeMap = new HashMap<String, Set<String>>();
		for (final String probeName : probeToSupportedScopesMapping.keySet()) {
			final Set<String> scopes = new HashSet<>();
			for (final Class<?> concreteScope : knownConcreteScopeClasses) {
				supportedScopeLoop: for (final Class<?> supportedScope : probeToSupportedScopesMapping.get(probeName)) {
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
	public String getMeasurementData() throws MeasurementException {
		// final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final MeasurementData data = AbstractDataSource.getDefaultDataSource().read();
		final ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(data); 
		} catch (final Exception e) {
			LOGGER.error("Failed to generate Json sfinal sation from measurements",e);
			throw new RuntimeException("Failed to generate Json from measurements");
		}
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

	/** Create a new, adapted instrumentation description that excludes JBCL and AIM classes
	 * @param instrumentationDescription Original description
	 * @return Adapted description
	 */
	private InstrumentationDescription adaptInstrumentationDescription(
			final InstrumentationDescription instrumentationDescription) {
		final InstrumentationDescriptionBuilder adaptedDescriptionBuilder = 
				InstrumentationDescriptionBuilder.fromInstrumentationDescription(instrumentationDescription);
		final RestrictionBuilder<InstrumentationDescriptionBuilder> restrictionBuilder = adaptedDescriptionBuilder.editGlobalRestriction();
		for (final String systemPackage : InstrumentationConstants.ALL_SYSTEM_PACKAGES) {
			restrictionBuilder.excludePackage(systemPackage);
		}
		final InstrumentationDescription adaptedDescription = restrictionBuilder.restrictionDone().build();
		return adaptedDescription;
	}

}
