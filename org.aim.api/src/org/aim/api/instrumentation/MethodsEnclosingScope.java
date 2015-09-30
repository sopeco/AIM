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
package org.aim.api.instrumentation;

import java.util.Collection;

import org.aim.aiminterface.description.scope.ScopeDescription;
import org.aim.aiminterface.exceptions.InstrumentationException;
import org.lpe.common.extension.IExtension;

/**
 * This is a common interface for all scopes referring to sets of method
 * enclosures.
 * 
 * @author Henning Schulz, Steffen Becker
 * 
 */
public abstract class MethodsEnclosingScope extends Scope {

	protected MethodsEnclosingScope(final IExtension provider, final long id) {
		super(provider, id);
	}

	public MethodsEnclosingScope(final IExtension provider, final ScopeDescription fromDescription) {
		super(provider, fromDescription);
	}

	public abstract IScopeAnalyzer getScopeAnalyzer(Collection<Class<?>> allLoadedClasses) throws InstrumentationException;
}
