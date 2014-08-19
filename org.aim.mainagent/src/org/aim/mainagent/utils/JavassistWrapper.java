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

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps a javassist pool object for singleton access.
 * 
 * @author Alexander Wert
 * 
 */
public final class JavassistWrapper {
	private static final Logger LOGGER = LoggerFactory.getLogger(JavassistWrapper.class);
	private static JavassistWrapper instance;

	/**
	 * Returns a singleton instance.
	 * 
	 * @return singleton
	 */
	public static JavassistWrapper getInstance() {
		if (instance == null) {
			instance = new JavassistWrapper();
		}
		return instance;
	}

	private final ClassPool pool;

	private JavassistWrapper() {
		pool = new ClassPool(true);
	}

	/**
	 * @param targetClass
	 *            class of interest
	 * @return a CtClass for the given java class
	 * @throws ClassNotFoundException
	 *             if class cannot be found
	 * @throws NotFoundException
	 *             if class cannot be found
	 */
	public CtClass getCtClass(Class<?> targetClass) throws NotFoundException, ClassNotFoundException {

		if (pool.find(targetClass.getName()) == null) {
			LOGGER.info("CtClass {} not found in the current pool. Extending pool...", targetClass.getName());
			ClassPath cp = new LoaderClassPath(targetClass.getClassLoader());
			pool.appendClassPath(cp);
		}

		CtClass ctClass = pool.getOrNull(targetClass.getCanonicalName());
		if (ctClass != null) {
			LOGGER.debug("CtClass found: {}", targetClass.getName());
		}
		return ctClass;
	}
}
