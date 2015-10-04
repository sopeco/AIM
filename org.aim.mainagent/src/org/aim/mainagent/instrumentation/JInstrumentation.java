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
package org.aim.mainagent.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.aim.aiminterface.exceptions.InstrumentationException;
import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.aim.logging.LoggingLevel;

/**
 * Singleton wrapper around Java instrumentation instance.
 * 
 * @author Alexander Wert
 * 
 */
public enum JInstrumentation {
	INSTANCE;
	
	private final AIMLogger LOGGER = AIMLoggerFactory.getLogger(JInstrumentation.class);
	
	public static final String J_INSTRUMENTATION_KEY = "jinstrumentation";

	/**
	 * Returns singleton instance.
	 * 
	 * @return singleton
	 */
	public static synchronized JInstrumentation getInstance() {
		return INSTANCE;
	}

	private Instrumentation jInstrumentation;
	private final Map<String,Set<Class<?>>> loadedClassInfo = new HashMap<>();

	private JInstrumentation() {}

	/**
	 * @return the jInstrumentation
	 * @throws InstrumentationException
	 *             if jInstrumentation has not been set yet
	 */
	Instrumentation getjInstrumentation() throws InstrumentationException {
		if (jInstrumentation == null) {
			throw new InstrumentationException("Java instrumentation instance has not been set, yet!");
		}
		return jInstrumentation;
	}

	/**
	 * @param jInstrumentation
	 *            the jInstrumentation to set
	 */
	public void setjInstrumentation(final Instrumentation jInstrumentation) {
		this.jInstrumentation = jInstrumentation;
		System.getProperties().put(J_INSTRUMENTATION_KEY, jInstrumentation);
		jInstrumentation.addTransformer(new ClassFileTransformer() {
			
			@Override
			public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
					final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {
				if (LOGGER.isLogLevelEnabled(LoggingLevel.DEBUG)) {
					LOGGER.debug(formatCL(loader) + " loaded/modified "+className.replace("/","."));
				}
				if (!JInstrumentation.this.loadedClassInfo.containsKey(className)) {
					JInstrumentation.this.loadedClassInfo.put(className, new LinkedHashSet<Class<?>>());
				}
				JInstrumentation.this.loadedClassInfo.get(className).add(classBeingRedefined);
				return null;
			}

			private String formatCL(ClassLoader loader) {
				final StringBuilder sb = new StringBuilder();
				while (loader != null) {
					sb.insert(0, loader.getClass().getSimpleName());
					sb.insert(0, ":");
					loader = loader.getParent();
				}
				sb.deleteCharAt(sb.length()-1);
				sb.insert(0, "SystemLoader:");
				return sb.toString();
			}
		}, true);
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
	public Set<Class<?>> getClassesByName(final String className) throws InstrumentationException {
		if (jInstrumentation == null) {
			throw new InstrumentationException("Java instrumentation instance has not been set, yet!");
		}

		if (this.loadedClassInfo.containsKey(className)) {
			return this.loadedClassInfo.get(className);
		} else {
			try {
				// As a last resort, try loading the class using our classloader
				this.getClass().getClassLoader().loadClass(className);
			} catch (final ClassNotFoundException e) { }
			return Collections.<Class<?>> emptySet();
		}
	}
	
	public Collection<Class<?>> getKnownClasses() {
		return Collections.unmodifiableCollection(Arrays.asList((Class<?>[])this.jInstrumentation.getAllLoadedClasses()));
	}
}
