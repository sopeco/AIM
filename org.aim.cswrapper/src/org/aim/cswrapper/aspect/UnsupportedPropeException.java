package org.aim.cswrapper.aspect;

import org.aim.description.probes.MeasurementProbe;

public class UnsupportedPropeException extends RuntimeException {

	public UnsupportedPropeException(MeasurementProbe<?> probe) {
		super("The prope is not supportd yet: " + probe.getName());
	}

	/** */
	private static final long serialVersionUID = 1L;
}
