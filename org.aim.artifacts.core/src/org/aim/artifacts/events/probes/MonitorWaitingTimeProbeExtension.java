package org.aim.artifacts.events.probes;

import java.util.HashSet;
import java.util.Set;

import org.aim.api.events.AbstractEventProbe;
import org.aim.api.events.AbstractEventProbeExtension;
import org.aim.description.scopes.SynchronizedScope;

/**
 * Event probe for gathering the waiting times on monitor(synchronization)
 * requests.
 * 
 * @author Alexander Wert
 * 
 */
public class MonitorWaitingTimeProbeExtension extends AbstractEventProbeExtension {

	@Override
	public AbstractEventProbe createExtensionArtifact() {
		return new MonitorWaitingTimeProbe(this);
	}

	@Override
	public Class<? extends AbstractEventProbe> getProbeClass() {
		return MonitorWaitingTimeProbe.class;
	}

	@Override
	public Set<Class<?>> getScopeDependencies() {
		Set<Class<?>> supportedScopes = new HashSet<>();
		supportedScopes.add(SynchronizedScope.class);
		return supportedScopes;
	}

}
