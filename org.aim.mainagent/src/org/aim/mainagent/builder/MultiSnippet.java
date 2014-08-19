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
package org.aim.mainagent.builder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Snippet with configuration options.
 * 
 * @author Alexander Wert
 * 
 */
public class MultiSnippet {

	/**
	 * Mappings from pre-conditional method name, to snippet artifacts
	 */
	private final Map<String, Class<?>> variables = new HashMap<>();
	private final Map<Set<String>, String> beforePart = new HashMap<>();
	private final Map<Set<String>, String> afterPart = new HashMap<>();
	private String incrementalPart;

	/**
	 * @return the variables
	 */
	public Map<String, Class<?>> getVariables() {
		return variables;
	}

	/**
	 * @return the beforePart
	 */
	public Map<Set<String>, String> getBeforePart() {
		return beforePart;
	}

	/**
	 * @return the afterPart
	 */
	public Map<Set<String>, String> getAfterPart() {
		return afterPart;
	}

	/**
	 * @return the incrementalPart
	 */
	public String getIncrementalPart() {
		if (incrementalPart == null) {
			incrementalPart = "";
		}
		return incrementalPart;
	}

	/**
	 * @param incrementalPart
	 *            the incrementalPart to set
	 */
	public void setIncrementalPart(String incrementalPart) {
		this.incrementalPart = incrementalPart;
	}

	/**
	 * Returns before part which matches the given method name.
	 * 
	 * @param methodName
	 *            method name to match
	 * @return instrumentation before part
	 */
	public String getBeforePart(String methodName) {
		for (Set<String> nameRequirements : getBeforePart().keySet()) {
			for (String nameRequirement : nameRequirements) {
				if (methodName.contains(nameRequirement)) {
					return getBeforePart().get(nameRequirements);
				}
			}
		}
		String defaultValue = getBeforePart().get(Collections.EMPTY_SET);
		return defaultValue == null ? "" : defaultValue;
	}

	/**
	 * Returns after part which matches the given method name.
	 * 
	 * @param methodName
	 *            method name to match
	 * @return instrumentation after part
	 */
	public String getAfterPart(String methodName) {
		for (Set<String> nameRequirements : getAfterPart().keySet()) {
			for (String nameRequirement : nameRequirements) {
				if (methodName.contains(nameRequirement)) {
					return getAfterPart().get(nameRequirements);
				}
			}
		}
		String defaultValue = getAfterPart().get(Collections.EMPTY_SET);
		return defaultValue == null ? "" : defaultValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((afterPart == null) ? 0 : afterPart.hashCode());
		result = prime * result + ((beforePart == null) ? 0 : beforePart.hashCode());
		result = prime * result + ((incrementalPart == null) ? 0 : incrementalPart.hashCode());
		result = prime * result + ((variables == null) ? 0 : variables.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MultiSnippet other = (MultiSnippet) obj;
		if (afterPart == null) {
			if (other.afterPart != null) {
				return false;
			}
		} else if (!afterPart.equals(other.afterPart)) {
			return false;
		}
		if (beforePart == null) {
			if (other.beforePart != null) {
				return false;
			}
		} else if (!beforePart.equals(other.beforePart)) {
			return false;
		}
		if (incrementalPart == null) {
			if (other.incrementalPart != null) {
				return false;
			}
		} else if (!incrementalPart.equals(other.incrementalPart)) {
			return false;
		}
		if (variables == null) {
			if (other.variables != null) {
				return false;
			}
		} else if (!variables.equals(other.variables)) {
			return false;
		}
		return true;
	}

}
