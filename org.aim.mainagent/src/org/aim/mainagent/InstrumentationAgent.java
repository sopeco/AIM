/**
 * Copyright 2014 SAP AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aim.mainagent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Properties;

import org.aim.api.measurement.collector.AbstractDataSource;
import org.aim.api.measurement.collector.CollectorFactory;
import org.aim.artifacts.instrumentation.InstrumentationClient;
import org.aim.artifacts.measurement.collector.MemoryDataSource;
import org.aim.mainagent.instrumentor.JInstrumentation;
import org.aim.mainagent.service.CurrentTimeServlet;
import org.aim.mainagent.service.DisableMeasurementServlet;
import org.aim.mainagent.service.EnableMeasurementServlet;
import org.aim.mainagent.service.GetDataServlet;
import org.aim.mainagent.service.GetStateServlet;
import org.aim.mainagent.service.GetSupportedExtensionsServlet;
import org.aim.mainagent.service.InstrumentServlet;
import org.aim.mainagent.service.MeasureOverheadServlet;
import org.aim.mainagent.service.Service;
import org.aim.mainagent.service.TestConnectionServlet;
import org.aim.mainagent.service.UninstrumentServlet;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.lpe.common.config.GlobalConfiguration;
import org.lpe.common.extension.ExtensionRegistry;
import org.lpe.common.util.system.LpeSystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link InstrumentationAgent} is a java agent which provides the service
 * to dynamically instrument an arbitrary java application. This agent either
 * can be started with an java application as an "-javaagent:" argument, or
 * loaded into a JVM at runtime.
 * 
 * @author Alexander Wert
 * 
 */
