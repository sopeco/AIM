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
 * This scope refers to all points in execution, where an object is allocated.
 * These points can be restricted to a set of classes.
 * 
 * @author Henning Schulz
 * 
 */
public class AllocationScope implements Scope {

	private final String[] targetClasses;

	private final long id;

	@Override
	public long getId() {
		return id;
	}

	/**
	 * Constructor.
	 * 
	 * @param targetClasses
	 *            classes to be considered
	 * @param id
	 *            scope id
	 */
	@JsonCreator
	public AllocationScope(@JsonProperty("targetClasses") String[] targetClasses, @JsonProperty("id") long id) {
		this.id = id;
		this.targetClasses = targetClasses;
	}

	/**
	 * Constructor.
	 * 
	 * @param targetClasses
	 *            classes to be considered
	 */
	public AllocationScope(String[] targetClasses) {
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

		builder.append("Allocation Scope [");

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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}

		AllocationScope other = (AllocationScope) obj;
		return this.getId() == other.getId();
	}

	@Override
	public int hashCode() {
		return (int) id;
	}

}
