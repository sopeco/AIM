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
 * This scope refers to all methods of the given API. The APIScope is an
 * extension point and can be extended by using custom API names.
 * 
 * @author Henning Schulz
 * 
 */
public class APIScope extends MethodsEnclosingScope {

	private final String apiName;

	/**
	 * Constructor.
	 * 
	 * @param apiName
	 *            name of the represented API
	 * @param id
	 *            scope id
	 */
	@JsonCreator
	public APIScope(@JsonProperty("apiName") String apiName, @JsonProperty("id") long id) {
		super(id);
		this.apiName = apiName;
	}

	/**
	 * Constructor.
	 * 
	 * @param aPIName
	 *            name of the represented API
	 */
	public APIScope(String aPIName) {
		this(aPIName, System.nanoTime());
	}

	/**
	 * @return the API name
	 */
	public String getApiName() {
		return apiName;
	}

	@Override
	public String toString() {
		return apiName + " Scope";
	}

}
