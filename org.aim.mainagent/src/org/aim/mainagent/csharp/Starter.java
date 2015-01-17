package org.aim.mainagent.csharp;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.aim.api.exceptions.MeasurementException;
import org.aim.api.measurement.collector.AbstractDataSource;
import org.aim.api.measurement.collector.IDataCollector;
import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.aim.logging.AIMLoggingConfig;
import org.aim.mainagent.CEventAgentAdapter;
import org.aim.mainagent.InstrumentationAgent;
import org.aim.mainagent.instrumentor.JInstrumentation;
import org.aim.mainagent.sampling.Sampling;
import org.aim.mainagent.service.MeasurementStateServlet;
import org.lpe.common.util.system.LpeSystemUtils;

public final class Starter {

	private static AIMLogger logger;

	private static boolean initialized = false;

	public static void main(String[] args) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException,
			NoSuchFieldException, MeasurementException {
		System.out.println("Test");

		startAIM();
	}

	private static AIMLogger getLogger() {
		if (logger == null) {
			logger = AIMLoggerFactory.getLogger(Starter.class);
		}
		return logger;
	}

	/**
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws NoSuchFieldException
	 * @throws MeasurementException
	 * @throws IOException
	 * 
	 */
	public static void startAIM() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException,
			MeasurementException {
		if (initialized) {
			System.out.println("Aim already started");
			return;
		}
		initialized = true;

		System.out.println("Start AIM");

		// InstrumentationAgent.premain("F:\\Dropbox\\HiWi\\aim.config", null);

		Constructor<InstrumentationAgent> cnstr = (Constructor<InstrumentationAgent>) InstrumentationAgent.class
				.getDeclaredConstructors()[0];
		cnstr.setAccessible(true);

		InstrumentationAgent agent = cnstr.newInstance();

		// Emulate agentmain(..)

		invokeMethod(agent, "parseArgs", "F:\\Dropbox\\HiWi\\aim.config");

		AIMLoggingConfig aimLoggingConfig = (AIMLoggingConfig) getField(agent, "aimLoggingConfig");
		AIMLoggerFactory.initialize(aimLoggingConfig);

		boolean cAgentInitializedSuccessfully = CEventAgentAdapter.initialize();
		if (!cAgentInitializedSuccessfully) {
			getLogger().warn("The C event agent could not be initialized and will not be used therefore.");
		}

		invokeMethod(agent, "initializeGlobalConfig");

		LpeSystemUtils.loadNativeLibraries();

		// JInstrumentation.getInstance().setjInstrumentation(inst);
		invokeMethod(agent, "initDataCollector");
		invokeMethod(agent, "startServer");

		// Field aimLoggingConfigField =
		// InstrumentationAgent.class.getDeclaredField("aimLoggingConfig");
		// aimLoggingConfigField.setAccessible(true);
		// AIMLoggingConfig aimLoggingConfig = (AIMLoggingConfig)
		// aimLoggingConfigField.get(agent);

		// Field propertiesField =
		// InstrumentationAgent.class.getDeclaredField("properties");
		// propertiesField.setAccessible(true);
		// Properties properties = (Properties) propertiesField.get(agent);
		// Properties properties = (Properties) getField(agent, "properties");

		// AbstractDataSource defaultDataSource =
		// AbstractDataSource.getDefaultDataSource();

		// System.out.println(">> Enable measurement");
		// IDataCollector collector = AbstractDataSource.getDefaultDataSource();
		//
		// collector.enable();
		// Sampling.getInstance().start();
		// MeasurementStateServlet.setMeasurementState(true);

		System.out.println();
	}

	private static Object getField(Object obj, String fieldName) throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		Field propertiesField = obj.getClass().getDeclaredField(fieldName);
		propertiesField.setAccessible(true);
		return propertiesField.get(obj);
	}

	private static void invokeMethod(Object obj, String name, Object... args) throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?>[] classes = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			classes[i] = args[i].getClass();
		}
		Method initDataCollector = InstrumentationAgent.class.getDeclaredMethod(name, classes);
		initDataCollector.setAccessible(true);
		initDataCollector.invoke(obj, args);
	}

	private void i() {
		try {
			// parseArgs(agentArgs);
			// AIMLoggerFactory.initialize(aimLoggingConfig);
			//
			// boolean cAgentInitializedSuccessfully =
			// CEventAgentAdapter.initialize();
			// if (!cAgentInitializedSuccessfully) {
			// getLogger().warn("The C event agent could not be initialized and will not be used therefore.");
			// // TODO: handle this case
			// }

			// if (!inst.isRedefineClassesSupported()) {
			// throw new IllegalStateException(
			// "Redefining classes not supported, InstrumentationAgent cannot work properly!");
			// }

			// initializeGlobalConfig();
			// LpeSystemUtils.loadNativeLibraries();
			// JInstrumentation.getInstance().setjInstrumentation(inst);
			// initDataCollector();
			// startServer();
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().error("Agent ERROR: {}", e);
		}
	}

}
