package org.aim.artifacts.scopes;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.aim.aiminterface.description.scope.ScopeDescription;
import org.aim.api.instrumentation.Scope;
import org.lpe.common.config.ConfigParameterDescription;
import org.lpe.common.extension.IExtension;
import org.lpe.common.extension.IExtensionArtifact;

public class GenericScopeExtension implements IExtension {

	private final Class<? extends Scope> scopeClass;
	
	public GenericScopeExtension(final Class<? extends Scope> scopeClass) {
		super();
		this.scopeClass = scopeClass;
	}

	@Override
	public String getName() {
		return scopeClass.getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <EA extends IExtensionArtifact> EA createExtensionArtifact(final String... args) {
		final ScopeDescription scopeDescription = new ScopeDescription(scopeClass.getName(), 0, Arrays.asList(args));
		EA artifact;
		try {
			artifact = (EA)scopeClass.getConstructor(IExtension.class, ScopeDescription.class).newInstance(this,scopeDescription);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
		return artifact;
	}

	@Override
	public Set<ConfigParameterDescription> getConfigParameters() {
		return Collections.emptySet();
	}

}
