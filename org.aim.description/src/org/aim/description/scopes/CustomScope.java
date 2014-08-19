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
 * This scope is an extension point to define custom scopes. For that, a String
 * denoting the scope has to be specified.
 * 
 * @author Henning Schulz
 * 
 */
public class CustomScope extends MethodsEnclosingScope {

	private final String scopeName;

	/**
	 * Constructor.
	 * 
	 * @param scopeName
	 *            name of the custom scope
	 * @param id
	 *            scope id
	 */
	@JsonCreator
	public CustomScope(@JsonProperty("scopeName") String scopeName, @JsonProperty("id") long id) {
		super(id);
		this.scopeName = scopeName;
	}

	/**
	 * Constructor.
	 * 
	 * @param scopeName
	 *            name of the custom scope
	 */
	public CustomScope(String scopeName) {
		this(scopeName, System.nanoTime());
	}

	/**
	 * @return the scope name
	 */
	public String getScopeName() {
		return scopeName;
	}

	@Override
	public String toString() {
		return scopeName;
	}

}
