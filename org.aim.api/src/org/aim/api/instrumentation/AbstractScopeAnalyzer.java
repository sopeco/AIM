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
package org.aim.api.instrumentation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.lpe.common.util.LpeStringUtils;

/**
 * Abstract Class for scope analyzer.
 * 
 * @author Alexander Wert
 * 
 */
public abstract class AbstractScopeAnalyzer implements IScopeAnalyzer {
	private Long scopeId;

	@Override
	public Long getScopeId() {
		return scopeId;
	}

	@Override
	public void setScopeId(final Long scopeId) {
		this.scopeId = scopeId;

	}

	/**
	 * Returns the constructor signature for the given java reflection
	 * constructor instance.
	 * 
	 * @param constructor
	 *            constructor for which the signature shell be constructed
	 * @param includeFullClassName
	 *            specifies whether to include the full qualified name of the
	 *            declaring class
	 * @return string representation of the signature
	 */
	protected String getMethodSignature(final Constructor<?> constructor,
			final boolean includeFullClassName) {
		final String methodName = constructor.getName();
		String className = "";
		if (includeFullClassName) {
			className = constructor.getDeclaringClass().getName() + ".";
		}
		String parameterList = "";
		boolean first = true;
		for (final Class<?> pType : constructor.getParameterTypes()) {
			if (!first) {
				parameterList += ",";
			} else {
				first = false;
			}
			parameterList += pType.getName();
		}
		return className + methodName + "(" + parameterList + ")";
	}
	
	/**
	 * Returns the method signature for the given java reflection method
	 * instance.
	 * 
	 * @param method
	 *            method for which the signature shell be constructed
	 * @param includeFullClassName
	 *            specifies whether to include the full qualified name of the
	 *            declaring class
	 * @return string representation of the signature
	 */
	protected String getMethodSignature(final Method method,
			final boolean includeFullClassName) {
		final String methodName = method.getName();
		String className = "";
		if (includeFullClassName) {
			className = method.getDeclaringClass().getName() + ".";
		}
		String parameterList = "";
		boolean first = true;
		for (Class<?> pType : method.getParameterTypes()) {
			if (!first) {
				parameterList += ",";
			} else {
				first = false;
			}

			String arrayBraces = "";
			while (pType.isArray()) {
				pType = pType.getComponentType();
				arrayBraces += "[]";
			}
			parameterList += pType.getName() + arrayBraces;
		}
		return className + methodName + "(" + parameterList + ")";
	}

	protected Class<?>[] getParameterTypes(final String methodSignature) throws ClassNotFoundException {
		return getParameterTypes(methodSignature, null);
	}
	
	private static final Map<String, Class<?>> primitiveTypes;

	static {
		primitiveTypes = new HashMap<>();
		primitiveTypes.put("B", byte.class);
		primitiveTypes.put("C", char.class);
		primitiveTypes.put("D", double.class);
		primitiveTypes.put("F", float.class);
		primitiveTypes.put("I", int.class);
		primitiveTypes.put("J", long.class);
		primitiveTypes.put("S", short.class);
		primitiveTypes.put("Z", boolean.class);
		primitiveTypes.put("V", void.class);
	}

	/**
	 * Extracts the parameter types of the given method signature.
	 * 
	 * @param methodSignature
	 *            method signature to analyze.
	 * @return array of classes representing the parameter types.
	 * @throws ClassNotFoundException
	 *             if a parameter type class cannot be found
	 */
	protected Class<?>[] getParameterTypes(String methodSignature, final ClassLoader classLoader)
			throws ClassNotFoundException {
		methodSignature = methodSignature.replaceAll("\\s", "");
		final String parameterList = methodSignature.substring(
				methodSignature.indexOf('(') + 1,
				methodSignature.lastIndexOf(')'));

		if (parameterList.isEmpty()) {

			return new Class<?>[0];

		} else {

			final String[] parameters = parameterList.split(",");
			final Class<?>[] parameterTypes = new Class<?>[parameters.length];

			for (int i = 0; i < parameters.length; i++) {
				final String parameterType = parameters[i];
				final String bytecodeNotationParameterType = LpeStringUtils
						.convertTypeToNativeFormat(parameterType);
				final String bcNotationWithDots = bytecodeNotationParameterType.replace("/", ".");

				if (primitiveTypes.containsKey(bcNotationWithDots)) {
					// primitive type
					parameterTypes[i] = primitiveTypes
							.get(bcNotationWithDots);
				} else if (bcNotationWithDots.startsWith("[")) {
					// array type
					parameterTypes[i] = loadClass(bcNotationWithDots,classLoader);
				} else {
					parameterTypes[i] = loadClass(parameterType,classLoader);
				}

			}

			return parameterTypes;
		}
	}

	private static Class<?> loadClass(final String bcNotationWithDots, final ClassLoader classLoader) throws ClassNotFoundException {
		if (classLoader == null) {
			return Class
				.forName(bcNotationWithDots);
		} else {
			return Class
					.forName(bcNotationWithDots, false, classLoader);
		}
	}
	
	/**
	 * 
	 * @param clazz
	 *            class to check
	 * @return returns true, if given class is a normal class, meaning it is not
	 *         an interface, not an array, not an enum, etc.
	 */
	protected boolean isNormalClass(final Class<?> clazz) {
		try {
			return !clazz.isLocalClass() && !clazz.isArray()
					&& !clazz.isInterface() && !clazz.isPrimitive()
					&& !clazz.isMemberClass() && !clazz.isEnum()
					&& !clazz.isAnnotation() && !clazz.isAnonymousClass()
					&& !clazz.isSynthetic() && clazz.getCanonicalName() != null;
		} catch (final Throwable e) {

			return false;
		}

	}

	/**
	 * 
	 * @param clazz
	 *            class to check
	 * @return true if given class has been loaded by the bootstrap class loader
	 */
	private boolean isBootstrapClass(final Class<?> clazz) {
		try {
			return clazz.getClassLoader() == null;
		} catch (final Throwable e) {
			return false;
		}
	}

	/**
	 * 
	 * @param clazz
	 *            class to check
	 * @return returns true, if given class is an interface
	 */
	private boolean isInterface(final Class<?> clazz) {
		try {
			return clazz.isInterface() && !clazz.isAnnotation()
					&& clazz.getCanonicalName() != null;
		} catch (final Throwable e) {
			return false;
		}
	}

}
