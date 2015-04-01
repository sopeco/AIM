package org.aim.cswrapper;

import java.io.IOException;

import org.aim.cswrapper.command.CompileAndPublishCommand;
import org.aim.cswrapper.command.CompileCommand;

public final class Utils {
	private Utils() {
	}

	public static void compileAndPublish() {
		CompileAndPublishCommand cmd = new CompileAndPublishCommand();
		try {
			cmd.execute();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void compile(String csProject) {
		CompileCommand cmd = new CompileCommand(csProject);
		try {
			cmd.execute();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
