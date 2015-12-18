package org.aim.mainagent;

import static org.lpe.common.util.web.LpeWebUtils.addServlet;

import java.io.IOException;

import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.aim.logging.AIMLoggingConfig;
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
import org.aim.mainagent.service.helper.AdaptiveFacadeProvider;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
	private static String jmxport = "9010";
	private static String jmxhost = "localhost";
	public static final String URL_PATH_INSTRUMENTATION = JsonAdaptiveInstrumentationClient.URL_PATH_INSTRUMENTATION;
	public static final String URL_PATH_MEASUREMENT = JsonAdaptiveInstrumentationClient.URL_PATH_MEASUREMENT;


	public static void main(final String[] args) throws InterruptedException {
		parseCommandLine(args,createOptions());
		startServer();
		while (true) {
			getLogger().info("Json instrumentation adapter still running...");
			Thread.sleep(1l * 1000 * 60 * 5);
		}
	}

	private static void parseCommandLine(final String[] args, final Options options) {
		final CommandLineParser parser = new DefaultParser();
		try {
			final CommandLine cmd = parser.parse( options, args);
			if (cmd.hasOption("h")) {
				final HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "json adapter", options );
				System.exit(-1);
			}
			port = cmd.getOptionValue("port", "8888");
			jmxport = cmd.getOptionValue("jmxport","9010");
			jmxhost  = cmd.getOptionValue("jmxhost","localhost");
			AdaptiveFacadeProvider.jmxPort = jmxport;
			AdaptiveFacadeProvider.jmxHost = jmxhost;
			final AIMLoggingConfig loggerConfig = new AIMLoggingConfig();
			loggerConfig.setLoggingLevel(Integer.parseInt(cmd.getOptionValue("loglevel", "3")));
			AIMLoggerFactory.initialize(loggerConfig);
		} catch (final ParseException e) {
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "json adapter", options );
			System.exit(-1);
		}		
	}

	private static Options createOptions() {
		final Options options = new Options();
		options.addOption( Option.builder("port")
				.desc ( "Port where the Json adapter listens" )
                .hasArg()
                .type(Integer.class)
                .argName("PORT")
                .build() );
		options.addOption( Option.builder("jmxport")
				.desc ( "Port where the jmx server is listening" )
                .hasArg()
                .type(Integer.class)
                .argName("PORT")
                .build() );
		options.addOption( Option.builder("loglevel")
				.desc ( "The loglevel to use (0=Debug ... 3=Error (default))" )
                .hasArg()
                .type(Integer.class)
                .argName("LOGLEVEL")
                .build() );
		options.addOption( Option.builder("jmxhost")
				.desc ( "Host where the jmx server is listening" )
                .hasArg()
                .argName("SERVER")
                .build() );
		options.addOption( Option.builder("h").longOpt("help").build() );
		return options;
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
