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

import org.aim.description.scopes.MethodsEnclosingScope;
import org.lpe.common.extension.IExtension;
import org.lpe.common.extension.IExtensionArtifact;

/**
 * Common class for all custom scopes.
 * 
 * @author Alexander Wert
 * 
 */
public abstract class AbstractCustomScope extends MethodsEnclosingScope implements IExtensionArtifact, IScopeAnalyzer {

	/**
	 * Extension provider.
	 */
	private final IExtension<?> provider;

	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider
	 */
	public AbstractCustomScope(final IExtension<?> provider) {
		super(0);
		this.provider = provider;
	}

	/**
	 * @return returns the provider of this extension.
	 */
	@Override
	public IExtension<?> getProvider() {
		return this.provider;
	}

}
