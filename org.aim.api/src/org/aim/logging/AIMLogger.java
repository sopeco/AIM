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
	private IAIMLogWriter logWriter;
	private String className;
	private SimpleDateFormat dateFormat;
	private int logLevel;

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
	protected AIMLogger(IAIMLogWriter logWriter, int logLevel, Class<?> clazz) {
		this.logWriter = logWriter;
		this.logLevel = logLevel;
		className = shortenClassName(clazz.getName());
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
	public void debug(String message, Object... arguments) {
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
	public void info(String message, Object... arguments) {
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
	public void warn(String message, Object... arguments) {
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
	public void error(String message, Object... arguments) {
		if (logLevel <= LoggingLevel.ERROR) {
			logWriter.writeLogMessage(buildMessage("ERROR", message, arguments));
		}
	}

	private String buildMessage(String logLevel, String message, Object... arguments) {
		StringBuilder builder = new StringBuilder(logLevel);
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

	private String shortenClassName(String name) {
		String[] parts = name.split(PACKAGE_SEPARATOR_REGEX);
		if (parts.length == 0) {
			return name;
		}
		if (parts.length == 1 && name.endsWith(PACKAGE_SEPARATOR)) {
			return parts[0].charAt(0) + PACKAGE_SEPARATOR;
		}
		StringBuilder sb = new StringBuilder();
		int countPackages = parts.length - 1;
		for (int i = 0; i < countPackages; ++i) {
			sb.append(parts[i].charAt(0) + PACKAGE_SEPARATOR);
		}
		sb.append(parts[countPackages]);
		return sb.toString();
	}
}
