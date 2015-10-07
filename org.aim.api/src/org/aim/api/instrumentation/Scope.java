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

import org.aim.aiminterface.description.scope.ScopeDescription;
import org.lpe.common.extension.IExtension;
import org.lpe.common.extension.IExtensionArtifact;

/**
 * This is a common interface of all scopes to be used within instrumentation
 * descriptions.
 * 
 * @author Henning Schulz, Steffen Becker
 * 
 */
public abstract class Scope implements IExtensionArtifact {
	private final long id;
	private final IExtension provider;
	
	protected Scope(final IExtension provider, final long id) {
		super();
		this.id = id;
		this.provider = provider;
	}

	public Scope(final IExtension provider, final ScopeDescription fromDescription) {
		this(provider, fromDescription.getId());
	}
	
	public long getId() {
		return id;
	}
		
	@Override
	public IExtension getProvider() {
		return provider;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Scope other = (Scope) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}
}
