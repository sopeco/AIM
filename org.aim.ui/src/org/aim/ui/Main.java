package org.aim.ui;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.aim.ui.view.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Marius Oehler
 *
 */
public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private static Executor THREAD_POOL = Executors.newCachedThreadPool();

	/**
	 * Shutdown logic of the application
	 */
	public static void exit() {
		LOGGER.debug("Exit..");
		System.exit(0);
	}

	public static Executor getThreadPool() {
		return THREAD_POOL;
	}

	public static void main(String[] args) {
		LOGGER.debug("Starting AIM UserInterface");

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		MainView.SINGLETON().setVisible(true);
		MainView.SINGLETON().setLocationRelativeTo(null);
	}

	private Main() {
	}

}
