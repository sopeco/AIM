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

import org.aim.api.exceptions.InstrumentationException;
import org.aim.api.instrumentation.description.internal.FlatInstrumentationEntity;
import org.aim.api.instrumentation.description.internal.InstrumentationSet;
import org.aim.description.InstrumentationDescription;
import org.aim.description.restrictions.Restriction;
import org.aim.description.scopes.MethodsEnclosingScope;
import org.aim.mainagent.instrumentor.BCInjector;
import org.aim.mainagent.instrumentor.JAgentSwapper;
import org.aim.mainagent.instrumentor.JInstrumentation;
import org.aim.mainagent.scope.ScopeAnalysisController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keeps track of and conducts method instrumentation.
 * 
 * @author Alexander Wert
 * 
 */
public class MethodInstrumentor implements IInstrumentor {
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodInstrumentor.class);
	private final Set<FlatInstrumentationEntity> currentInstrumentationState = new HashSet<>();

	@SuppressWarnings("rawtypes")
	@Override
	public void instrument(InstrumentationDescription descr) throws InstrumentationException {
		if (!containsValidInstrumentationInstructions(descr)) {
			return;
		}
		ScopeAnalysisController scopeAnalyzer = new ScopeAnalysisController(descr); // TODO:
																					// usage
																					// with
																					// IDM
		Class[] classes = JInstrumentation.getInstance().getjInstrumentation().getAllLoadedClasses();

		// copy all classes to a new list as the array is not allowed to be
		// modified!
		List<Class> allLoadedClasses = new ArrayList<>();
		allLoadedClasses.addAll(Arrays.asList(classes));

		Set<FlatInstrumentationEntity> newInstrumentationStatements = scopeAnalyzer.resolveScopes(allLoadedClasses);
		Set<Class<?>> overLappingClasses = revertOverlappingInstrumentation(newInstrumentationStatements);

		for (FlatInstrumentationEntity oldEntity : getCurrentInstrumentationState()) {
			if (overLappingClasses.contains(oldEntity.getClazz())) {
				newInstrumentationStatements.add(oldEntity);
			}
		}

		InstrumentationSet newInstrumentationSet = new InstrumentationSet(newInstrumentationStatements);

		LOGGER.info("Going to instrument the following methods:");
		for (FlatInstrumentationEntity fie : newInstrumentationStatements) {
			LOGGER.info("{}", fie.getMethodSignature());
		}

		injectNewInstrumentation(newInstrumentationSet, descr.getGlobalRestriction());

		getCurrentInstrumentationState().addAll(newInstrumentationStatements);

	}

	private boolean containsValidInstrumentationInstructions(InstrumentationDescription descr) {
		return descr.containsScopeType(MethodsEnclosingScope.class);

	}

	private void injectNewInstrumentation(InstrumentationSet newInstrumentationSet,
			Restriction instrumentationRestriction) throws InstrumentationException {
		Map<Class<?>, byte[]> classesToRevert = BCInjector.getInstance().injectInstrumentationProbes(
				newInstrumentationSet, instrumentationRestriction);
		JAgentSwapper.getInstance().redefineClasses(classesToRevert);
	}

	private Set<Class<?>> revertOverlappingInstrumentation(Set<FlatInstrumentationEntity> newInstrumentationStatements)
			throws InstrumentationException {
		Set<Class<?>> intersection = new InstrumentationSet(newInstrumentationStatements).classesToInstrument();

		intersection.retainAll(new InstrumentationSet(getCurrentInstrumentationState()).classesToInstrument());
		Map<Class<?>, byte[]> classesToRevert = BCInjector.getInstance().partlyRevertInstrumentation(intersection);
		JAgentSwapper.getInstance().redefineClasses(classesToRevert);
		return intersection;
	}

	@Override
	public void undoInstrumentation() throws InstrumentationException {
		Map<Class<?>, byte[]> classesToRevert = BCInjector.getInstance().revertInstrumentation();
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
