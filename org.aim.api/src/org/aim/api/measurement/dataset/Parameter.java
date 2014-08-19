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
package org.aim.api.measurement.dataset;

import org.lpe.common.util.LpeSupportedTypes;

/**
 * Additional parameter of a record.
 * 
 * @author Alexander Wert
 * 
 */
public class Parameter implements Comparable<Parameter> {
	private String name;
	private Object value;

	/**
	 * Defualt constructor.
	 */
	public Parameter() {
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            parameter name
	 * @param value
	 *            parameter value
	 */
	public Parameter(String name, Object value) {
		setName(name);
		setValue(value);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Object value) {
		if (LpeSupportedTypes.get(value.getClass()) == null) {
			throw new IllegalArgumentException("Unsupported Parameter type!");
		}
		this.value = value;
	}

	/**
	 * Returns the value as the passed type.
	 * 
	 * @param type
	 *            type the value should be casted to
	 * @param <T>
	 *            desired type of the value
	 * @return the value
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue(Class<T> type) {
		return (T) value;
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Parameter other = (Parameter) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int compareTo(Parameter other) {
		int nameCompare = this.getName().compareTo(other.getName());
		if (nameCompare == 0) {
			return ((Comparable) this.getValue()).compareTo(other.getValue());
		} else {
			return nameCompare;
		}
	}

}
