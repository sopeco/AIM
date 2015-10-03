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
import org.aim.aiminterface.exceptions.InstrumentationException;
import org.aim.api.instrumentation.APIScopeAnalyzer;
import org.aim.api.instrumentation.AbstractInstAPIScope;
import org.aim.api.instrumentation.AbstractInstApiScopeExtension;
import org.aim.api.instrumentation.IScopeAnalyzer;
import org.aim.api.instrumentation.MethodsEnclosingScope;
import org.lpe.common.extension.ExtensionRegistry;
import org.lpe.common.extension.IExtension;

/**
 * This scope refers to all methods of the given API. The APIScope is an
 * extension point and can be extended by using custom API names.
 * 
 * @author Henning Schulz
 * 
 */
public class APIScope extends MethodsEnclosingScope {

	private final String apiName;

	protected APIScope(final IExtension provider, final long id, final String apiName) {
		super(provider, id);
		this.apiName = apiName;
	}

	public APIScope(final IExtension provider, final ScopeDescription fromDescription) {
		this(provider, fromDescription.getId(), fromDescription.getParameter().get(0));
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

	@Override
	public IScopeAnalyzer getScopeAnalyzer(final Collection<Class<?>> allLoadedClasses) throws InstrumentationException {
		final AbstractInstAPIScope apiScopeInstance = ExtensionRegistry.getSingleton().getExtensionArtifact(
				AbstractInstApiScopeExtension.class, apiName);

		if (apiScopeInstance == null) {
			throw new InstrumentationException("Unable to instantiate scope analyzer for API scope type "
					+ apiName);
		}

		return new APIScopeAnalyzer(apiScopeInstance, allLoadedClasses);
	}

}
