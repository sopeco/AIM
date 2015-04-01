package org.aim.cswrapper.command;

import org.aim.cswrapper.Configuration;
import org.aim.cswrapper.Configuration.ConfigurationKey;

public class CompileCommand extends Command {

	private static String buildCommandString(String csProject) {
		String commandString = "\"" + Configuration.get(ConfigurationKey.MSBUILD_EXE) + "\" \"" + csProject
				+ "\" /p:VisualStudioVersion=12.0 /t:Clean,Build";
		return commandString;
	}

	public CompileCommand(String csProject) {
		super(buildCommandString(csProject));
	}

}
