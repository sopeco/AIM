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

import org.aim.api.instrumentation.AbstractEnclosingProbe;

/**
 * Internal flat representation of the instrumentation description.
 * 
 * @author Alexander Wert
 * 
 */
public class FlatInstrumentationEntity extends FlatScopeEntity {

	private Class<? extends AbstractEnclosingProbe> probeType;

	/**
	 * Constructor.
	 * 
	 * @param scopeEntity
	 *            scope entity to extend
	 * @param probeType
	 *            probe type to add
	 */
	public FlatInstrumentationEntity(FlatScopeEntity scopeEntity, Class<? extends AbstractEnclosingProbe> probeType) {
		this(scopeEntity.getClazz(), scopeEntity.getMethodSignature(), probeType);
	}

	/**
	 * Constructor.
	 * 
	 * @param clazz
	 *            Class to instrument.
	 * @param methodSignature
	 *            Signature of the method to instrument. This signature must
	 *            contain the fully qualified name of the declaring class!
	 * @param probeType
	 *            type of the probe to inject (here the type is the fully
	 *            qualified class name of the corresponding probe
	 *            implementation)
	 */
	public FlatInstrumentationEntity(Class<?> clazz, String methodSignature,
			Class<? extends AbstractEnclosingProbe> probeType) {
		super(clazz, methodSignature);
		this.probeType = probeType;
	}

	/**
	 * @return the probeType
	 */
	public Class<? extends AbstractEnclosingProbe> getProbeType() {
		return probeType;
	}

	/**
	 * @param probeType
	 *            the probeType to set
	 */
	public void setProbeType(Class<? extends AbstractEnclosingProbe> probeType) {
		this.probeType = probeType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((probeType == null) ? 0 : probeType.hashCode());
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
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FlatInstrumentationEntity other = (FlatInstrumentationEntity) obj;
		if (probeType == null) {
			if (other.probeType != null) {
				return false;
			}
		} else if (!probeType.equals(other.probeType)) {
			return false;
		}
		return true;
	}

}
