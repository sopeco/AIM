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
package org.aim.mainagent.instrumentor;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

import org.aim.api.exceptions.InstrumentationException;

/**
 * Singleton wrapper around Java instrumentation instance.
 * 
 * @author Alexander Wert
 * 
 */
public final class JInstrumentation {
	public static final String J_INSTRUMENTATION_KEY = "jinstrumentation";
	private static JInstrumentation instance;

	/**
	 * Returns singleton instance.
	 * 
	 * @return singleton
	 */
	public static synchronized JInstrumentation getInstance() {
		if (instance == null) {
			instance = new JInstrumentation();
		}
		return instance;
	}

	private Instrumentation jInstrumentation;

	private JInstrumentation() {

	}

	/**
	 * @return the jInstrumentation
	 * @throws InstrumentationException
	 *             if jInstrumentation has not been set yet
	 */
	public Instrumentation getjInstrumentation() throws InstrumentationException {
		if (jInstrumentation == null) {
			throw new InstrumentationException("Java instrumentation instance has not been set, yet!");
		}
		return jInstrumentation;
	}

	/**
	 * @param jInstrumentation
	 *            the jInstrumentation to set
	 */
	public void setjInstrumentation(Instrumentation jInstrumentation) {
		this.jInstrumentation = jInstrumentation;
		System.getProperties().put(J_INSTRUMENTATION_KEY, jInstrumentation);
	}

	/**
	 * Returns classes by their name.
	 * 
	 * @param className
	 *            name of the classes to search for
	 * @return list of classes matching the name
	 * @throws InstrumentationException
	 *             if the instrumentation agent is null
	 */
	public List<Class<?>> getClassesByName(String className) throws InstrumentationException {
		if (jInstrumentation == null) {
			throw new InstrumentationException("Java instrumentation instance has not been set, yet!");
		}

		// TODO: maybe a cache is useful / reasonable to reduce instrumentation
		// time???
		List<Class<?>> classes = new ArrayList<>();
		for (Class<?> clazz : jInstrumentation.getAllLoadedClasses()) {
			if (clazz.getName().equals(className)) {
				classes.add(clazz);
			}
		}
		return classes;

	}
}
