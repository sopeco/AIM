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
package org.lpe.common.resourcemonitoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.aim.api.measurement.collector.AbstractDataSource;
import org.aim.api.measurement.collector.CollectorFactory;
import org.aim.artifacts.measurement.collector.MemoryDataSource;
import org.lpe.common.util.system.LpeSystemUtils;
import org.lpe.common.util.web.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of the System Monitoring Utility. Starts a grizzly server and
 * initializes the Jersey application.
 * 
 * @author Alexander Wert
 * 
 */
public final class ServerLauncher {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerLauncher.class);
	public static final String PREFIX = "sampler";
	private static final String PORT_KEY = "port=";
	private static final String DATA_COLLECTOR_KEY = "collector=";
	private static String port = "8889";
	private static String collectorType = MemoryDataSource.class.getName();
	private static final Properties properties = new Properties();
	

	private ServerLauncher() {
	}

	/**
	 * Opens up a server on the localhost IP address and configured port of the
	 * underlying system.
	 * 
	 * @param args
	 *            not used.
	 */
	public static void main(String[] args) {
		if (args == null || args.length < 1) {
			LOGGER.error("Resource Monitoring Launcher requires at least one argument:");
			LOGGER.error("1st argument: start / shutdown");
			LOGGER.error("[opt] port=<PORT>");
			LOGGER.error("[opt] collector=<COLLECTOR_CLASS_NAME>");
			LOGGER.error("[opt] org.aim.fileDataSource.sinkDirectory=<DIRECTORY>");
			System.exit(0);
		}

		parseArgs(args);
		if (args[0].equalsIgnoreCase("start")) {
			LpeSystemUtils.loadNativeLibraries();

			List<String> servicePackages = new ArrayList<>();
			servicePackages.add("org.lpe.common.resourcemonitoring.service");
			initDataCollector();
			WebServer.getInstance().start(Integer.parseInt(port), PREFIX, servicePackages);
		} else if (args[0].equalsIgnoreCase("shutdown")) {
			WebServer.triggerServerShutdown(Integer.parseInt(port), PREFIX);
		} else {
			LOGGER.error("Invalid value for 1st argument! Valid values are: start / shutdown");
		}

	}

	/**
	 * Parses the agent arguments.
	 * 
	 * @param agentArgs
	 *            arguments as string
	 */
	private static void parseArgs(String [] agentArgs) {
		if (agentArgs == null) {
			return;
		}
		for (String arg : agentArgs) {
			if (arg.startsWith(PORT_KEY)) {
				port = arg.substring(PORT_KEY.length());
			} else if (arg.startsWith(DATA_COLLECTOR_KEY)) {
				collectorType = arg.substring(DATA_COLLECTOR_KEY.length());
			} else if (arg.contains("=")) {
				String[] keyValuePair = arg.split("=");
				if (keyValuePair.length == 2) {
					properties.put(keyValuePair[0], keyValuePair[1]);
				}

			}
		}
	}
	
	/**
	 * Initializes the data collector.
	 */
	private static void initDataCollector() {
		AbstractDataSource dataSource = CollectorFactory.createDataSource(collectorType, properties);
		AbstractDataSource.setDefaultDataSource(dataSource);
	}

}
