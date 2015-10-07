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

import java.util.Map;

import org.aim.aiminterface.exceptions.InstrumentationException;

import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;

/**
 * Utility class for dynamic instrumentation.
 * 
 * @author Alexander Wert
 * 
 */
public final class Utils {
	
	/**
	 * Private constructor due to utility class.
	 */
	private Utils() {

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
	public static CtBehavior getCtBehaviour(final CtClass ctClass, String methodName)
			throws InstrumentationException {
		if (ctClass == null || ctClass.getDeclaredMethods() == null) {
			return null;
		}
		String shortMethodName = "";
		try {
			methodName = methodName.trim();
			final int firstBraceIndex = methodName.indexOf('(');
			final String parameterPart = methodName.substring(firstBraceIndex);
			final String methodFirstPart = methodName.substring(0, firstBraceIndex);
			final int lastDotIndex = methodFirstPart.lastIndexOf('.');

			if (lastDotIndex >= 0) {
				shortMethodName += methodFirstPart.substring(lastDotIndex + 1)
						+ parameterPart;
			} else {
				shortMethodName += methodFirstPart + parameterPart;
			}
		} catch (final Exception e) {
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
	public static void insertMethodLocalVariables(final CtBehavior ctBehaviour,
			final Map<String, Class<?>> variables) throws InstrumentationException {
		try {
			for (final String vName : variables.keySet()) {

				final Class<?> type = variables.get(vName);
				final ClassPool pool = ctBehaviour.getDeclaringClass().getClassPool();
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
		} catch (final Exception e) {
			throw new InstrumentationException(
					"failed inserting local variables!", e);
		}

	}



}
