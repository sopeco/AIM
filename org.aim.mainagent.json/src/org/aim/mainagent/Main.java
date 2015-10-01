package org.aim.mainagent;

import static org.lpe.common.util.web.LpeWebUtils.addServlet;

import java.io.IOException;

import org.aim.artifacts.instrumentation.JsonAdaptiveInstrumentationClient;
import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.aim.mainagent.service.CurrentTimeServlet;
import org.aim.mainagent.service.DisableMeasurementServlet;
import org.aim.mainagent.service.EnableMeasurementServlet;
import org.aim.mainagent.service.GetDataServlet;
import org.aim.mainagent.service.GetStateServlet;
import org.aim.mainagent.service.GetSupportedExtensionsServlet;
import org.aim.mainagent.service.InstrumentServlet;
import org.aim.mainagent.service.MeasureOverheadServlet;
import org.aim.mainagent.service.MeasurementStateServlet;
import org.aim.mainagent.service.TestConnectionServlet;
import org.aim.mainagent.service.UninstrumentServlet;
import org.glassfish.grizzly.http.server.HttpServer;

public class Main {
	private static AIMLogger logger;

	private static AIMLogger getLogger() {
		if (logger == null) {
			logger = AIMLoggerFactory.getLogger(Main.class);
		}
		return logger;
	}

	public static final String PATH_PREFIX = JsonAdaptiveInstrumentationClient.PATH_PREFIX;
	private static String port = "8888";
	public static final String URL_PATH_INSTRUMENTATION = JsonAdaptiveInstrumentationClient.URL_PATH_INSTRUMENTATION;
	public static final String URL_PATH_MEASUREMENT = JsonAdaptiveInstrumentationClient.URL_PATH_MEASUREMENT;


	public static void main(final String[] args) throws InterruptedException {
		startServer();
		Thread.sleep(10000000);
	}

	private static void startServer() {
		final HttpServer server = HttpServer.createSimpleServer("", Integer.parseInt(port));
		try {
			addServlet(server, new TestConnectionServlet(), "/" + PATH_PREFIX + "/" + "testConnection");

			addServlet(server, new InstrumentServlet(), "/" + PATH_PREFIX + "/" + URL_PATH_INSTRUMENTATION + "/instrument");
			addServlet(server, new UninstrumentServlet(),"/" + PATH_PREFIX + "/" + URL_PATH_INSTRUMENTATION + "/uninstrument");
			addServlet(server, new GetStateServlet(), "/" + PATH_PREFIX + "/" +URL_PATH_INSTRUMENTATION + "/getState");
			addServlet(server, new GetSupportedExtensionsServlet(), "/" + PATH_PREFIX + "/" +URL_PATH_INSTRUMENTATION
					+ "/getSupportedExtensions");

			addServlet(server, new EnableMeasurementServlet(), "/" + PATH_PREFIX + "/" +URL_PATH_MEASUREMENT + "/enable");
			addServlet(server, new DisableMeasurementServlet(), "/" + PATH_PREFIX + "/" +URL_PATH_MEASUREMENT + "/disable");
			addServlet(server, new GetDataServlet(), "/" + PATH_PREFIX + "/" +URL_PATH_MEASUREMENT + "/getdata");
			addServlet(server, new CurrentTimeServlet(), "/" + PATH_PREFIX + "/" +URL_PATH_MEASUREMENT + "/currentTime");
			addServlet(server, new MeasureOverheadServlet(), "/" + PATH_PREFIX + "/" +URL_PATH_MEASUREMENT + "/measureOverhead");
			addServlet(server, new MeasurementStateServlet(), "/" + PATH_PREFIX + "/" + URL_PATH_MEASUREMENT + "/monitoringState");
			server.start();
			getLogger().info("Started Instrumentation Agent Server: {}.", getAddress());

		} catch (final IllegalArgumentException iae) {
			getLogger().error("Illegal Argument Exception happend in main method of ServerLauncher: {}",
					iae.getMessage());
		} catch (final IOException ioe) {
			getLogger().error("IO Exception happend in main method of ServerLauncher: {}", ioe.getMessage());
		}
	}
	
	/**
	 * 
	 * @return Returns the server address for the REST service.
	 */
	private static String getAddress() {
		return "http://0.0.0.0:" + port + "/" + PATH_PREFIX;
	}

}
