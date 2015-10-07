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
package org.aim.artifacts.scopes;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.aim.aiminterface.description.restriction.Restriction;
import org.aim.api.instrumentation.AbstractScopeAnalyzer;
import org.aim.api.instrumentation.description.internal.FlatScopeEntity;
import org.lpe.common.util.LpeStringUtils;

/**
 * Analyzes a methods scope.
 * 
 * @author Alexander Wert
 * 
 */
public class MethodScopeAnalyzer extends AbstractScopeAnalyzer {
	private Restriction restriction;
	private final String [] methodPatterns;

	/**
	 * Constructor.
	 * 
	 * @param methodNames
	 *            full qualified signatures of the methods to instrument
	 */
	public MethodScopeAnalyzer(final String [] methodNames) {
		this.methodPatterns = methodNames;
	}

	@Override
	public void visitClass(final Class<?> clazz, final Set<FlatScopeEntity> scopeEntities) {
		final String className = clazz.getName();
		if (restriction.isExcluded(className)) {
			return;
		}

		for (final String patternToInstrument : methodPatterns) {
			if (!LpeStringUtils.patternPrefixMatches(className, patternToInstrument)) {
				continue;
			}
			final Set<Method> methods = new HashSet<>();
			for (final Method m : clazz.getMethods()) {
				if (!Modifier.isAbstract(m.getModifiers()) && !Modifier.isNative(m.getModifiers())) {
					methods.add(m);
				}
			}

			for (final Method m : clazz.getDeclaredMethods()) {
				if (!Modifier.isAbstract(m.getModifiers()) && !Modifier.isNative(m.getModifiers())) {
					methods.add(m);
				}
			}

			for (final Method m : methods) {
				checkMethodMatching(scopeEntities, patternToInstrument, m);
			}

		}

	}

	private void checkMethodMatching(final Set<FlatScopeEntity> scopeEntities, final String patternToMatch, final Method m) {
		if (LpeStringUtils.patternMatches(getMethodSignature(m, true), patternToMatch)) {
			if (!restriction.modifierSetExcluded(m.getModifiers())
					&& !restriction.isExcluded(m.getDeclaringClass().getName())) {
				scopeEntities.add(new FlatScopeEntity(m.getDeclaringClass(), getMethodSignature(m, true), this.getScopeId()));
			}
		}
	}

	@Override
	public void setRestriction(final Restriction restriction) {
		this.restriction = restriction;

	}
}
