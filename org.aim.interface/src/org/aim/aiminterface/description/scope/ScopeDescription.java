package org.aim.aiminterface.description.scope;

import java.beans.ConstructorProperties;
import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ScopeDescription {
	private final String name;
	private final long id;
	private final List<String> parameter;
	
	@ConstructorProperties({"name","id","parameter"})
	@JsonCreator
	public ScopeDescription(
			@JsonProperty("name") final String name, 
			@JsonProperty("id") final long id, 
			@JsonProperty("parameter") final List<String> parameter) {
		super();
		this.name = name;
		this.id = id;
		this.parameter = parameter;
	}

	public String getName() {
		return name;
	}

	public long getId() {
		return id;
	}

	public List<String> getParameter() {
		return parameter;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
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
		final ScopeDescription other = (ScopeDescription) obj;
		if (id != other.id) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (parameter == null) {
			if (other.parameter != null) {
				return false;
			}
		} else if (!parameter.equals(other.parameter)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return name + (parameter.isEmpty() ? "" :  " " + parameter);
	}
}
