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
