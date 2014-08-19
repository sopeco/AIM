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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the injection code.
 * 
 * @author Alexander Wert
 * 
 */
public class Snippet {

	private final Map<String, Class<?>> variables = new HashMap<>();
	private String beforePart;
	private String afterPart;
	private String incrementalPart;

	/**
	 * @return the beforePart
	 */
	public String getBeforePart() {
		if (beforePart == null) {
			beforePart = "";
		}
		return beforePart;
	}

	/**
	 * @param beforePart
	 *            the beforePart to set
	 */
	public void setBeforePart(String beforePart) {
		this.beforePart = beforePart;
	}

	/**
	 * @return the afterPart
	 */
	public String getAfterPart() {
		if (afterPart == null) {
			afterPart = "";
		}
		return afterPart;
	}

	/**
	 * @param afterPart
	 *            the afterPart to set
	 */
	public void setAfterPart(String afterPart) {
		this.afterPart = afterPart;
	}

	/**
	 * @return the variables
	 */
	public Map<String, Class<?>> getVariables() {
		return variables;
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
		Snippet other = (Snippet) obj;
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
		if (variables == null) {
			if (other.variables != null) {
				return false;
			}
		} else if (!variables.equals(other.variables)) {
			return false;
		}
		return true;
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

}
