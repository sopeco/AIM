package org.aim.mainagent.csharp.services;

import org.aim.description.InstrumentationDescription;

public interface CsServiceHandler {

	public void instrument(InstrumentationDescription description);
	
	public void uninstrument();
	
}
