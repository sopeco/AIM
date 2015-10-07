package org.aim.artifacts.scopes;

import org.aim.aiminterface.description.scope.ScopeDescription;
import org.aim.api.instrumentation.Scope;
import org.lpe.common.extension.IExtension;

public abstract class ClassScope extends Scope {

	private final String[] targetClasses;

	protected ClassScope(final IExtension provider, final long id, final String[] targetClasses) {
		super(provider, id);
		this.targetClasses = targetClasses;
	}
	
	public ClassScope(final IExtension provider, final ScopeDescription fromDescription) {
		this(provider,fromDescription.getId(),fromDescription.getParameter().toArray(new String[]{}));
	}

	/**
	 * @return the target classes
	 */
	public String[] getTargetClasses() {
		return targetClasses;
	}

}