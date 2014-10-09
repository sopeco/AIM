package org.aim.api.events;

import java.util.Collections;
import java.util.Set;

import org.lpe.common.config.ConfigParameterDescription;
import org.lpe.common.extension.IExtension;

/**
 * Abstract class for all event probe extensions.
 * 
 * @author Alexander Wert
 * 
 */
public abstract class AbstractEventProbeExtension implements IExtension<AbstractEventProbe> {
	@Override
	public String getName() {
		return getProbeClass().getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<ConfigParameterDescription> getConfigParameters() {
		return Collections.EMPTY_SET;
	}

	/**
	 * Returns the class of the probe.
	 * 
	 * @return class of the probe
	 */
	public abstract Class<? extends AbstractEventProbe> getProbeClass();

	/**
	 * Returns the type of the scope this probe is applicable to.
	 * 
	 * @return class of the scope
	 */
	public abstract Set<Class<?>> getScopeDependencies();
}
