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
package org.aim.mainagent.scope;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aim.api.exceptions.InstrumentationException;
import org.aim.api.instrumentation.AbstractInstAPIScope;
import org.aim.api.instrumentation.AbstractScopeAnalyzer;
import org.aim.api.instrumentation.description.internal.FlatScopeEntity;
import org.aim.description.restrictions.Restriction;
import org.aim.mainagent.instrumentor.JInstrumentation;
import org.aim.mainagent.utils.MethodSignature;
import org.aim.mainagent.utils.Utils;

/**
 * Analyzes a certain API scope.
 * 
 * @author Alexander Wert
 * 
 */
public class APIScopeAnalyzer extends AbstractScopeAnalyzer {

	private final Map<Class<?>, List<MethodSignature>> methodsToMatch;
	private final Set<Class<Annotation>> methodAnnotationsToMatch;
	private Restriction restriction;

	/**
	 * Constructor.
	 * 
	 * @param apiScope
	 *            concrete instance of an AbstractInstAPIScope specifying a
	 *            concrete scope.
	 * @param allLoadedClasses
	 *            all classes loaded by the JVM
	 * @throws InstrumentationException
	 *             if an API class or interface could not be found.
	 */
	@SuppressWarnings("rawtypes")
	public APIScopeAnalyzer(final AbstractInstAPIScope apiScope, final List<Class> allLoadedClasses)
			throws InstrumentationException {
		methodsToMatch = new HashMap<>();
		for (final String containerName : apiScope.getMethodsToMatch().keySet()) {
			try {
				final List<Class<?>> classList = JInstrumentation.getInstance().getClassesByName(containerName);
				if (classList.size() != 1) {
					throw new InstrumentationException("Multiple classes found with name "+containerName);
				}
				final Class<?> containerClass = classList.get(0);
				final List<MethodSignature> signatures = new ArrayList<>();
				for (final String apiMethod : apiScope.getMethodsToMatch().get(containerName)) {
					final String methodName = apiMethod.substring(0, apiMethod.indexOf('('));
					final Class<?>[] paramTypes = Utils.getParameterTypes(apiMethod,containerClass.getClassLoader());
					signatures.add(new MethodSignature(methodName, paramTypes));
				}
				methodsToMatch.put(containerClass, signatures);
			} catch (final ClassNotFoundException e) {
				throw new InstrumentationException("Failed determining scope " + apiScope.getClass().getName(), e);
			}
		}

		methodAnnotationsToMatch = new HashSet<>();
		for (final String annotationName : apiScope.getMethodAnnotationsToMatch()) {
			try {
				final Class<Annotation> annotationClass = findAnnotation(allLoadedClasses, annotationName);
				if (annotationClass != null) {
					methodAnnotationsToMatch.add(annotationClass);
				}
			} catch (final ClassNotFoundException e) {
				throw new InstrumentationException("Failed determining scope " + apiScope.getClass().getName(), e);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private Class<Annotation> findAnnotation(final List<Class> allLoadedClasses, final String annotationName)
			throws ClassNotFoundException {
		for (final Class<?> clazz : allLoadedClasses) {
			try {
				if (clazz.getName().equals(annotationName)) {
					@SuppressWarnings("unchecked")
					final
					Class<Annotation> annotationClass = (Class<Annotation>) clazz;
					return annotationClass;
				}
			} catch (final Throwable t) {
				continue;
			}
		}
		return null;
	}

	@Override
	public void visitClass(final Class<?> clazz, Set<FlatScopeEntity> scopeEntities) {
		if (clazz == null || !Utils.isNormalClass(clazz)) {
			return;
		}
		if (restriction.hasModifierRestrictions() && restriction.modifierSetExcluded(Modifier.PUBLIC)) {
			return;
		}
		if (restriction.isExcluded(clazz.getName())) {
			return;
		}
		if (scopeEntities == null) {
			scopeEntities = new HashSet<>();
		}

		final Set<Method> methods = new HashSet<>();
		for (final Method m : clazz.getMethods()) {
			if (!Modifier.isAbstract(m.getModifiers()) && !Modifier.isNative(m.getModifiers())
					&& m.getDeclaringClass().equals(clazz)) {
				methods.add(m);
			}
		}

		for (final Method m : clazz.getDeclaredMethods()) {
			if (!Modifier.isAbstract(m.getModifiers()) && !Modifier.isNative(m.getModifiers())) {
				methods.add(m);
			}
		}

		for (final Method method : methods) {
			for (final Class<?> apiClass : methodsToMatch.keySet()) {
				if (apiClass.isAssignableFrom(clazz)) {
					for (final MethodSignature apiMethodSignature : methodsToMatch.get(apiClass)) {
						if (method.getName().equals(apiMethodSignature.getMethodName())
								&& Arrays.equals(method.getParameterTypes(), apiMethodSignature.getParameters())) {
							scopeEntities.add(new FlatScopeEntity(clazz, Utils.getMethodSignature(method, true)));
						}
					}
				}
			}

			for (final Class<Annotation> annotationClass : methodAnnotationsToMatch) {
				if (method.getAnnotation(annotationClass) != null) {
					scopeEntities.add(new FlatScopeEntity(clazz, Utils.getMethodSignature(method, true)));
					break;
				}

			}
		}
	}

	@Override
	public void setRestriction(final Restriction restriction) {
		this.restriction = restriction;

	}

}
