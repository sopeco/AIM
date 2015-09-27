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

import org.aim.description.extension.CommonlyUsedScopeTypes;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This scope refers to all points in execution, where an object is allocated.
 * These points can be restricted to a set of classes.
 * 
 * @author Henning Schulz
 * 
 */
public class AllocationScope extends ClassScope {

	/**
	 * Constructor.
	 * 
	 * @param targetClasses
	 *            classes to be considered
	 * @param id
	 *            scope id
	 */
	@JsonCreator
	public AllocationScope(@JsonProperty("targetClasses") final String[] targetClasses, @JsonProperty("id") final long id) {
		super(targetClasses, id, CommonlyUsedScopeTypes.CLASS_SCOPE_TYPE);
	}

	/**
	 * Constructor.
	 * 
	 * @param targetClasses
	 *            classes to be considered
	 */
	public AllocationScope(final String[] targetClasses) {
		this(targetClasses, System.nanoTime());
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		boolean trailingComma = false;

		builder.append("Allocation Scope [");

		for (final String clazz : getTargetClasses()) {
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
