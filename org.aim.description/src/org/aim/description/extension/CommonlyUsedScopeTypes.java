package org.aim.description.extension;

import org.aim.aiminterface.description.scope.ScopeType;

public final class CommonlyUsedScopeTypes {

	private CommonlyUsedScopeTypes() {};
	
	public static final ScopeType CLASS_SCOPE_TYPE = new ScopeType("ClassScopeType");
	public static final ScopeType METHOD_ENCLOSING_SCOPE_TYPE = new ScopeType("MethodEnclosingScopeType");
	public static final ScopeType GLOBAL_SCOPE_TYPE = new ScopeType("GlobalScopeType");
	public static final ScopeType API_SCOPE_TYPE = new ScopeType("APIScopeType");
	public static final ScopeType SYNCHRONIZED_SCOPE_TYPE = new ScopeType("SynchronizedScopeType");
}
