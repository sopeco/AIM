package org.aim.mainagent.csharp;

import static org.lpe.common.util.web.LpeWebUtils.addServlet;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.aim.api.measurement.collector.AbstractDataSource;
import org.aim.api.measurement.collector.CollectorFactory;
import org.aim.artifacts.instrumentation.InstrumentationClient;
import org.aim.artifacts.measurement.collector.MemoryDataSource;
import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.aim.logging.AIMLoggingConfig;
import org.aim.logging.LoggingLevel;
import org.aim.mainagent.CEventAgentAdapter;
import org.aim.mainagent.csharp.services.CsInstrumentServlet;
import org.aim.mainagent.csharp.services.CsServiceHandler;
import org.aim.mainagent.csharp.services.CsUninstrumentServlet;
import org.aim.mainagent.service.CurrentTimeServlet;
import org.aim.mainagent.service.DisableMeasurementServlet;
import org.aim.mainagent.service.EnableMeasurementServlet;
import org.aim.mainagent.service.GetDataServlet;
import org.aim.mainagent.service.GetStateServlet;
import org.aim.mainagent.service.GetSupportedExtensionsServlet;
import org.aim.mainagent.service.MeasureOverheadServlet;
import org.aim.mainagent.service.MeasurementStateServlet;
import org.aim.mainagent.service.TestConnectionServlet;
import org.glassfish.grizzly.http.server.HttpServer;
import org.lpe.common.config.GlobalConfiguration;
import org.lpe.common.extension.ExtensionRegistry;
import org.lpe.common.util.system.LpeSystemUtils;

public final class DotNetAgent {

	private static AIMLogger logger;

	private static Boolean isInitialized = false;

	private static AIMLogger getLogger() {
		if (logger == null) {
			logger = AIMLoggerFactory.getLogger(DotNetAgent.class);
		}
		return logger;
	}

	// //////////////////////

	private static final AIMLoggingConfig aimLoggingConfig = new AIMLoggingConfig();

	private static final String PORT_KEY = "port";
	private static final String DATA_COLLECTOR_KEY = "collector";
	private static final String PLUGINS_ROOT_KEY = "pluginsRootDir";
	private static final String LOGGING_TYPE_KEY = "logType";
	private static final String LOGGING_FILE_KEY = "logFile";
	private static final String LOGGING_LEVEL_KEY = "logLevel";
	private static final String DEFAULT_PLUGINS_FOLDER = "plugins";

	public static final String URL_PATH_INSTRUMENTATION = InstrumentationClient.URL_PATH_INSTRUMENTATION;
	public static final String URL_PATH_MEASUREMENT = InstrumentationClient.URL_PATH_MEASUREMENT;
	public static final String PATH_PREFIX = InstrumentationClient.PATH_PREFIX;

	private static String pluginsRoot;
	private static String collectorType = MemoryDataSource.class.getName();
	private static final Properties properties = new Properties();
	private static String port = "8888";

	private static CsServiceHandler serviceHandler;

	/**
	 * @param instrumentListener
	 *            the instrumentListener to set
	 */
	public static void setServiceHandler(final CsServiceHandler instrumentListener) {
		DotNetAgent.serviceHandler = instrumentListener;
	}

	/**
	 * @return the instrumentListener
	 */
	public static CsServiceHandler getServiceHandler() {
		return serviceHandler;
	}

	public static void start(final String agentConfig) {
		synchronized (isInitialized) {
			if (isInitialized) {
				getLogger().debug("C# agent already started");
				return;
			} else {
				getLogger().debug("Starting C# agent.");
				isInitialized = true;
			}
		}

		try {
			parseArgs(agentConfig);
			AIMLoggerFactory.initialize(aimLoggingConfig);

			final boolean cAgentInitializedSuccessfully = CEventAgentAdapter.initialize();
			if (!cAgentInitializedSuccessfully) {
				getLogger().warn("The C event agent could not be initialized and will not be used therefore.");
				// TODO: handle this case
			}

			initializeGlobalConfig();
			LpeSystemUtils.loadNativeLibraries();
			initDataCollector();
			startServer();
		} catch (final Exception e) {
			e.printStackTrace();
			getLogger().error("Agent ERROR: {}", e);
		}
	}

	private static void initializeGlobalConfig() {
		if (pluginsRoot == null) {
			pluginsRoot = System.getProperty("user.dir");
		}

		final Properties coreProperties = new Properties();
		coreProperties.setProperty(ExtensionRegistry.APP_ROOT_DIR_PROPERTY_KEY, pluginsRoot);
		coreProperties.setProperty(ExtensionRegistry.PLUGINS_FOLDER_PROPERTY_KEY, DEFAULT_PLUGINS_FOLDER);
		GlobalConfiguration.initialize(coreProperties);
	}

