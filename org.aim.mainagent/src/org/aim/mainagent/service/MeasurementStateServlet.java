package org.aim.mainagent.service;

import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

public class MeasurementStateServlet implements Service {
	private static final AIMLogger LOGGER = AIMLoggerFactory.getLogger(MeasurementStateServlet.class);

	private static boolean measurementEnabled;

	@Override
	public void doService(Request req, Response resp) throws Exception {
		LOGGER.info("Retrieving measurement state.");

		JsonFactory factory = new JsonFactory();
		ObjectMapper mapper = new ObjectMapper(factory);

		resp.setContentType("application/json");
		mapper.writeValue(resp.getOutputStream(), measurementEnabled);
	}

	/**
	 * Sets the current measurement state.
	 * 
	 * @param state
	 *            is measurement enabled
	 */
	public static void setMeasurementState(boolean state) {
		measurementEnabled = state;
	}
}
