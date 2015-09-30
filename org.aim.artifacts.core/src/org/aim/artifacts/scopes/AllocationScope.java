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
package org.aim.artifacts.scopes;

import org.aim.aiminterface.description.scope.ScopeDescription;
import org.lpe.common.extension.IExtension;

/**
 * This scope refers to all points in execution, where an object is allocated.
 * These points can be restricted to a set of classes.
 * 
 * @author Henning Schulz
 * 
 */
public class AllocationScope extends ClassScope {

	public AllocationScope(final IExtension provider, final ScopeDescription fromDescription) {
		super(provider, fromDescription);
	}

	AllocationScope(final IExtension provider, final long id, final String[] targetClasses) {
		super(provider, id, targetClasses);
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