	/**
	 * Initializes the data collector.
	 */
	private static void initDataCollector() {
		final AbstractDataSource dataSource = CollectorFactory.createDataSource(collectorType, properties);
		AbstractDataSource.setDefaultDataSource(dataSource);
	}

	private static void startServer() {
		final HttpServer server = HttpServer.createSimpleServer("", Integer.parseInt(port));
		try {

			addServlet(server, new TestConnectionServlet(),  "/" + PATH_PREFIX + "/" + "testConnection");

			addServlet(server, new CsInstrumentServlet(), "/" + PATH_PREFIX + "/" + URL_PATH_INSTRUMENTATION + "/instrument");
			addServlet(server, new CsUninstrumentServlet(),  "/" + PATH_PREFIX + "/" + URL_PATH_INSTRUMENTATION + "/uninstrument");
			addServlet(server, new GetStateServlet(), "/" + PATH_PREFIX + "/" + URL_PATH_INSTRUMENTATION + "/getState");
			addServlet(server, new GetSupportedExtensionsServlet(), "/" + PATH_PREFIX + "/" + URL_PATH_INSTRUMENTATION
					+ "/getSupportedExtensions");

			addServlet(server, new EnableMeasurementServlet(), "/" + PATH_PREFIX + "/" + URL_PATH_MEASUREMENT + "/enable");
			addServlet(server, new DisableMeasurementServlet(), "/" + PATH_PREFIX + "/" + URL_PATH_MEASUREMENT + "/disable");
			addServlet(server, new GetDataServlet(),  "/" + PATH_PREFIX + "/" +URL_PATH_MEASUREMENT + "/getdata");
			addServlet(server, new CurrentTimeServlet(),  "/" + PATH_PREFIX + "/" +URL_PATH_MEASUREMENT + "/currentTime");
			addServlet(server, new MeasureOverheadServlet(),  "/" + PATH_PREFIX + "/" +URL_PATH_MEASUREMENT + "/measureOverhead");
			addServlet(server, new MeasurementStateServlet(),  "/" + PATH_PREFIX + "/" +URL_PATH_MEASUREMENT + "/monitoringState");
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

	/**
	 * Parses the agent arguments.
	 * 
	 * @param agentArgs
	 *            arguments as string
	 */
	private static void parseArgs(final String agentArgs) {
		if (agentArgs == null) {
			return;
		}
		final File agentConfigFile = new File(agentArgs);
		if (!agentConfigFile.isFile() || !agentConfigFile.exists()) {
			return;
		}
		final Properties agentProperties = new Properties();
		try (FileReader reader = new FileReader(agentConfigFile)) {
			agentProperties.load(reader);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}

		for (final Object key : agentProperties.keySet()) {
			if (key.toString().equals(PORT_KEY)) {
				port = agentProperties.getProperty(PORT_KEY);
			} else if (key.toString().equals(DATA_COLLECTOR_KEY)) {
				collectorType = agentProperties.getProperty(DATA_COLLECTOR_KEY);
			} else if (key.toString().equals(PLUGINS_ROOT_KEY)) {
				pluginsRoot = agentProperties.getProperty(PLUGINS_ROOT_KEY);
			} else if (key.toString().equals(LOGGING_LEVEL_KEY)) {
				aimLoggingConfig.setLoggingLevel(LoggingLevel.logLevelFromName(agentProperties
						.getProperty(LOGGING_LEVEL_KEY)));
			} else if (key.toString().equals(LOGGING_TYPE_KEY)) {
				if (agentProperties.getProperty(LOGGING_TYPE_KEY).compareToIgnoreCase(
						AIMLoggingConfig.LoggingType.STDOUT.toString()) == 0) {
					aimLoggingConfig.setLoggingType(AIMLoggingConfig.LoggingType.STDOUT);
				} else if (agentProperties.getProperty(LOGGING_TYPE_KEY).compareToIgnoreCase(
						AIMLoggingConfig.LoggingType.FILE.toString()) == 0) {
					aimLoggingConfig.setLoggingType(AIMLoggingConfig.LoggingType.FILE);
				}
			} else if (key.toString().equals(LOGGING_FILE_KEY)) {
				final String fileName = agentProperties.getProperty(LOGGING_FILE_KEY);
				final File file = new File(fileName);
				aimLoggingConfig.setFileName(file.getAbsolutePath());

			} else {
				properties.put(key.toString(), agentProperties.getProperty(key.toString()));
			}
		}
	}
}
