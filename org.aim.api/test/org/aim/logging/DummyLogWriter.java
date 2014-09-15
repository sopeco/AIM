package org.aim.logging;

public class DummyLogWriter implements IAIMLogWriter {

	public String message;

	@Override
	public void writeLogMessage(String message) {
		this.message = message;

	}

}
