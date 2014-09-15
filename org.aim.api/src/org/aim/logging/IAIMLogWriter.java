package org.aim.logging;

/**
 * Interface for LogWriter.
 * 
 * @author Alexander Wert
 * 
 */
public interface IAIMLogWriter {
	/**
	 * Writes the log message to the log destination.
	 * 
	 * @param message
	 *            log message
	 */
	void writeLogMessage(String message);
}
