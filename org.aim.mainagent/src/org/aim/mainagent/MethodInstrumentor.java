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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aim.aiminterface.description.instrumentation.InstrumentationDescription;
import org.aim.aiminterface.description.restriction.Restriction;
import org.aim.aiminterface.exceptions.InstrumentationException;
import org.aim.api.instrumentation.MethodsEnclosingScope;
import org.aim.api.instrumentation.description.internal.FlatInstrumentationEntity;
import org.aim.api.instrumentation.description.internal.InstrumentationSet;
import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.aim.mainagent.instrumentor.BCInjector;
import org.aim.mainagent.instrumentor.JAgentSwapper;
import org.aim.mainagent.instrumentor.JInstrumentation;
import org.aim.mainagent.scope.ScopeAnalysisController;

/**
 * Keeps track of and conducts method instrumentation.
 * 
 * @author Alexander Wert
 * 
 */
public class MethodInstrumentor implements IInstrumentor {
	private static final AIMLogger LOGGER = AIMLoggerFactory.getLogger(MethodInstrumentor.class);
	private final Set<FlatInstrumentationEntity> currentInstrumentationState = new HashSet<>();

	@Override
	public void instrument(final InstrumentationDescription descr) throws InstrumentationException {
		if (!containsValidInstrumentationInstructions(descr)) {
			return;
		}
		final ScopeAnalysisController scopeAnalyzer = new ScopeAnalysisController(descr); // TODO:
																					// usage
																					// with
																					// IDM
		final Class<?>[] classes = JInstrumentation.getInstance().getjInstrumentation().getAllLoadedClasses();

		// copy all classes to a new list as the array is not allowed to be
		// modified!
		final List<Class<?>> allLoadedClasses = new ArrayList<>();
		allLoadedClasses.addAll(Arrays.asList(classes));

		final Set<FlatInstrumentationEntity> newInstrumentationStatements = scopeAnalyzer.resolveScopes(allLoadedClasses);
		final Set<Class<?>> overLappingClasses = revertOverlappingInstrumentation(newInstrumentationStatements);

		for (final FlatInstrumentationEntity oldEntity : getCurrentInstrumentationState()) {
			if (overLappingClasses.contains(oldEntity.getClazz())) {
				newInstrumentationStatements.add(oldEntity);
			}
		}

		final InstrumentationSet newInstrumentationSet = new InstrumentationSet(newInstrumentationStatements);

		LOGGER.info("Going to instrument the following methods:");
		for (final FlatInstrumentationEntity fie : newInstrumentationStatements) {
			LOGGER.info("{}", fie.getMethodSignature());
		}

		injectNewInstrumentation(newInstrumentationSet, descr.getGlobalRestriction());

		getCurrentInstrumentationState().addAll(newInstrumentationStatements);

	}

	private boolean containsValidInstrumentationInstructions(final InstrumentationDescription descr) {
		return descr.containsScopeType(MethodsEnclosingScope.class);

	}

	private void injectNewInstrumentation(final InstrumentationSet newInstrumentationSet,
			final Restriction instrumentationRestriction) throws InstrumentationException {
		final Map<Class<?>, byte[]> classesToRevert = BCInjector.getInstance().injectInstrumentationProbes(
				newInstrumentationSet, instrumentationRestriction);
		JAgentSwapper.getInstance().redefineClasses(classesToRevert);
	}

	private Set<Class<?>> revertOverlappingInstrumentation(final Set<FlatInstrumentationEntity> newInstrumentationStatements)
			throws InstrumentationException {
		final Set<Class<?>> intersection = new InstrumentationSet(newInstrumentationStatements).classesToInstrument();

		intersection.retainAll(new InstrumentationSet(getCurrentInstrumentationState()).classesToInstrument());
		final Map<Class<?>, byte[]> classesToRevert = BCInjector.getInstance().partlyRevertInstrumentation(intersection);
		JAgentSwapper.getInstance().redefineClasses(classesToRevert);
		return intersection;
	}

	@Override
	public void undoInstrumentation() throws InstrumentationException {
		final Map<Class<?>, byte[]> classesToRevert = BCInjector.getInstance().revertInstrumentation();
		JAgentSwapper.getInstance().redefineClasses(classesToRevert);
		getCurrentInstrumentationState().clear();
	}

	/**
	 * @return the currentInstrumentationState
	 */
	public Set<FlatInstrumentationEntity> getCurrentInstrumentationState() {
		return currentInstrumentationState;
	}

}
