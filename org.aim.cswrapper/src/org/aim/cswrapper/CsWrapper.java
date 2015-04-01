package org.aim.cswrapper;

import org.aim.cswrapper.Configuration.ConfigurationKey;
import org.aim.mainagent.csharp.DotNetAgent;

public class CsWrapper {

	private static ServiceHandler serviceHandler;

	public static void main(String[] args) throws InterruptedException {
		Configuration.readConfiguration();
		
		IISExpressController.start(Configuration.get(ConfigurationKey.IIS_EXPRESS_SITE));

		JNBridge.getInstance().start();

		serviceHandler = new ServiceHandler();

		DotNetAgent.setServiceHandler(serviceHandler);
		DotNetAgent.start(Configuration.get(ConfigurationKey.AIM_CONFIG_FILE));
	}

}
