package org.aim.logging;

import junit.framework.Assert;

import org.aim.logging.AIMLoggingConfig.LoggingType;
import org.junit.Test;

public class AIMLoggingTest {
	@Test
	public void testAIMLoggerDebugLevel() {
		DummyLogWriter logWriter = new DummyLogWriter();
		AIMLogger logger = new AIMLogger(logWriter, LoggingLevel.DEBUG, AIMLoggingTest.class);
		Assert.assertNull(logWriter.message);
		String message_1 = "hello";
		logger.debug(message_1);
		Assert.assertNotNull(logWriter.message);
		Assert.assertTrue(logWriter.message.startsWith("DEBUG"));
		Assert.assertTrue(logWriter.message.contains("AIMLoggingTest:"));
		Assert.assertTrue(logWriter.message.contains(message_1));
		

		logWriter.message = null;

		String message_2 = "anotherText";
		logger.info(message_2);
		Assert.assertNotNull(logWriter.message);
		Assert.assertTrue(logWriter.message.startsWith("INFO"));
		Assert.assertTrue(logWriter.message.contains("AIMLoggingTest:"));
		Assert.assertTrue(logWriter.message.contains(message_2));
		Assert.assertFalse(logWriter.message.contains(message_1));

		logWriter.message = null;

		String message_3 = "myText";
		logger.warn(message_3);
		Assert.assertNotNull(logWriter.message);
		Assert.assertTrue(logWriter.message.startsWith("WARN"));
		Assert.assertTrue(logWriter.message.contains("AIMLoggingTest:"));
		Assert.assertTrue(logWriter.message.contains(message_3));
		Assert.assertFalse(logWriter.message.contains(message_2));

		logWriter.message = null;

		String message_4 = "ownText";
		logger.error(message_4);
		Assert.assertNotNull(logWriter.message);
		Assert.assertTrue(logWriter.message.startsWith("ERROR"));
		Assert.assertTrue(logWriter.message.contains("AIMLoggingTest:"));
		Assert.assertTrue(logWriter.message.contains(message_4));
		Assert.assertFalse(logWriter.message.contains(message_3));
	}
	
	@Test
	public void testAIMLoggerInfoLevel() {
		DummyLogWriter logWriter = new DummyLogWriter();
		AIMLogger logger = new AIMLogger(logWriter, LoggingLevel.INFO, AIMLoggingTest.class);
		Assert.assertNull(logWriter.message);
		String message_1 = "hallo";
		logger.debug(message_1);
		Assert.assertNull(logWriter.message);
		

		logWriter.message = null;

		String message_2 = "anotherText";
		logger.info(message_2);
		Assert.assertNotNull(logWriter.message);
		Assert.assertTrue(logWriter.message.startsWith("INFO"));
		Assert.assertTrue(logWriter.message.contains("AIMLoggingTest:"));
		Assert.assertTrue(logWriter.message.contains(message_2));
		Assert.assertFalse(logWriter.message.contains(message_1));

		logWriter.message = null;

		String message_3 = "myText";
		logger.warn(message_3);
		Assert.assertNotNull(logWriter.message);
		Assert.assertTrue(logWriter.message.startsWith("WARN"));
		Assert.assertTrue(logWriter.message.contains("AIMLoggingTest:"));
		Assert.assertTrue(logWriter.message.contains(message_3));
		Assert.assertFalse(logWriter.message.contains(message_2));

		logWriter.message = null;

		String message_4 = "ownText";
		logger.error(message_4);
		Assert.assertNotNull(logWriter.message);
		Assert.assertTrue(logWriter.message.startsWith("ERROR"));
		Assert.assertTrue(logWriter.message.contains("AIMLoggingTest:"));
		Assert.assertTrue(logWriter.message.contains(message_4));
		Assert.assertFalse(logWriter.message.contains(message_3));
	}
	
