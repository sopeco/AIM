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
 * AIM logging level.
 * 
 * @author Alexander Wert
 * 
 */
public final class LoggingLevel {
	private LoggingLevel() {
	}

	public static final int DEBUG = 0;
	public static final int INFO = 1;
	public static final int WARN = 2;
	public static final int ERROR = 3;

	/**
	 * Returns the name of the log level constant.
	 * 
	 * @param level
	 *            log level
	 * @return name of the log level
	 */
	public static String getName(int level) {
		switch (level) {
		case DEBUG:
			return "DEBUG";

		case INFO:
			return "INFO";

		case WARN:
			return "WARN";

		case ERROR:
			return "ERROR";

		default:
			return "ERROR";
		}
	}

	/**
	 * Return log level for given name.
	 * 
	 * @param level
	 *            log level name
	 * @return log level as integer
	 */
	public static int logLevelFromName(String level) {

		if (level.compareToIgnoreCase("DEBUG") == 0) {
			return DEBUG;
		} else if (level.compareToIgnoreCase("INFO") == 0) {
			return INFO;
		} else if (level.compareToIgnoreCase("WARN") == 0) {
			return WARN;
		} else if (level.compareToIgnoreCase("ERROR") == 0) {
			return ERROR;
		}

		return ERROR;
	}
}
