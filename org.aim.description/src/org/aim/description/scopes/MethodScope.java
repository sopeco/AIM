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
 * This scope contains a given set of methods. These methods are to be specified
 * by method patterns.
 * 
 * @author Henning Schulz
 * 
 */
public class MethodScope extends MethodsEnclosingScope {

	private final String[] methods;

	/**
	 * Constructor.
	 * 
	 * @param methods
	 *            methods which are to be instrumented
	 * @param id
	 *            scope id
	 */
	@JsonCreator
	public MethodScope(@JsonProperty("methods") String[] methods, @JsonProperty("id") long id) {
		super(id);
		this.methods = methods;
	}

	/**
	 * Constructor.
	 * 
	 * @param methods
	 *            methods which are to be instrumented
	 */
	public MethodScope(String[] methods) {
		this(methods, System.nanoTime());
	}

	/**
	 * @return the methods
	 */
	public String[] getMethods() {
		return methods;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		boolean trailingComma = false;

		builder.append("Method Scope [");

		for (String m : methods) {
			builder.append(m);
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
