package org.aim.api.events;

import org.lpe.common.extension.AbstractExtensionArtifact;
import org.lpe.common.extension.IExtension;

/**
 * Abstract class for all event probes.
 * 
 * @author Alexander Wert
 * 
 */
public abstract class AbstractEventProbe extends AbstractExtensionArtifact implements IEventProbe {

	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider
	 */
	public AbstractEventProbe(IExtension<?> provider) {
		super(provider);
	}

}
