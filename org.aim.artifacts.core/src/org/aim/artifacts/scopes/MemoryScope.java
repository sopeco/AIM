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
import org.aim.api.instrumentation.Scope;
import org.lpe.common.extension.IExtension;


/**
 * This scope refers to tasks related to memory.
 * 
 * @author Henning Schulz, Steffen Becker
 * 
 */
public class MemoryScope extends Scope {

	public MemoryScope(final IExtension provider, final ScopeDescription fromDescription) {
		super(provider, fromDescription);
	}
	
	MemoryScope(final IExtension provider) {
		super(provider, 0);
	}


	@Override
	public String toString() {
		return "Memory Scope";
	}
}
