package org.aim.description.scopes;

import org.aim.aiminterface.description.scope.Scope;
import org.aim.aiminterface.description.scope.ScopeType;
import org.codehaus.jackson.annotate.JsonProperty;

public abstract class ClassScope extends Scope {

	private final String[] targetClasses;

	public ClassScope(@JsonProperty("targetClasses") final String[] targetClasses, @JsonProperty("id") final long id, @JsonProperty final ScopeType scopeType) {
		super(id, scopeType);
		this.targetClasses = targetClasses;
	}

	/**
	 * @return the target classes
	 */
	public String[] getTargetClasses() {
		return targetClasses;
	}

}