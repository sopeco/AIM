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
 * This scope refers to all constructor calls of the given classes.
 * 
 * @author Henning Schulz
 * 
 */
public class ConstructorScope extends AbstractScope {

	private final String[] targetClasses;

	protected ConstructorScope(final IExtension provider, final long id, final String[] targetClasses) {
		super(provider, id);
		this.targetClasses = targetClasses;
	}

	public ConstructorScope(final IExtension provider, final ScopeDescription fromDescription) {
		this(provider, fromDescription.getId(), fromDescription.getParameter().toArray(new String[] {}));
	}

	/**
	 * @return the target classes
	 */
	public String[] getTargetClasses() {
		return targetClasses;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		boolean trailingComma = false;

		builder.append("Constructor Scope [");

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

	@Override
	public IScopeAnalyzer getScopeAnalyzer(final Collection<Class<?>> allLoadedClasses) {
		return new ConstructorScopeAnalyzer(getTargetClasses());
	}

}
