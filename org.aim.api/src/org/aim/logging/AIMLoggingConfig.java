package org.aim.logging;

/**
 * Configuration of AIM Logging.
 * 
 * @author Alexander Wert
 * 
 */
public class AIMLoggingConfig {

	/**
	 * Logging type.
	 * 
	 * @author Alexander Wert
	 * 
	 */
	public enum LoggingType {
		STDOUT, FILE
	}

	private LoggingType loggingType = LoggingType.STDOUT;
	private String fileName = "aim.log";
	private int loggingLevel = LoggingLevel.ERROR;

	/**
	 * @return the loggingType
	 */
	public LoggingType getLoggingType() {
		return loggingType;
	}

	/**
	 * @param loggingType
	 *            the loggingType to set
	 */
	public void setLoggingType(LoggingType loggingType) {
		this.loggingType = loggingType;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the loggingLevel
	 */
	public int getLoggingLevel() {
		return loggingLevel;
	}

	/**
	 * @param loggingLevel
	 *            the loggingLevel to set
	 */
	public void setLoggingLevel(int loggingLevel) {
		this.loggingLevel = loggingLevel;
	}

}
