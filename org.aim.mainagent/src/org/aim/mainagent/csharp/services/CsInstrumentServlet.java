package org.aim.mainagent.csharp.services;

import org.aim.description.InstrumentationDescription;
import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.aim.mainagent.csharp.DotNetAgent;
import org.aim.mainagent.service.Service;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

public class CsInstrumentServlet implements Service {

	private static final AIMLogger LOGGER = AIMLoggerFactory.getLogger(CsInstrumentServlet.class);

	@Override
	public void doService(Request req, Response resp) throws Exception {
		LOGGER.info("Requested instrumentation ...");
		ObjectMapper mapper = new ObjectMapper();
		InstrumentationDescription description = mapper.readValue(req.getInputStream(),
				InstrumentationDescription.class);

		if (DotNetAgent.getServiceHandler() != null) {
			DotNetAgent.getServiceHandler().instrument(description);
		}

		LOGGER.info("Instrumentation finished!");
	}

}
