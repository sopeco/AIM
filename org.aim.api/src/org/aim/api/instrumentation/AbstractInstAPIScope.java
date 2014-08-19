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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lpe.common.extension.AbstractExtensionArtifact;
import org.lpe.common.extension.IExtension;

/**
 * An instrumentation scope cheks whether a certain entity matches the specific
 * scope.
 * 
 * @author Alexander Wert
 * 
 */
public abstract class AbstractInstAPIScope extends AbstractExtensionArtifact {

	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider.
	 */
	public AbstractInstAPIScope(IExtension<?> provider) {
		super(provider);
		init();
	}

	private Map<String, List<String>> methodsToMatch;
	private Set<String> methodAnnotationsToMatch;

	/**
	 * Initialize API scope.
	 */
	protected abstract void init();

	/**
	 * Adds a method to the matching list.
	 * 
	 * @param containerName
	 *            class or interface name defining the method
	 * @param methodName
	 *            method name
	 */
	protected void addMethod(String containerName, String methodName) {
		if (!getMethodsToMatch().containsKey(containerName)) {
			getMethodsToMatch().put(containerName, new ArrayList<String>());
		}
		getMethodsToMatch().get(containerName).add(methodName);
	}

	/**
	 * @return the interfacesAndMethodsToMatch
	 */
	public Map<String, List<String>> getMethodsToMatch() {
		if (methodsToMatch == null) {
			methodsToMatch = new HashMap<String, List<String>>();
		}
		return methodsToMatch;
	}

	/**
	 * Adds an annotation for a method to match.
	 * 
	 * @param annotationName
	 *            full qualified name of the annotation to match
	 */
	protected void addMethodAnnotationToMatch(String annotationName) {
		getMethodAnnotationsToMatch().add(annotationName);
	}

	/**
	 * @return the methodAnnotationsToMatch
	 */
	public Set<String> getMethodAnnotationsToMatch() {
		if (methodAnnotationsToMatch == null) {
			methodAnnotationsToMatch = new HashSet<>();
		}
		return methodAnnotationsToMatch;
	}

}
