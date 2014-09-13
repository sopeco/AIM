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
public final class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private static Executor threadPool = Executors.newCachedThreadPool();

	/**
	 * Shutdown logic of the application.
	 */
	public static void exit() {
		LOGGER.debug("Exit..");
		System.exit(0);
	}

	/**
	 * This pool can be used, if a thread is needed.
	 * 
	 * @return a cached ThreadPool
	 */
	public static Executor getThreadPool() {
		return threadPool;
	}

	/**
	 * The application's starting method.
	 * 
	 * @param args
	 *            - program arguments
	 */
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

		MainView.instance().setVisible(true);
		MainView.instance().setLocationRelativeTo(null);
	}

	/**
	 * Hidden constructor to prevent instantiation of this class.
	 */
	private Main() {
	}

}
