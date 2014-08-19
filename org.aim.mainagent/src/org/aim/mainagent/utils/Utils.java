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
package org.aim.mainagent.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;

import org.aim.api.exceptions.InstrumentationException;
import org.lpe.common.util.LpeStringUtils;

/**
 * Utility class for dynamic instrumentation.
 * 
 * @author Alexander Wert
 * 
 */
public final class Utils {

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
	 * Private constructor due to utility class.
	 */
	private Utils() {

	}

	/**
	 * 
	 * @param clazz
	 *            class to check
	 * @return returns true, if given class is a normal class, meaning it is not
	 *         an interface, not an array, not an enum, etc.
	 */
	public static boolean isNormalClass(Class<?> clazz) {
		try {
			return !clazz.isLocalClass() && !clazz.isArray()
					&& !clazz.isInterface() && !clazz.isPrimitive()
					&& !clazz.isMemberClass() && !clazz.isEnum()
					&& !clazz.isAnnotation() && !clazz.isAnonymousClass()
					&& !clazz.isSynthetic() && clazz.getCanonicalName() != null;
		} catch (Throwable e) {

			return false;
		}

	}

	/**
	 * 
	 * @param clazz
	 *            class to check
	 * @return true if given class has been loaded by the bootstrap class loader
	 */
	public static boolean isBootstrapClass(Class<?> clazz) {
		try {
			return clazz.getClassLoader() == null;
		} catch (Throwable e) {

			return false;
		}

	}

	/**
	 * 
	 * @param clazz
	 *            class to check
	 * @return returns true, if given class is an interface
	 */
	public static boolean isInterface(Class<?> clazz) {
		try {
			return clazz.isInterface() && !clazz.isAnnotation()
					&& clazz.getCanonicalName() != null;
		} catch (Throwable e) {
			return false;
		}

	}

	/**
	 * 
	 * @param ctClass
	 *            class where to search the method
	 * @param methodName
	 *            name of the method of interest
	 * @return the CtMethod of the passed CtClass for the given methodName or
	 *         null if the given method could not have been found in the given
	 *         class
	 * @throws InstrumentationException
	 *             if ctMethod cannot be retrieved
	 */
	public static CtBehavior getCtBehaviour(CtClass ctClass, String methodName)
			throws InstrumentationException {
		if (ctClass == null || ctClass.getDeclaredMethods() == null) {
			return null;
		}
		String shortMethodName = "";
		try {
			methodName = methodName.trim();
			int firstBraceIndex = methodName.indexOf('(');
			String parameterPart = methodName.substring(firstBraceIndex);
			String methodFirstPart = methodName.substring(0, firstBraceIndex);
			int lastDotIndex = methodFirstPart.lastIndexOf('.');

			if (lastDotIndex >= 0) {
				shortMethodName += methodFirstPart.substring(lastDotIndex + 1)
						+ parameterPart;
			} else {
				shortMethodName += methodFirstPart + parameterPart;
			}
		} catch (Exception e) {
			throw new InstrumentationException(
					"Invalid format of the passed method name!", e);
		}

		for (final CtBehavior method : ctClass.getDeclaredBehaviors()) {
			if (method.getLongName().endsWith(shortMethodName)) {

				return method;
			}
		}
		return null;
	}

	/**
	 * Inserts local variables into the given method.
	 * 
	 * @param ctBehaviour
	 *            the CtMethod where to inject local variable
	 * @param variables
	 *            map of variables to inject.
	 * @throws InstrumentationException
	 *             if variables cannot be inserted
	 */
	public static void insertMethodLocalVariables(CtBehavior ctBehaviour,
			Map<String, Class<?>> variables) throws InstrumentationException {
		try {
			for (String vName : variables.keySet()) {

				Class<?> type = variables.get(vName);
				ClassPool pool = ctBehaviour.getDeclaringClass().getClassPool();
				if (type.getCanonicalName().equalsIgnoreCase(
						"java.lang.Boolean")) {
					ctBehaviour.addLocalVariable(vName, CtClass.booleanType);
				} else if (type.getCanonicalName().equalsIgnoreCase(
						"java.lang.Byte")) {
					ctBehaviour.addLocalVariable(vName, CtClass.byteType);
				} else if (type.getCanonicalName().equalsIgnoreCase(
						"java.lang.Character")) {
					ctBehaviour.addLocalVariable(vName, CtClass.charType);
				} else if (type.getCanonicalName().equalsIgnoreCase(
						"java.lang.Double")) {
					ctBehaviour.addLocalVariable(vName, CtClass.doubleType);
				} else if (type.getCanonicalName().equalsIgnoreCase(
						"java.lang.Float")) {
					ctBehaviour.addLocalVariable(vName, CtClass.floatType);
				} else if (type.getCanonicalName().equalsIgnoreCase(
						"java.lang.Integer")) {
					ctBehaviour.addLocalVariable(vName, CtClass.intType);
				} else if (type.getCanonicalName().equalsIgnoreCase(
						"java.lang.Long")) {
					ctBehaviour.addLocalVariable(vName, CtClass.longType);
				} else if (type.getCanonicalName().equalsIgnoreCase(
						"java.lang.Short")) {
					ctBehaviour.addLocalVariable(vName, CtClass.shortType);
				} else {
					ctBehaviour.addLocalVariable(vName,
							pool.getCtClass(type.getCanonicalName()));
				}
			}
		} catch (Exception e) {
			throw new InstrumentationException(
					"failed inserting local variables!", e);
		}

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
	public static String getMethodSignature(Method method,
			boolean includeFullClassName) {
		String methodName = method.getName();
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
	public static String getMethodSignature(Constructor<?> constructor,
			boolean includeFullClassName) {
		String methodName = constructor.getName();
		String className = "";
		if (includeFullClassName) {
			className = constructor.getDeclaringClass().getName() + ".";
		}
		String parameterList = "";
		boolean first = true;
		for (Class<?> pType : constructor.getParameterTypes()) {
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
	 * Extracts the parameter types of the given method signature.
	 * 
	 * @param methodSignature
	 *            method signature to analyze.
	 * @return array of classes representing the parameter types.
	 * @throws ClassNotFoundException
	 *             if a parameter type class cannot be found
	 */
	public static Class<?>[] getParameterTypes(String methodSignature)
			throws ClassNotFoundException {
		methodSignature = methodSignature.replaceAll("\\s", "");
		String parameterList = methodSignature.substring(
				methodSignature.indexOf('(') + 1,
				methodSignature.lastIndexOf(')'));

		if (parameterList.isEmpty()) {

			return new Class<?>[0];

		} else {

			String[] parameters = parameterList.split(",");
			Class<?>[] parameterTypes = new Class<?>[parameters.length];

			for (int i = 0; i < parameters.length; i++) {
				String parameterType = parameters[i];
				String bytecodeNotationParameterType = LpeStringUtils
						.convertTypeToNativeFormat(parameterType);
				String bcNotationWithDots = bytecodeNotationParameterType.replace("/", ".");

				if (primitiveTypes.containsKey(bcNotationWithDots)) {
					// primitive type
					parameterTypes[i] = primitiveTypes
							.get(bcNotationWithDots);
				} else if (bcNotationWithDots.startsWith("[")) {
					// array type
					parameterTypes[i] = Class
							.forName(bcNotationWithDots);
				} else {
					parameterTypes[i] = Class.forName(parameterType);
				}

			}

			return parameterTypes;
		}
	}

}
