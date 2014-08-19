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

import java.lang.instrument.ClassDefinition;
import java.util.Map;

import org.aim.api.exceptions.InstrumentationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for swapping bytecode.
 * 
 * @author Alexander Wert
 * 
 */
public final class JAgentSwapper implements BCSwapper {
	private static final Logger LOGGER = LoggerFactory.getLogger(JAgentSwapper.class);

	private static JAgentSwapper instance;

	/**
	 * Returns singleton instance.
	 * 
	 * @return singleton
	 * @throws InstrumentationException
	 *             if Java Instrumentation instance has never been set
	 */
	public static synchronized JAgentSwapper getInstance() throws InstrumentationException {
		if (instance == null) {
			instance = new JAgentSwapper();
		}
		return instance;
	}

	private JAgentSwapper() throws InstrumentationException {
		if (JInstrumentation.getInstance().getjInstrumentation() == null) {
			throw new RuntimeException("Java Instrumentation instance has never been set!");
		}
	}

	/**
	 * Swaps the bytecode of the given classes.
	 * 
	 * @param newByteCodes
	 *            mapping from classes to new bytecodes to swap
	 */
	public void redefineClasses(Map<Class<?>, byte[]> newByteCodes) {

		int i = 0;
		for (Class<?> clazz : newByteCodes.keySet()) {
			try {
				ClassDefinition cd = new ClassDefinition(clazz, newByteCodes.get(clazz));
				LOGGER.debug("Going to redefined class: {}", clazz.getName());
				ClassDefinition[] cdArray = new ClassDefinition[1];
				cdArray[0] = cd;
				JInstrumentation.getInstance().getjInstrumentation().redefineClasses(cdArray);
				i++;
			} catch (Throwable e) {
				LOGGER.error("Error: {}", e);
			}

		}
		if (i > 0) {
			LOGGER.info("Redefined {} classes!", i);
		}

	}
}
