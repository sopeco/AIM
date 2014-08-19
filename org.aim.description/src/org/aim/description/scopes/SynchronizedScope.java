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
 * This scope refers to all points in execution related to {@code synchronized}
 * events.
 * 
 * @author Henning Schulz
 * 
 */
public class SynchronizedScope implements Scope {
	private final long id;

	@Override
	public long getId() {
		return id;
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            scope id
	 */
	@JsonCreator
	public SynchronizedScope(@JsonProperty("id") long id) {
		this.id = id;
	}

	/**
	 * Constructor.
	 */
	public SynchronizedScope() {
		this(System.nanoTime());
	}

	@Override
	public String toString() {
		return "Synchronized Scope";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}

		SynchronizedScope other = (SynchronizedScope) obj;
		return this.getId() == other.getId();
	}
	
	@Override
	public int hashCode() {
		return (int) id;
	}

}
