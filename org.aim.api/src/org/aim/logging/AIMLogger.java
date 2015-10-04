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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logger interface for the AIM specific Logger.
 * 
 * @author Alexander Wert
 * 
 */
public class AIMLogger {

	private static final String PLACE_HOLDER = "{}";
	private static final String PACKAGE_SEPARATOR = ".";
	private static final String PACKAGE_SEPARATOR_REGEX = "\\" + PACKAGE_SEPARATOR;
	private final IAIMLogWriter logWriter;
	private final String className;
	private final SimpleDateFormat dateFormat;
	private final int logLevel;

	/**
	 * Constructor.
	 * 
	 * @param logWriter
	 *            log writer implementation
	 * @param logLevel
	 *            maximal logLevel
	 * @param clazz
	 *            class
	 */
	protected AIMLogger(final IAIMLogWriter logWriter, final int logLevel, final Class<?> clazz) {
		this.logWriter = logWriter;
		this.logLevel = logLevel;
		className = shortenClassName(clazz.getName());
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	public boolean isLogLevelEnabled(final int logLevel) {
		return this.logLevel <= logLevel;
	}

	/**
	 * Log debug message and arguments. Arguments replace the '{}' Strings in
	 * the message.
	 * 
	 * @param message
	 *            message
	 * @param arguments
	 *            additional arguments
	 */
	public void debug(final String message, final Object... arguments) {
		if (logLevel <= LoggingLevel.DEBUG) {
			logWriter.writeLogMessage(buildMessage("DEBUG", message, arguments));
		}
	}

	/**
	 * Log info message and arguments. Arguments replace the '{}' Strings in the
	 * message.
	 * 
	 * @param message
	 *            message
	 * @param arguments
	 *            additional arguments
	 */
	public void info(final String message, final Object... arguments) {
		if (logLevel <= LoggingLevel.INFO) {
			logWriter.writeLogMessage(buildMessage("INFO", message, arguments));
		}
	}

	/**
	 * Log warn message and arguments. Arguments replace the '{}' Strings in the
	 * message.
	 * 
	 * @param message
	 *            message
	 * @param arguments
	 *            additional arguments
	 */
	public void warn(final String message, final Object... arguments) {
		if (logLevel <= LoggingLevel.WARN) {
			logWriter.writeLogMessage(buildMessage("WARN", message, arguments));
		}
	}

	/**
	 * Log error message and arguments. Arguments replace the '{}' Strings in
	 * the message.
	 * 
	 * @param message
	 *            message
	 * @param arguments
	 *            additional arguments
	 */
	public void error(final String message, final Object... arguments) {
		if (logLevel <= LoggingLevel.ERROR) {
			logWriter.writeLogMessage(buildMessage("ERROR", message, arguments));
		}
	}

	private String buildMessage(final String logLevel, final String message, final Object... arguments) {
		final StringBuilder builder = new StringBuilder(logLevel);
		builder.append(" ");
		builder.append(dateFormat.format(new Date()));
		builder.append(" ");
		builder.append(className);
		builder.append(": ");
		int lastStopIx = 0;
		int ix = message.indexOf(PLACE_HOLDER, lastStopIx);
		int i = 0;
		while (ix >= 0) {
			builder.append(message.substring(lastStopIx, ix));
			builder.append(arguments[i].toString());
			lastStopIx = ix + PLACE_HOLDER.length();
			if (lastStopIx < message.length()) {
				ix = message.indexOf(PLACE_HOLDER, lastStopIx);
				i++;
			} else {
				ix = -1;
			}
		}
		builder.append(message.substring(lastStopIx));
		return builder.toString();
	}

	private String shortenClassName(final String name) {
		final String[] parts = name.split(PACKAGE_SEPARATOR_REGEX);
		if (parts.length == 0) {
			return name;
		}
		if (parts.length == 1 && name.endsWith(PACKAGE_SEPARATOR)) {
			return parts[0].charAt(0) + PACKAGE_SEPARATOR;
		}
		final StringBuilder sb = new StringBuilder();
		final int countPackages = parts.length - 1;
		for (int i = 0; i < countPackages; ++i) {
			sb.append(parts[i].charAt(0) + PACKAGE_SEPARATOR);
		}
		sb.append(parts[countPackages]);
		return sb.toString();
	}
}
