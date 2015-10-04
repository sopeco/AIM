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

import java.util.Collection;

import org.aim.aiminterface.description.scope.ScopeDescription;
import org.aim.api.instrumentation.AbstractScope;
import org.aim.api.instrumentation.IScopeAnalyzer;
import org.lpe.common.extension.IExtension;

/**
 * This scope contains a given set of methods. These methods are to be specified
 * by method patterns.
 * 
 * @author Henning Schulz, Steffen Becker
 * 
 */
public class MethodScope extends AbstractScope {

	private final String[] methods;

	public MethodScope(final IExtension provider, final ScopeDescription fromScopeDescription) {
		super(provider, fromScopeDescription.getId());
		this.methods = fromScopeDescription.getParameter().toArray(new String[]{});
	}

	/**
	 * @return the methods
	 */
	public String[] getMethods() {
		return methods;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		boolean trailingComma = false;

		builder.append("Method Scope [");

		for (final String m : methods) {
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

	@Override
	public IScopeAnalyzer getScopeAnalyzer(final Collection<Class<?>> allLoadedClasses) {
		return new MethodScopeAnalyzer(getMethods());
	}

}
