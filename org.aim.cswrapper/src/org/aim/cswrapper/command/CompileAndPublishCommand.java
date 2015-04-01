package org.aim.cswrapper.command;

import org.aim.cswrapper.Configuration;
import org.aim.cswrapper.Configuration.ConfigurationKey;

public class CompileAndPublishCommand extends Command {

	private static String buildCommandString() {

		String commandString = "\"" + Configuration.get(ConfigurationKey.MSBUILD_EXE) + "\" \""
				+ Configuration.get(ConfigurationKey.CS_APP_PROJECT) + "\" /p:VisualStudioVersion=12.0 \"/p:PublishDestination="
				+ Configuration.get(ConfigurationKey.CS_APP_PUBLISH_DESTINATION) + "\" /t:Clean,Build,PublishToFileSystem";

		return commandString;
	}

	public CompileAndPublishCommand() {
		super(buildCommandString());
	}

}
