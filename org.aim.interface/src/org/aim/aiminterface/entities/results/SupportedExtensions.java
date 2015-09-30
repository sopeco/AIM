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
package org.aim.aiminterface.entities.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Container for the information about supported extensions.
 * 
 * @author Alexander Wert
 * 
 */
public class SupportedExtensions {

	private final List<String> samplerExtensions;
	private final List<String> apiScopeExtensions;
	private final Map<String, Set<String>> probeExtensionsMapping;
	private final List<String> customScopeExtensions;

	public SupportedExtensions(final List<String> samplerExtensions, final List<String> apiScopeExtensions,
			final Map<String, Set<String>> probeExtensionsMapping, final List<String> customScopeExtensions) {
		super();
		this.samplerExtensions = new ArrayList<>(samplerExtensions);
		this.apiScopeExtensions = new ArrayList<>(apiScopeExtensions);
		this.probeExtensionsMapping = new HashMap<>(probeExtensionsMapping);
		this.customScopeExtensions = new ArrayList<>(customScopeExtensions);
	}

	/**
	 * @return the samplerExtensions
	 */
	public List<String> getSamplerExtensions() {
		return Collections.unmodifiableList(samplerExtensions);
	}

	/**
	 * @return the apiScopeExtensions
	 */
	public List<String> getApiScopeExtensions() {
		return Collections.unmodifiableList(apiScopeExtensions);
	}

	/**
	 * @return the customScopeExtensions
	 */
	public List<String> getCustomScopeExtensions() {
		return Collections.unmodifiableList(customScopeExtensions);
	}

	/**
	 * @return the probeExtensionsMapping
	 */
	public Map<String, Set<String>> getProbeExtensionsMapping() {
		return Collections.unmodifiableMap(probeExtensionsMapping);
	}
}
