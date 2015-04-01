package org.aim.cswrapper;

import org.aim.cswrapper.Configuration.ConfigurationKey;

import com.jnbridge.jnbcore.JNBMain;

public class JNBridge extends Thread {

	private static JNBridge instance;

	public static JNBridge getInstance() {
		if (instance == null) {
			instance = new JNBridge();
		}
		return instance;
	}

	private JNBridge() {
	}

	@Override
	public void run() {
		// required dependencies "bcel-5.1-jnbridge.jar" and "jnbcore.jar" from JBBridge
		JNBMain.main(new String[] { "/props", Configuration.get(ConfigurationKey.JNBRIDGE_PROPERTIES) });
	}
}
