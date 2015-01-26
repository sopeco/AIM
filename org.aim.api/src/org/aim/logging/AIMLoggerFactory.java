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
package org.aim.logging;

/**
 * AIM specific logging factory.
 * 
 * @author Alexander Wert
 * 
 */
public final class AIMLoggerFactory {
	private AIMLoggerFactory() {

	}

	private static AIMLoggingConfig loggingConfig;
	private static IAIMLogWriter logWriter;

	/**
	 * Initializes the log writer.
	 * 
	 * @param config
	 *            logging configuration
	 */
	public static void initialize(AIMLoggingConfig config) {
		loggingConfig = config;
		switch (loggingConfig.getLoggingType()) {
		case FILE:
			logWriter = new FileLogWriter(loggingConfig.getFileName());
			break;
		case STDOUT:
			logWriter = new ConsoleLogWriter();
			break;
		default:
			throw new IllegalArgumentException("Invalid Logging Type");

		}
	}

	/**
	 * Return the logger for the given class.
	 * 
	 * @param clazz
	 *            class to be logged
	 * @return logger
	 */
	public static synchronized AIMLogger getLogger(Class<?> clazz) {
		if (loggingConfig == null) {
			loggingConfig = new AIMLoggingConfig();
			initialize(loggingConfig);
		}
		return new AIMLogger(logWriter, loggingConfig.getLoggingLevel(), clazz);
	}

}
