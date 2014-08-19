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
package org.aim.api.instrumentation.entities;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Container for the information about supported extensions.
 * 
 * @author Alexander Wert
 * 
 */
@XmlRootElement
public class SupportedExtensions {
	private List<String> samplerExtensions;
	private List<String> apiScopeExtensions;
	private List<String> enclosingProbeExtensions;
	private List<String> singlePointProbeExtensions;
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
	public void setSamplerExtensions(List<String> samplerExtensions) {
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
	public void setApiScopeExtensions(List<String> apiScopeExtensions) {
		this.apiScopeExtensions = apiScopeExtensions;
	}

	/**
	 * @return the enclosingProbeExtensions
	 */
	public List<String> getEnclosingProbeExtensions() {
		if (enclosingProbeExtensions == null) {
			enclosingProbeExtensions = new ArrayList<>();
		}
		return enclosingProbeExtensions;
	}

	/**
	 * @param enclosingProbeExtensions
	 *            the enclosingProbeExtensions to set
	 */
	public void setEnclosingProbeExtensions(List<String> enclosingProbeExtensions) {
		this.enclosingProbeExtensions = enclosingProbeExtensions;
	}

	/**
	 * @return the singlePointProbeExtensions
	 */
	public List<String> getSinglePointProbeExtensions() {
		if (singlePointProbeExtensions == null) {
			singlePointProbeExtensions = new ArrayList<>();
		}
		return singlePointProbeExtensions;
	}

	/**
	 * @param singlePointProbeExtensions
	 *            the singlePointProbeExtensions to set
	 */
	public void setSinglePointProbeExtensions(List<String> singlePointProbeExtensions) {
		this.singlePointProbeExtensions = singlePointProbeExtensions;
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
	public void setCustomScopeExtensions(List<String> customScopeExtensions) {
		this.customScopeExtensions = customScopeExtensions;
	}

}
