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
package org.aim.mainagent.events;


/**
 * Event listener for synchronized events.
 * 
 * @author Alexander Wert
 * 
 */
public final class MonitorEventListener implements IEventListener<IMonitorEventProbe> {

	private static MonitorEventListener instance;

	/**
	 * 
	 * @return singleton instance
	 */
	public static MonitorEventListener getInstance() {
		if (instance == null) {
			instance = new MonitorEventListener();
		}

		return instance;
	}

	private MonitorEventListener() {
	}

	/**
	 * Event callback when a thread starts waiting for a monitor.
	 * 
	 * @param thread
	 *            thread which waits
	 * @param monitor
	 *            monitor which has been requested
	 * @param timestamp
	 *            timestamp in nanoseconds
	 */
	public void onMonitorWait(Thread thread, Object monitor, long timestamp) {
		for (Class<? extends IMonitorEventProbe> probeClass : EventProbeRegistry.getInstance()
				.getProbeClasses(getClass())) {
			try {
				IMonitorEventProbe probe = probeClass.newInstance();
				probe.setThread(thread);
				probe.setMonitor(monitor);
				probe.setEventTimeStamp(timestamp);
				probe.setEventType(IMonitorEventProbe.TYPE_WAIT_ON_MONITOR);
				probe.proceed();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Event callback when a thread gets a monitor.
	 * 
	 * @param thread
	 *            thread which waits
	 * @param monitor
	 *            monitor which has been requested
	 * @param timestamp
	 *            timestamp in nanoseconds
	 */
	public void onMonitorEntered(Thread thread, Object monitor, long timestamp) {
		for (Class<? extends IMonitorEventProbe> probeClass : EventProbeRegistry.getInstance()
				.getProbeClasses(getClass())) {
			try {
				IMonitorEventProbe probe = probeClass.newInstance();
				probe.setThread(thread);
				probe.setMonitor(monitor);
				probe.setEventTimeStamp(timestamp);
				probe.setEventType(IMonitorEventProbe.TYPE_ENTERED_MONITOR);
				probe.proceed();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}