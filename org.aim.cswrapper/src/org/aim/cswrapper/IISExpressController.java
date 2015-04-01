package org.aim.cswrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aim.cswrapper.Configuration.ConfigurationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IISExpressController extends Thread {

	private static Logger logger = LoggerFactory.getLogger(IISExpressController.class);
	private static final String IIS_EXE = "iisexpress.exe";

	private static String siteUrl;

	private Process iisProcess;

	private IISExpressController(Process iisProcess) {
		this.iisProcess = iisProcess;
	}

	private static void sleepMs(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}

	public static String getSiteUrl() {
		return siteUrl;
	}

	private static void exec(String arguments) {
		try {
			if (isRunning()) {
				logger.debug("IISExpress is already running. Terminating running instances..");
				while (isRunning()) {
					sleepMs(1000);
					if (isRunning()) {
						kill();
						sleepMs(1000);
					}
				}
			}
			Process process = Runtime.getRuntime()
					.exec(Configuration.get(ConfigurationKey.IIS_EXPRESS_EXE) + arguments);

			new IISExpressController(process).start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static boolean isRunning() throws IOException {
		Process p = Runtime.getRuntime().exec("tasklist.exe");

		String processOutput = blockingProcessReader(p);

		return processOutput.contains(IIS_EXE);
	}

	public static String blockingProcessReader(Process process) {
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuffer buffer = new StringBuffer();
			String line;
			while ((line = input.readLine()) != null) {
				buffer.append(line);
			}
			input.close();
			return buffer.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void start(int siteId) {
		logger.debug("Starting site with id {} in IISExpress..", siteId);
		exec(" /siteId:" + siteId);
	}

	public static void start(String site) {
		if (site == null || site.isEmpty()) {
			logger.warn("Site cannot be null or empty. IISExpress has not been started.");
			return;
		}
		logger.debug("Starting site {} in IISExpress..", site);
		exec(" /site:" + site);
	}

	public static void kill() {
		try {
			logger.debug("Sending termination signal to IISExpress.");
			Process kill = Runtime.getRuntime().exec("taskkill /IM iisexpress.exe");

			String processOutput = blockingProcessReader(kill);
			int lastIndex = 0, counter = 0;
			while (lastIndex != -1) {
				lastIndex = processOutput.indexOf(IIS_EXE, lastIndex);
				if (lastIndex != -1) {
					counter++;
					lastIndex += IIS_EXE.length();
				}
			}

			logger.debug("{} termination signal sent..", counter);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(iisProcess.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("Successfully")) {

					String pattern = "Successfully registered URL \"(.*)\" for site ";
					Pattern r = Pattern.compile(pattern);

					// Now create matcher object.
					Matcher m = r.matcher(line);
					if (m.find()) {
						siteUrl = m.group(1);
						logger.debug("Registered site on URL: {}", siteUrl);
					}
				}
				logger.debug(line);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
