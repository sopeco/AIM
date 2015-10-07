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
	private final long scopeId;
	private final Class<?> clazz;
	private final String methodSignature;

	/**
	 * Constructor.
	 * 
	 * @param clazz
	 *            Class to instrument.
	 * @param methodSignature
	 *            Signature of the method to instrument. This signature must
	 *            contain the fully qualified name of the declaring class!
	 */
	public FlatScopeEntity(final Class<?> clazz, final String methodSignature, final long id) {
		super();
		this.clazz = clazz;
		this.methodSignature = methodSignature;
		this.scopeId = id;
	}

	public FlatScopeEntity(final Class<?> clazz, final String methodSignature) {
		this(clazz,methodSignature,0L);
	}

	/**
	 * @return the clazz
	 */
	public Class<?> getClazz() {
		return clazz;
	}

	/**
	 * @return the methodSignature
	 */
	public String getMethodSignature() {
		return methodSignature;
	}

	/**
	 * @return the scopeId
	 */
	public Long getScopeId() {
		return scopeId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.getName().hashCode());
		result = prime * result + ((methodSignature == null) ? 0 : methodSignature.hashCode());
		result = prime * result + (int) (scopeId ^ (scopeId >>> 32));
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
		final FlatScopeEntity other = (FlatScopeEntity) obj;
		if (clazz == null) {
			if (other.clazz != null) {
				return false;
			}
		} else if (!clazz.getName().equals(other.clazz.getName()) && !(clazz.getClassLoader() == other.clazz.getClassLoader())) {
			return false;
		}
		if (methodSignature == null) {
			if (other.methodSignature != null) {
				return false;
			}
		} else if (!methodSignature.equals(other.methodSignature)) {
			return false;
		}
		if (scopeId != other.scopeId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "FlatScopeEntity [scopeId=" + scopeId + ", clazz=" + clazz + ", methodSignature=" + methodSignature
				+ "]";
	}

}
