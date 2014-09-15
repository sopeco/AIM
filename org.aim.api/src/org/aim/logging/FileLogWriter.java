package org.aim.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * String File log writer.
 * 
 * @author Alexander Wert
 * 
 */
public class FileLogWriter implements IAIMLogWriter {
	private String fileName;

	/**
	 * Constructor.
	 * 
	 * @param fileName
	 *            name fo the file where to write the log messages to.
	 */
	public FileLogWriter(String fileName) {
		this.fileName = fileName;
		File file = new File(this.fileName);
		if (!file.exists()) {
			file = file.getAbsoluteFile();
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public synchronized void writeLogMessage(String message) {
		try (BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true))) {
			out.append(message);
			out.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
