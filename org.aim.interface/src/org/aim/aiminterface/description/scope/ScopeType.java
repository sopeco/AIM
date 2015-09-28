package org.aim.aiminterface.description.scope;

public class ScopeType {
	
	private final String name;
	private final ScopeType parentScopeType;

	public ScopeType(final String name) {
		super();
		this.name = name;
		this.parentScopeType = null;
	}

	public ScopeType(final String name, final ScopeType parentScopeType) {
		super();
		this.name = name;
		this.parentScopeType = parentScopeType;
	}

	public String getName() {
		return name;
	}
	
	public ScopeType getParentScopeType() {
		return parentScopeType;
	}
	
	public boolean isCompatibleWith(final ScopeType other) {
		if (this.equals(other)) {
			return true;
		} else {
			if (parentScopeType == null) {
				return false;
			} else {
				return this.getParentScopeType().isCompatibleWith(other);
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		final ScopeType other = (ScopeType) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ScopeType [name=" + name + "]";
	}
	
}
