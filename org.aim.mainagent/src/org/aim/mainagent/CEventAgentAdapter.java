/**
 * Copyright 2014 SAP AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aim.mainagent;

import org.aim.description.restrictions.Restriction;
import org.aim.mainagent.events.MonitorEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter for the JVMTI Event Agent. To use this agent, the CEventAgent has to
 * be started with the target application.<br>
 * 
 * Before the agent can be used, the {@link CEventAgentAdapter#initialize()
 * initialize()} method has to be called.
 * 
 * @author Henning Schulz
 * 
 */
public final class CEventAgentAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CEventAgentAdapter.class);

	private static final String PACKAGE_AIM = "org.aim";
	private static final String PACKAGE_LPE_COMMON = "org.lpe.common";
	private static final String PACKAGE_JAVA = "java.";
	private static final String CLASS_NAME = "java.lang.Class";

	private static MonitorEventListener monitorListener;
	private static Restriction restriction;

	private static boolean initialized = false;
	private static boolean activated = false;

	private CEventAgentAdapter() {
	}

	/**
	 * This method is called if a thread has to wait on a monitor and monitor
	 * events are enabled.<br>
	 * 
	 * <b>This methods implementation should avoid to provoke monitor waits as
	 * they may lead to endless recursions!</b>
	 * 
	 * @param thread
	 *            Thread which has to wait
	 * @param monitor
	 *            Monitor on which {@code thread} has to wait
	 * @param waitTime
	 *            timestamp in nanoseconds
	 * 
	 * @see CEventAgentAdapter#enableMonitorEvents() enableMonitorEvents()
	 * @see CEventAgentAdapter#disableMonitorEvents() disableMonitorEvents()
	 */
	public static void onMonitorWait(Thread thread, Object monitor, long waitTime) {
		if (monitorListener == null) {
			throw new IllegalStateException("No AIMEventListener specified!");
		}

		String className = monitor == null ? "null" : monitor.getClass().getName();
		if (!restriction.isExcluded(className) || className.startsWith(CLASS_NAME)) {
			monitorListener.onMonitorWait(thread, monitor, waitTime);
		}
	}

	/**
	 * This method is called if a thread had to wait on a monitor and entered
	 * it.
	 * 
	 * <b>This methods implementation should avoid to provoke monitor waits as
	 * they may lead to endless recursions!</b>
	 * 
	 * @param thread
	 *            Thread which had to wait
	 * @param monitor
	 *            Monitor on which {@code thread} had to wait
	 * @param enteredTime
	 *            timestamp in nanoseconds
	 * 
	 * @see CEventAgentAdapter#enableMonitorEvents() enableMonitorEvents()
	 * @see CEventAgentAdapter#disableMonitorEvents() disableMonitorEvents()
	 */
	public static void onMonitorEntered(Thread thread, Object monitor, long enteredTime) {
		if (monitorListener == null) {
			throw new IllegalStateException("No AIMEventListener specified!");
		}

		String className = monitor == null ? "null" : monitor.getClass().getName();
		if (!restriction.isExcluded(className) || className.startsWith(CLASS_NAME)) {
			monitorListener.onMonitorEntered(thread, monitor, enteredTime);
		}
	}

	/**
	 * Initializes the underlying JVMTI agent.
	 * 
	 * @return {@code false}, if initialization failed, because the C agent
	 *         could not be found, or {@code true} otherwise.
	 */
	public static boolean initialize() {
		if (initialized) {
			LOGGER.warn("The C agent has already been initialized!");
		} else {
			try {
				init();
			} catch (UnsatisfiedLinkError e) {
				LOGGER.warn("The C agent could not be found!");
				return false;
			}

			initialized = true;
		}

		return true;
	}

	/**
	 * Sets the synchronizedListener.
	 * 
	 * @param listener
	 *            synchronized listener to be set
	 */
	public static void setMonitorListener(MonitorEventListener listener) {
		monitorListener = listener;
	}

	/**
	 * Sets the restriction.
	 * 
	 * @param res
	 *            Restriction to be set
	 */
	public static void setRestriction(Restriction res) {
		restriction = res;
	}

	private static native void init();

	/**
	 * Enables listening to monitor events.
	 * 
	 * @see CEventAgentAdapter#onMonitorWait(Thread, Object)
	 *      onMonitorWait(Thread, Object)
	 * @see CEventAgentAdapter#onMonitorEntered(Thread, Object)
	 *      onMonitorEntered(Thread, Object)
	 */
	public static void enableMonitorEvents() {
		if (!initialized) {
			throw new RuntimeException("The C agent has to be initialized before using it!");
		}

		if (activated) {
			LOGGER.warn("Synchronized listening has alredy been activated!");
		} else {
			activateMonitorEvents();
			activated = true;
		}
	}

	private static native void activateMonitorEvents();

	/**
	 * Disables listening to monitor events.
	 * 
	 * @see CEventAgentAdapter#onMonitorWait(Thread, Object)
	 *      onMonitorWait(Thread, Object)
	 * @see CEventAgentAdapter#onMonitorEntered(Thread, Object)
	 *      onMonitorEntered(Thread, Object)
	 */
	public static void disableMonitorEvents() {
		if (!initialized) {
			throw new RuntimeException("The C agent has to be initialized before using it!");
		}

		if (!activated) {
			LOGGER.warn("Synchronized listening has alredy been deactivated!");
		} else {
			deactivateMonitorEvents();
			monitorListener = null;
			activated = false;
		}
	}

	private static native void deactivateMonitorEvents();

	/**
	 * Prints a message without the ability of monitor waits. May be used for
	 * logging in {@link CEventAgentAdapter#onMonitorWait(Thread, Object)
	 * onMonitorWait(Thread, Object)} and
	 * {@link CEventAgentAdapter#onMonitorEntered(Thread, Object)
	 * onMonitorEntered(Thread, Object)}.
	 * 
	 * @param message
	 *            Message to be printed
	 */
	private static native void printlnNonBlocking(String message);

	/**
	 * Checks whether the C-Agent has been successfully initialized.
	 * 
	 * @return true, if the CAgent has been initialized
	 */
	public static boolean isInitialized() {
		return initialized;
	}
}
