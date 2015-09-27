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
package org.aim.mainagent.builder;

import java.util.HashSet;
import java.util.Set;

import org.aim.aiminterface.exceptions.InstrumentationException;
import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.lpe.common.util.LpeNumericUtils;
import org.lpe.common.util.LpeSupportedTypes;

/**
 * Builder for creating a combined probe snippet.
 * 
 * @author Alexander Wert
 * 
 */
public class ProbeBuilder {

	private static final String GRANULARITY_INIT_PART = "\n_GenericProbe_threadId=java.lang.Thread.currentThread().getId();";
	private final String GRANULARITY_BEFORE_PART;
	private static final String GRANULARITY_AFTER_PART = "}\n";

	private final boolean useGranularity;

	private String methodSignature;

	Snippet currentSnippet = new Snippet();
	private final Set<Class<? extends AbstractEnclosingProbe>> injectedProbeTypes;

	/**
	 * Constructor.
	 * 
	 * @param methodSignature
	 *            target method to instrument
	 */
	public ProbeBuilder(String methodSignature, double granularity) {
		this.methodSignature = methodSignature;
		injectedProbeTypes = new HashSet<>();

		int[] granNumDenom = LpeNumericUtils.getFractionFromDouble(granularity);
		GRANULARITY_BEFORE_PART = "\nif(_GenericProbe_threadId % " + granNumDenom[1] + " < " + granNumDenom[0] + ") {";

		if (granNumDenom[0] < granNumDenom[1]) {
			useGranularity = true;
		} else {
			useGranularity = false;
		}
	}

	/**
	 * Adds a probe for injection.
	 * 
	 * @param probeType
	 *            class of the probe to inject
	 * @return builder instance
	 * @throws InstrumentationException
	 *             if injection fails
	 */
	public ProbeBuilder inject(Class<? extends AbstractEnclosingProbe> probeType) throws InstrumentationException {
		if (!injectedProbeTypes.contains(probeType)) {

			MultiSnippet mSnippet = SnippetProvider.getInstance().getSnippet(probeType);
			String beforePart = mSnippet.getBeforePart(methodSignature) + currentSnippet.getBeforePart();
			currentSnippet.setBeforePart(beforePart);
			String afterPart = currentSnippet.getAfterPart() + mSnippet.getAfterPart(methodSignature);
			currentSnippet.setAfterPart(afterPart);
			String incPart = currentSnippet.getIncrementalPart() + mSnippet.getIncrementalPart();
			currentSnippet.setIncrementalPart(incPart);
			currentSnippet.getVariables().putAll(mSnippet.getVariables());
			injectedProbeTypes.add(probeType);
		}

		return this;
	}

	/**
	 * Builds a combined probe snippet surrounded by common probe code.
	 * 
	 * @return combined probe snippet.
	 * @throws InstrumentationException
	 *             if snippet cannot be built
	 */
	public Snippet build() throws InstrumentationException {

		MultiSnippet mSnippet = SnippetProvider.getInstance().getGenericSnippet();
		Snippet resultSnippet = new Snippet();
		String beforePart = mSnippet.getBeforePart(methodSignature) + currentSnippet.getBeforePart();
		String afterPart = currentSnippet.getAfterPart() + mSnippet.getAfterPart(methodSignature);

		if (useGranularity) {
			StringBuilder initPartBuilder = new StringBuilder();
			for (String var : mSnippet.getVariables().keySet()) {
				initPartBuilder.append("\n");
				initPartBuilder.append(var);
				initPartBuilder.append("=");
				LpeSupportedTypes lst = LpeSupportedTypes.get(mSnippet.getVariables().get(var));
				if (lst == null || lst == LpeSupportedTypes.String) {
					initPartBuilder.append("null");
				} else {
					initPartBuilder.append(LpeSupportedTypes.getValueOfType("0", lst));
				}
				initPartBuilder.append(";");
			}
			for (String var : currentSnippet.getVariables().keySet()) {
				initPartBuilder.append("\n");
				initPartBuilder.append(var);
				initPartBuilder.append("=");
				LpeSupportedTypes lst = LpeSupportedTypes.get(currentSnippet.getVariables().get(var));
				if (lst == null || lst == LpeSupportedTypes.String) {
					initPartBuilder.append("null");
				} else {
					initPartBuilder.append(LpeSupportedTypes.getValueOfType("0", lst));
				}
				initPartBuilder.append(";");
			}

			beforePart = initPartBuilder.toString() + GRANULARITY_INIT_PART + GRANULARITY_BEFORE_PART + beforePart
					+ GRANULARITY_AFTER_PART;
			afterPart = GRANULARITY_BEFORE_PART + afterPart + GRANULARITY_AFTER_PART;
		}

		beforePart = beforePart.replace(AbstractEnclosingProbe.METHOD_SIGNATURE_PLACE_HOLDER, "\"" + methodSignature
				+ "\"");
		beforePart = beforePart.replace("((java.util.Hashtable<K, Object>) System.getProperties())", "System.getProperties()");
	
		afterPart = afterPart.replace(AbstractEnclosingProbe.METHOD_SIGNATURE_PLACE_HOLDER, "\"" + methodSignature
				+ "\"");
		afterPart = afterPart.replace("((java.util.Hashtable<K, Object>) System.getProperties())", "System.getProperties()");

		resultSnippet.setBeforePart(beforePart);
		resultSnippet.setAfterPart(afterPart);
		resultSnippet.setIncrementalPart(currentSnippet.getIncrementalPart());
		resultSnippet.getVariables().putAll(currentSnippet.getVariables());
		resultSnippet.getVariables().putAll(mSnippet.getVariables());

		return resultSnippet;
	}


}
