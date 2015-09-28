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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Container for the information about supported extensions.
 * 
 * @author Alexander Wert
 * 
 */
@XmlRootElement
public class SupportedExtensions implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3482210215382858791L;
	private List<String> samplerExtensions;
	private List<String> apiScopeExtensions;
	private Map<String, Set<String>> probeExtensionsMapping;
	private List<String> customScopeExtensions;

	/**
	 * @return the samplerExtensions
	 */
	public List<String> getSamplerExtensions() {
		if (samplerExtensions == null) {
			samplerExtensions = new ArrayList<>();
		}
		return samplerExtensions;
	}

	/**
	 * @param samplerExtensions
	 *            the samplerExtensions to set
	 */
	public void setSamplerExtensions(final List<String> samplerExtensions) {
		this.samplerExtensions = samplerExtensions;
	}

	/**
	 * @return the apiScopeExtensions
	 */
	public List<String> getApiScopeExtensions() {
		if (apiScopeExtensions == null) {
			apiScopeExtensions = new ArrayList<>();
		}
		return apiScopeExtensions;
	}

	/**
	 * @param apiScopeExtensions
	 *            the apiScopeExtensions to set
	 */
	public void setApiScopeExtensions(final List<String> apiScopeExtensions) {
		this.apiScopeExtensions = apiScopeExtensions;
	}

	/**
	 * @return the enclosingProbeExtensions
	 */
	@JsonIgnore
	public Set<String> getProbeExtensions() {
		return getProbeExtensionsMapping().keySet();
	}

	/**
	 * @param probeExtension
	 *            the enclosingProbeExtension to add
	 * @param supportedScopes
	 *            scopes which are supported by this probe
	 */
	@JsonIgnore
	public void addProbeExtension(final String probeExtension, final Set<String> supportedScopes) {
		getProbeExtensionsMapping().put(probeExtension, supportedScopes);
	}

	/**
	 * @return the customScopeExtensions
	 */
	public List<String> getCustomScopeExtensions() {
		if (customScopeExtensions == null) {
			customScopeExtensions = new ArrayList<>();
		}
		return customScopeExtensions;
	}

	/**
	 * @param customScopeExtensions
	 *            the customScopeExtensions to set
	 */
	public void setCustomScopeExtensions(final List<String> customScopeExtensions) {
		this.customScopeExtensions = customScopeExtensions;
	}

	/**
	 * @return the probeExtensionsMapping
	 */
	public Map<String, Set<String>> getProbeExtensionsMapping() {
		if (probeExtensionsMapping == null) {
			probeExtensionsMapping = new HashMap<>();
		}
		return probeExtensionsMapping;
	}

	/**
	 * @param probeExtensionsMapping
	 *            the probeExtensionsMapping to set
	 */
	public void setProbeExtensionsMapping(final Map<String, Set<String>> probeExtensionsMapping) {
		this.probeExtensionsMapping = probeExtensionsMapping;
	}

}
