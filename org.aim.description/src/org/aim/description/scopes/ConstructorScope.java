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
package org.aim.description.scopes;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This scope refers to all constructor calls of the given classes.
 * 
 * @author Henning Schulz
 * 
 */
public class ConstructorScope extends MethodsEnclosingScope {

	private final String[] targetClasses;

	/**
	 * Constructor.
	 * 
	 * @param targetClasses
	 *            classes which constructors are to be instrumented
	 * @param id
	 *            scope id
	 */
	@JsonCreator
	public ConstructorScope(@JsonProperty("targetClasses") String[] targetClasses, @JsonProperty("id") long id) {
		super(id);
		this.targetClasses = targetClasses;
	}

	/**
	 * Constructor.
	 * 
	 * @param targetClasses
	 *            classes which constructors are to be instrumented
	 */
	public ConstructorScope(String[] targetClasses) {
		this(targetClasses, System.nanoTime());
	}

	/**
	 * @return the target classes
	 */
	public String[] getTargetClasses() {
		return targetClasses;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		boolean trailingComma = false;

		builder.append("Constructor Scope [");

		for (String clazz : targetClasses) {
			builder.append(clazz);
			builder.append(", ");
			trailingComma = true;
		}

		if (trailingComma) {
			builder.deleteCharAt(builder.length() - 1);
			builder.deleteCharAt(builder.length() - 1);
		}

		builder.append("]");

		return builder.toString();
	}

}
