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
package org.aim.mainagent.instrumentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aim.aiminterface.description.restriction.Restriction;
import org.aim.aiminterface.exceptions.InstrumentationException;
import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.description.internal.InstrumentationSet;
import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.aim.mainagent.builder.ProbeBuilder;
import org.aim.mainagent.builder.Snippet;
import org.aim.mainagent.probes.IncrementalInstrumentationProbe;
import org.aim.mainagent.utils.JavassistWrapper;
import org.aim.mainagent.utils.Utils;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.CtClass;

/**
 * Conducts instrumentation of methods and constructors.
 * 
 * @author Alexander Wert
 * 
 */
public final class BCInjector {
	private static final AIMLogger LOGGER = AIMLoggerFactory.getLogger(BCInjector.class);

	private static BCInjector instance;

	/**
	 * Returns singleton instance.
	 * 
	 * @return singleton
	 */
	public static synchronized BCInjector getInstance() {
		if (instance == null) {
			instance = new BCInjector();
		}
		return instance;
	}

	private final Map<Class<?>, byte[]> originalByteCodes;

	private BCInjector() {

		originalByteCodes = new HashMap<>();
	}

	/**
	 * Injects bytecode according to the passed instrumentation description.
	 * 
	 * @param instrumentationSet
	 *            aggregated instrumentation description
	 * @return mapping from classes to new instrumented bytecodes
	 * @param instrumentationRestriction
	 *            restriction for the instrumentation
	 */
	public synchronized Map<Class<?>, byte[]> injectInstrumentationProbes(final InstrumentationSet instrumentationSet,
			final Restriction instrumentationRestriction) {
		final Map<Class<?>, byte[]> classesToRedefine = new HashMap<Class<?>, byte[]>();
		for (final Class<?> clazz : instrumentationSet.classesToInstrument()) {
			try {
				final CtClass ctClass = JavassistWrapper.getInstance().getCtClass(clazz);
				if (ctClass == null) {
					LOGGER.warn("No CtClass found for class {}. Skipping this class for instrumentation.",
							clazz.getCanonicalName());
					continue;
				}
				final byte[] originalByteCode = ctClass.toBytecode();

				if (ctClass.isFrozen()) {
					ctClass.defrost();
				}
				for (final Entry<String, Set<Long>> methodEntry : instrumentationSet.methodsToInstrument(clazz).entrySet()) {
					for (final long scopeId : methodEntry.getValue()) {
						instrumentBehaviour(instrumentationSet.probesToInject(methodEntry.getKey(), scopeId), ctClass,
								methodEntry.getKey(), scopeId, instrumentationRestriction);
					}
				}
				ctClass.freeze();

				if (!originalByteCodes.containsKey(clazz)) {
					originalByteCodes.put(clazz, originalByteCode);
				}
				classesToRedefine.put(clazz, ctClass.toBytecode());
			} catch (final Throwable e) {
				LOGGER.warn("Error ocured during instrumentation. Ignoring this error... {}", e);
				// throw new RuntimeException(e);
				// TODO: continue; ????
			}
		}
		return classesToRedefine;
	}

	/**
	 * Reverts instrumentation on the given classes.
	 * 
	 * @param classes
	 *            classes to revert
	 * @return mapping from classes to original bytecodes
	 */
	public synchronized Map<Class<?>, byte[]> partlyRevertInstrumentation(final Collection<Class<?>> classes) {
		final Map<Class<?>, byte[]> classesToRedefine = new HashMap<Class<?>, byte[]>();
		for (final Class<?> clazz : classes) {
			try {
				if (originalByteCodes.containsKey(clazz)) {
					final CtClass ctClass = JavassistWrapper.getInstance().getCtClass(clazz);
					if (ctClass != null) {
						ctClass.detach();
					}
					classesToRedefine.put(clazz, originalByteCodes.get(clazz));
					originalByteCodes.remove(clazz);
				}
			} catch (final Exception e) {
				LOGGER.error("Error: {}", e);
			}

		}
		return classesToRedefine;
	}

	/**
	 * Completely reverts instrumentation.
	 * 
	 * @return mapping from classes to original bytecodes
	 */
	public synchronized Map<Class<?>, byte[]> revertInstrumentation() {
		final List<Class<?>> classes = new ArrayList<>();
		classes.addAll(originalByteCodes.keySet());
		return partlyRevertInstrumentation(classes);
	}

	/**
	 * Instruments the behaviour.
	 * 
	 * @param probeTypes
	 *            probe types to inject
	 * @param ctClass
	 *            class of the behaviour
	 * @param behaviourSignature
	 *            signature of the behaviour
	 * @param scopeIds
	 *            ids of the scopes (required for incremental instrumentation)
	 * @param instrumentationRestriction
	 *            restriction
	 * @throws InstrumentationException
	 *             thrown if instrumentation fails
	 */
	protected void instrumentBehaviour(final Set<Class<? extends AbstractEnclosingProbe>> probeTypes, final CtClass ctClass,
			final String behaviourSignature, final long scopeId, final Restriction instrumentationRestriction)
			throws InstrumentationException {
		try {
			final ProbeBuilder pBuilder = new ProbeBuilder(behaviourSignature, instrumentationRestriction.getGranularity());
			for (final Class<? extends AbstractEnclosingProbe> probeType : probeTypes) {
				pBuilder.inject(probeType);
			}

			final CtBehavior ctBehaviour = Utils.getCtBehaviour(ctClass, behaviourSignature);

			if (ctBehaviour != null) {
				final Snippet snippet = pBuilder.build();

				if (!snippet.getIncrementalPart().isEmpty()) {
					String tempSnippet = snippet.getIncrementalPart().replace(
							IncrementalInstrumentationProbe.CLAZZ, "$0");
					tempSnippet = tempSnippet.replace(IncrementalInstrumentationProbe.INST_DESCRIPTION,
							String.valueOf(scopeId) + "L");

					final FullTraceMethodEditor ftmEditor = new FullTraceMethodEditor(ctBehaviour.getLongName(),
							tempSnippet, instrumentationRestriction);
					ctBehaviour.instrument(ftmEditor);
				}

				Utils.insertMethodLocalVariables(ctBehaviour, snippet.getVariables());
				ctBehaviour.insertBefore(snippet.getBeforePart());

				ctBehaviour.insertAfter(snippet.getAfterPart());

			}
		} catch (final CannotCompileException e) {
			throw new InstrumentationException("Failed instrumenting behaviour: " + behaviourSignature, e);
		}

	}
}
