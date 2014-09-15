package org.aim.logging;

/**
 * Console Log writer.
 * 
 * @author Alexander Wert
 * 
 */
public class ConsoleLogWriter implements IAIMLogWriter {

	@Override
	public void writeLogMessage(String message) {
		System.out.println(message);

	}

}
