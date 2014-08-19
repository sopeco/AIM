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
package org.aim.api.instrumentation.description.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aim.api.instrumentation.AbstractEnclosingProbe;

/**
 * Internal aggregated representation of the instrumentation description.
 * Basically maps from classes to methods to instrument and from methods to the
 * measurement probes to inject.
 * 
 * @author Alexander Wert
 * 
 */
public class InstrumentationSet {
	private final Collection<FlatInstrumentationEntity> instrumentationEntities;

	/**
	 * Constructor.
	 * 
	 * @param instrumentationEntities
	 *            flat representation
	 */
	public InstrumentationSet(Collection<FlatInstrumentationEntity> instrumentationEntities) {
		this.instrumentationEntities = instrumentationEntities;
	}

	/**
	 * Returns classes to instrument.
	 * 
	 * @return classes to instrument
	 */
	public Set<Class<?>> classesToInstrument() {
		Set<Class<?>> result = new HashSet<Class<?>>();
		for (FlatInstrumentationEntity fie : instrumentationEntities) {
			result.add(fie.getClazz());
		}
		return result;
	}

	/**
	 * Returns methods of the given class to instrument mapped to the scope ids
	 * for that methods.
	 * 
	 * @param clazz
	 *            class of interest
	 * @return methods of the given class to instrument mapped to the scope ids
	 *         for that methods or an empty map if class is not marked to be
	 *         instrumented
	 */
	public Map<String, Set<Long>> methodsToInstrument(Class<?> clazz) {
		Map<String, Set<Long>> result = new HashMap<>();

		for (FlatInstrumentationEntity fie : instrumentationEntities) {
			if (fie.getClazz().equals(clazz)) {
				if (!result.containsKey(fie.getMethodSignature())) {
					result.put(fie.getMethodSignature(), new HashSet<Long>());
				}
				Long scopeId = fie.getScopeId();
				if (scopeId >= 0) {
					result.get(fie.getMethodSignature()).add(scopeId);
				}

			}
		}

		return result;
	}

	/**
	 * Returns a set of probes to inject into the given method.
	 * 
	 * @param methodSignature
	 *            signature of the method of interest.
	 * @return a set of probes to inject into the given method or an empty set
	 *         if method is not going to be instrumented
	 */
	public Set<Class<? extends AbstractEnclosingProbe>> probesToInject(String methodSignature) {
		Set<Class<? extends AbstractEnclosingProbe>> result = new HashSet<Class<? extends AbstractEnclosingProbe>>();
		for (FlatInstrumentationEntity fie : instrumentationEntities) {
			if (fie.getMethodSignature().equals(methodSignature)) {
				result.add(fie.getProbeType());
			}
		}

		return result;
	}

}
