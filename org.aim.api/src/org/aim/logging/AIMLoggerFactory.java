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
