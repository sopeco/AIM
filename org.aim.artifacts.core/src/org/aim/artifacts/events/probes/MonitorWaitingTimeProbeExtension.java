package org.aim.artifacts.events.probes;

import java.util.Collections;
import java.util.Set;

import org.aim.api.events.AbstractEventProbe;
import org.lpe.common.config.ConfigParameterDescription;
import org.lpe.common.extension.IExtension;

/**
 * Event probe for gathering the waiting times on monitor(synchronization)
 * requests.
 * 
 * @author Alexander Wert
 * 
 */
public class MonitorWaitingTimeProbeExtension implements IExtension<AbstractEventProbe> {

	@Override
	public String getName() {
		return MonitorWaitingTimeProbe.MODEL_PROBE.getName();
	}

	@Override
	public AbstractEventProbe createExtensionArtifact() {
		return new MonitorWaitingTimeProbe(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<ConfigParameterDescription> getConfigParameters() {
		return Collections.EMPTY_SET;
	}

}
