package org.aim.cswrapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Configuration {

	public enum ConfigurationKey {
		AIM_CONFIG_FILE, JNBRIDGE_DIRECTORY, LOG4J_CONFIG_FILE, AIM_AGENT_JAR, IIS_EXPRESS_EXE, MSBUILD_EXE, CS_APP_PROJECT, CS_APP_PUBLISH_DESTINATION, JNBRIDGE_PROPERTIES, CS_APP_ASPECT_DESCRIPTION_FILE, IIS_EXPRESS_SITE
	}

	/**
	 * The configuration property file.
	 */
	private final static String CONFIG_FILE = "config.properties";

	/**
	 * Read configuration.
	 */
	private static Properties properties;

	private Configuration() {
	}

	/**
	 * Returns the value of the specified configuration key.
	 * 
	 * @param key
	 *            Configuration key.
	 * @return specified value
	 */
	public static String get(ConfigurationKey key) {
		if (properties == null) {
			return null;
		}
		return properties.getProperty(key.name());
	}

	/**
	 * Returns the value of the specified configuration key. If the specified
	 * key is null the method returns defaultValue.
	 * 
	 * @param key
	 *            Configuration key.
	 * @param defaultValue
	 *            Return value if the desired configuration entry is null.
	 * @return specified value
	 */
	public static String get(ConfigurationKey key, String defaultValue) {
		if (properties == null) {
			return defaultValue;
		}
		return properties.getProperty(key.name(), defaultValue);
	}

	/**
	 * Reads the configuration file.
	 */
	public static void readConfiguration() {
		properties = new Properties();
//		InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
		try {
			InputStream inputStream = new FileInputStream(CONFIG_FILE);
			properties.load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
