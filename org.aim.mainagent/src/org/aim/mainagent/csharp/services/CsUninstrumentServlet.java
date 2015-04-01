package org.aim.mainagent.csharp.services;

import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.aim.mainagent.csharp.DotNetAgent;
import org.aim.mainagent.sampling.Sampling;
import org.aim.mainagent.service.Service;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

public class CsUninstrumentServlet implements Service {
	private static final AIMLogger LOGGER = AIMLoggerFactory.getLogger(CsUninstrumentServlet.class);

	@Override
	public void doService(Request req, Response resp) throws Exception {
		LOGGER.info("Requested reversion of instrumentation ...");

		if (DotNetAgent.getServiceHandler() != null) {
			DotNetAgent.getServiceHandler().uninstrument();
		}
		
		Sampling.getInstance().clearMonitoringJobs();
		LOGGER.info("Reversion of instrumentation completed!");
	}
}