public final class InstrumentationAgent {
	private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentationAgent.class);

	private static final String PORT_KEY = "port=";
	private static final String DATA_COLLECTOR_KEY = "collector=";
	private static final String PLUGINS_ROOT_KEY = "pluginsRootDir=";
	private static final String DEFAULT_PLUGINS_FOLDER = "plugins";

	public static final String URL_PATH_INSTRUMENTATION = InstrumentationClient.URL_PATH_INSTRUMENTATION;
	public static final String URL_PATH_MEASUREMENT = InstrumentationClient.URL_PATH_MEASUREMENT;
	public static final String PATH_PREFIX = InstrumentationClient.PATH_PREFIX;

	private static final Properties properties = new Properties();

	private static String port = "8888";
	private static String pluginsRoot;
	private static String collectorType = MemoryDataSource.class.getName();

	/**
	 * Private Constructor due to utility class.
	 */
	private InstrumentationAgent() {

	}

	/**
	 * Main method for the agent. Initializes the agent. This method is called,
	 * when the agent is started with a java application as argument.
	 * 
	 * @param agentArgs
	 *            arguments for the agent. Valid arguments: port=<PORT> (port
	 *            where to bind the agent REST service), prefix=<PREFIX> (prefix
	 *            of the agent REST service), collector=<COLLECTORTYPE> (type of
	 *            the data collector (default: file)), any <KEY>=<VALUE> pair
	 *            which is passed as a Property entry
	 * @param inst
	 *            Java instrumentation instance
	 */
	public static void premain(String agentArgs, Instrumentation inst) {
		agentmain(agentArgs, inst);
	}

	/**
	 * Main method for the agent. Initializes the agent. This method is called,
	 * when the agent is loaded into a JVM at runtime.
	 * 
	 * @param agentArgs
	 *            arguments for the agent. Valid arguments: port=<PORT> (port
	 *            where to bind the agent REST service), prefix=<PREFIX> (prefix
	 *            of the agent REST service), collector=<COLLECTORTYPE> (type of
	 *            the data collector (default: file)), logging=<TRUE>/<FALSE>
	 *            (enable/disable logging), any <KEY>=<VALUE> pair which is
	 *            passed as a Property entry
	 * @param inst
	 *            Java instrumentation instance
	 */
	public static void agentmain(String agentArgs, Instrumentation inst) {
		boolean cAgentInitializedSuccessfully = CEventAgentAdapter.initialize();
		if (!cAgentInitializedSuccessfully) {
			LOGGER.warn("The C event agent could not be initialized and will not be used therefore.");
			// TODO: handle this case
		}

		try {
			if (!inst.isRedefineClassesSupported()) {
				throw new IllegalStateException(
						"Redefining classes not supported, InstrumentationAgent cannot work properly!");
			}
			parseArgs(agentArgs);
			initializeGlobalConfig();
			LpeSystemUtils.loadNativeLibraries();
			JInstrumentation.getInstance().setjInstrumentation(inst);
			initDataCollector();
			startServer();
		} catch (Exception e) {
			LOGGER.error("Agent ERROR: {}", e);
		}
	}

	private static void initializeGlobalConfig() {
		if (pluginsRoot == null) {
			pluginsRoot = System.getProperty("user.dir");
		}

		Properties coreProperties = new Properties();
		coreProperties.setProperty(ExtensionRegistry.APP_ROOT_DIR_PROPERTY_KEY, pluginsRoot);
		coreProperties.setProperty(ExtensionRegistry.PLUGINS_FOLDER_PROPERTY_KEY, DEFAULT_PLUGINS_FOLDER);
		GlobalConfiguration.initialize(coreProperties);
	}

	private static void startServer() {
		HttpServer server = HttpServer.createSimpleServer("", Integer.parseInt(port));
		try {

			addServlet(server, new TestConnectionServlet(), "testConnection");

			addServlet(server, new InstrumentServlet(), URL_PATH_INSTRUMENTATION + "/instrument");
			addServlet(server, new UninstrumentServlet(), URL_PATH_INSTRUMENTATION + "/uninstrument");
			addServlet(server, new GetStateServlet(), URL_PATH_INSTRUMENTATION + "/getState");
			addServlet(server, new GetSupportedExtensionsServlet(), URL_PATH_INSTRUMENTATION
					+ "/getSupportedExtensions");

			addServlet(server, new EnableMeasurementServlet(), URL_PATH_MEASUREMENT + "/enable");
			addServlet(server, new DisableMeasurementServlet(), URL_PATH_MEASUREMENT + "/disable");
			addServlet(server, new GetDataServlet(), URL_PATH_MEASUREMENT + "/getdata");
			addServlet(server, new CurrentTimeServlet(), URL_PATH_MEASUREMENT + "/currentTime");
			addServlet(server, new MeasureOverheadServlet(), URL_PATH_MEASUREMENT + "/measureOverhead");

			server.start();
			LOGGER.info("Started Instrumentation Agent Server: {}.", getAddress());

		} catch (IllegalArgumentException iae) {
			LOGGER.error("Illegal Argument Exception happend in main method of ServerLauncher: {}", iae.getMessage());
		} catch (IOException ioe) {
			LOGGER.error("IO Exception happend in main method of ServerLauncher: {}", ioe.getMessage());
		}

	}

	private static void addServlet(final HttpServer server, final Service service, final String path) {
		server.getServerConfiguration().addHttpHandler(new HttpHandler() {

			@Override
			public void service(Request req, Response resp) throws Exception {
				service.doService(req, resp);

			}
		}, "/" + PATH_PREFIX + "/" + path);
	}

	/**
	 * Parses the agent arguments.
	 * 
	 * @param agentArgs
	 *            arguments as string
	 */
	private static void parseArgs(String agentArgs) {
		if (agentArgs == null) {
			return;
		}
		String[] args = agentArgs.split(",");
		for (String arg : args) {
			if (arg.startsWith(PORT_KEY)) {
				port = arg.substring(PORT_KEY.length());
			} else if (arg.startsWith(DATA_COLLECTOR_KEY)) {
				collectorType = arg.substring(DATA_COLLECTOR_KEY.length());
			} else if (arg.startsWith(PLUGINS_ROOT_KEY)) {
				pluginsRoot = arg.substring(PLUGINS_ROOT_KEY.length());
			} else if (arg.contains("=")) {
				String[] keyValuePair = arg.split("=");
				if (keyValuePair.length == 2) {
					properties.put(keyValuePair[0], keyValuePair[1]);
				}

			}
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
	 * Initializes the data collector.
	 */
	private static void initDataCollector() {
		AbstractDataSource dataSource = CollectorFactory.createDataSource(collectorType, properties);
		AbstractDataSource.setDefaultDataSource(dataSource);
	}

	/**
	 * @return Returns the collector type of the agent.
	 */
	public static String getCollectorType() {
		return collectorType;
	}

}
