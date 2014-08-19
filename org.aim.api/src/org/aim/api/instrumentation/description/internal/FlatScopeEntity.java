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
package org.aim.api.instrumentation.description.internal;

/**
 * Internal flat representation of the scope description.
 * 
 * @author Alexander Wert
 * 
 */
public class FlatScopeEntity {
	private Long scopeId;
	private Class<?> clazz;
	private String methodSignature;

	/**
	 * Constructor.
	 * 
	 * @param clazz
	 *            Class to instrument.
	 * @param methodSignature
	 *            Signature of the method to instrument. This signature must
	 *            contain the fully qualified name of the declaring class!
	 */
	public FlatScopeEntity(Class<?> clazz, String methodSignature) {
		super();
		this.clazz = clazz;
		this.methodSignature = methodSignature;
	}

	/**
	 * @return the clazz
	 */
	public Class<?> getClazz() {
		return clazz;
	}

	/**
	 * @param clazz
	 *            the clazz to set
	 */
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	/**
	 * @return the methodSignature
	 */
	public String getMethodSignature() {
		return methodSignature;
	}

	/**
	 * @param methodSignature
	 *            the methodSignature to set
	 */
	public void setMethodSignature(String methodSignature) {
		this.methodSignature = methodSignature;
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
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((methodSignature == null) ? 0 : methodSignature.hashCode());
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
		FlatScopeEntity other = (FlatScopeEntity) obj;
		if (clazz == null) {
			if (other.clazz != null) {
				return false;
			}
		} else if (!clazz.equals(other.clazz)) {
			return false;
		}
		if (methodSignature == null) {
			if (other.methodSignature != null) {
				return false;
			}
		} else if (!methodSignature.equals(other.methodSignature)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the scopeId
	 */
	public Long getScopeId() {
		return scopeId;
	}

	/**
	 * @param scopeId
	 *            the scopeId to set
	 */
	public void setScopeId(Long scopeId) {
		this.scopeId = scopeId;
	}

}
