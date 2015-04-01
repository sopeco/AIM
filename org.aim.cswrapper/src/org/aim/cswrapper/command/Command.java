package org.aim.cswrapper.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Command implements Runnable {

	private Thread thread;
	private String commandString;
	private Process process;

	public Command(String commandString) {
		this.commandString = commandString;
	}

	public void execute() throws IOException {
		process = Runtime.getRuntime().exec(commandString);

		thread = new Thread(this);
		thread.start();
		
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		process.destroy();
	}

	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