	@Test
	public void testAIMLoggerWarnLevel() {
		DummyLogWriter logWriter = new DummyLogWriter();
		AIMLogger logger = new AIMLogger(logWriter, LoggingLevel.WARN, AIMLoggingTest.class);
		Assert.assertNull(logWriter.message);
		String message_1 = "hallo";
		logger.debug(message_1);
		Assert.assertNull(logWriter.message);
		

		logWriter.message = null;

		String message_2 = "anotherText";
		logger.info(message_2);
		Assert.assertNull(logWriter.message);
		
		logWriter.message = null;

		String message_3 = "myText";
		logger.warn(message_3);
		Assert.assertNotNull(logWriter.message);
		Assert.assertTrue(logWriter.message.startsWith("WARN"));
		Assert.assertTrue(logWriter.message.contains("AIMLoggingTest:"));
		Assert.assertTrue(logWriter.message.contains(message_3));
		Assert.assertFalse(logWriter.message.contains(message_2));

		logWriter.message = null;

		String message_4 = "ownText";
		logger.error(message_4);
		Assert.assertNotNull(logWriter.message);
		Assert.assertTrue(logWriter.message.startsWith("ERROR"));
		Assert.assertTrue(logWriter.message.contains("AIMLoggingTest:"));
		Assert.assertTrue(logWriter.message.contains(message_4));
		Assert.assertFalse(logWriter.message.contains(message_3));
	}
	
	@Test
	public void testAIMLoggerErrorLevel() {
		DummyLogWriter logWriter = new DummyLogWriter();
		AIMLogger logger = new AIMLogger(logWriter, LoggingLevel.ERROR, AIMLoggingTest.class);
		Assert.assertNull(logWriter.message);
		String message_1 = "hallo";
		logger.debug(message_1);
		Assert.assertNull(logWriter.message);
		

		logWriter.message = null;

		String message_2 = "anotherText";
		logger.info(message_2);
		Assert.assertNull(logWriter.message);
		
		logWriter.message = null;

		String message_3 = "myText";
		logger.warn(message_3);
		Assert.assertNull(logWriter.message);

		logWriter.message = null;

		String message_4 = "ownText";
		logger.error(message_4);
		Assert.assertNotNull(logWriter.message);
		Assert.assertTrue(logWriter.message.startsWith("ERROR"));
		Assert.assertTrue(logWriter.message.contains("AIMLoggingTest:"));
		Assert.assertTrue(logWriter.message.contains(message_4));
		Assert.assertFalse(logWriter.message.contains(message_3));
	}
	
	@Test
	public void testAIMLoggerArguments() {
		DummyLogWriter logWriter = new DummyLogWriter();
		AIMLogger logger = new AIMLogger(logWriter, LoggingLevel.DEBUG, AIMLoggingTest.class);
		Assert.assertNull(logWriter.message);
		String message_1 = "hallo";
		Integer i = 5777;
		logger.debug(message_1, String.class, "ABCDEFG", i);
		Assert.assertNotNull(logWriter.message);
		Assert.assertTrue(logWriter.message.startsWith("DEBUG"));
		Assert.assertTrue(logWriter.message.contains("AIMLoggingTest:"));
		Assert.assertTrue(logWriter.message.contains(message_1));
		Assert.assertFalse(logWriter.message.contains("java.lang.String"));
		Assert.assertFalse(logWriter.message.contains("ABCDEFG"));
		Assert.assertFalse(logWriter.message.contains("5777"));
		
		logWriter.message = null;
		
		String message_2 = "hello {} how {} are {} you";
		logger.debug(message_2, String.class, "ABCDEFG", i);
		Assert.assertNotNull(logWriter.message);
		Assert.assertTrue(logWriter.message.startsWith("DEBUG"));
		Assert.assertTrue(logWriter.message.contains("AIMLoggingTest:"));
		Assert.assertTrue(logWriter.message.contains("hello"));
		Assert.assertTrue(logWriter.message.contains("how"));
		Assert.assertTrue(logWriter.message.contains("are"));
		Assert.assertTrue(logWriter.message.contains("you"));
		Assert.assertTrue(logWriter.message.contains("java.lang.String"));
		Assert.assertTrue(logWriter.message.contains("ABCDEFG"));
		Assert.assertTrue(logWriter.message.contains("5777"));
	}
	
	@Test
	public void testAIMDefaultLogger() {
		AIMLogger logger = AIMLoggerFactory.getLogger(AIMLoggingTest.class);
		logger.info("Hello {}", 1);
		logger.debug("Hello {}", 2);
		logger.warn("Hello {}", 3);
		logger.error("Hello {}", 4);
	}
	
	@Test
	public void testAIMConcoleLogger() {
		AIMLoggingConfig config = new AIMLoggingConfig();
		config.setLoggingType(LoggingType.STDOUT);
		config.setLoggingLevel(LoggingLevel.INFO);
		AIMLoggerFactory.initialize(config);
		AIMLogger logger = AIMLoggerFactory.getLogger(AIMLoggingTest.class);
		logger.info("Hello {}", 1);
		logger.debug("Hello {}", 2);
		logger.warn("Hello {}", 3);
		logger.error("Hello {}", 4);
	}
}
