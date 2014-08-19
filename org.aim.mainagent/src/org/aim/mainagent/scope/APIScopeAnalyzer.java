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
import org.aim.mainagent.utils.MethodSignature;
import org.aim.mainagent.utils.Utils;

/**
 * Analyzes a certain API scope.
 * 
 * @author Alexander Wert
 * 
 */
public class APIScopeAnalyzer extends AbstractScopeAnalyzer {

	private Map<Class<?>, List<MethodSignature>> methodsToMatch;
	private Set<Class<Annotation>> methodAnnotationsToMatch;
	private Restriction restriction;

	/**
	 * Constructor.
	 * 
	 * @param apiScope
	 *            concrete instance of an AbstractInstAPIScope specifying a
	 *            concrete scope.
	 * @throws InstrumentationException
	 *             if an API class or interface could not be found.
	 */
	@SuppressWarnings("rawtypes")
	public APIScopeAnalyzer(AbstractInstAPIScope apiScope, List<Class> allLoadedClasses)
			throws InstrumentationException {
		methodsToMatch = new HashMap<>();
		for (String containerName : apiScope.getMethodsToMatch().keySet()) {
			try {
				Class<?> containerClass = Class.forName(containerName);
				List<MethodSignature> signatures = new ArrayList<>();
				for (String apiMethod : apiScope.getMethodsToMatch().get(containerName)) {
					String methodName = apiMethod.substring(0, apiMethod.indexOf('('));
					Class<?>[] paramTypes = Utils.getParameterTypes(apiMethod);
					signatures.add(new MethodSignature(methodName, paramTypes));
				}
				methodsToMatch.put(containerClass, signatures);
			} catch (ClassNotFoundException e) {
				throw new InstrumentationException("Failed determining scope " + apiScope.getClass().getName(), e);
			}
		}

		methodAnnotationsToMatch = new HashSet<>();
		for (String annotationName : apiScope.getMethodAnnotationsToMatch()) {
			try {
				Class<Annotation> annotationClass = findAnnotation(allLoadedClasses, annotationName);
				methodAnnotationsToMatch.add(annotationClass);
			} catch (ClassNotFoundException e) {
				throw new InstrumentationException("Failed determining scope " + apiScope.getClass().getName(), e);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private Class<Annotation> findAnnotation(List<Class> allLoadedClasses, String annotationName)
			throws ClassNotFoundException {
		for (Class<?> clazz : allLoadedClasses) {
			if (clazz.getName().equals(annotationName)) {
				@SuppressWarnings("unchecked")
				Class<Annotation> annotationClass = (Class<Annotation>) clazz;
				return annotationClass;
			}
		}
		throw new ClassNotFoundException("Class for Annotation with name" + annotationName + " not found!");

	}

	@Override
	public void visitClass(Class<?> clazz, Set<FlatScopeEntity> scopeEntities) {
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
		for (Class<?> apiClass : methodsToMatch.keySet()) {
			if (apiClass.isAssignableFrom(clazz)) {
				for (MethodSignature methodSignature : methodsToMatch.get(apiClass)) {
					try {
						Method targetMethod = clazz.getMethod(methodSignature.getMethodName(),
								methodSignature.getParameters());
						if (!Modifier.isAbstract(targetMethod.getModifiers())
								&& targetMethod.getDeclaringClass().equals(clazz)) {
							scopeEntities.add(new FlatScopeEntity(clazz, Utils.getMethodSignature(targetMethod, true)));
						}
					} catch (Exception e) {
						// method not found
						// --> class does not contain the desired method
						continue;
					}
				}
			}
		}

		Set<Method> methods = new HashSet<>();
		for (Method m : clazz.getMethods()) {
			if (!Modifier.isAbstract(m.getModifiers()) && !Modifier.isNative(m.getModifiers())) {
				methods.add(m);
			}
		}

		for (Method m : clazz.getDeclaredMethods()) {
			if (!Modifier.isAbstract(m.getModifiers()) && !Modifier.isNative(m.getModifiers())) {
				methods.add(m);
			}
		}

		for (Method method : methods) {
			for (Class<Annotation> annotationClass : methodAnnotationsToMatch) {
				if (method.getAnnotation(annotationClass) != null) {
					scopeEntities.add(new FlatScopeEntity(clazz, Utils.getMethodSignature(method, true)));
					break;
				}

			}
		}
	}

	@Override
	public void setRestriction(Restriction restriction) {
		this.restriction = restriction;

	}

}
