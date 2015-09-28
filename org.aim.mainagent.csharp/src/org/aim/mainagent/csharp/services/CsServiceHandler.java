package org.aim.mainagent.csharp.services;

import org.aim.aiminterface.description.instrumentation.InstrumentationDescription;

public interface CsServiceHandler {

	public void instrument(InstrumentationDescription description);
	
	public void uninstrument();
	
}
