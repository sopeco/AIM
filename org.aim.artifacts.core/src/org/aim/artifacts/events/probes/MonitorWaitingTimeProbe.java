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
package org.aim.artifacts.events.probes;

import org.aim.api.events.AbstractEventProbe;
import org.aim.api.events.IMonitorEventProbe;
import org.aim.api.instrumentation.GenericProbe;
import org.aim.api.measurement.collector.AbstractDataSource;
import org.aim.artifacts.records.EventTimeStampRecord;
import org.aim.description.probes.MeasurementProbe;
import org.aim.description.scopes.SynchronizedScope;
import org.lpe.common.extension.IExtension;

/**
 * Event probe for generating the waiting time of threads while they are waiting
 * to enter a {@code synchronized} block.
 * 
 * @author Henning Schulz
 * 
 */
public class MonitorWaitingTimeProbe extends AbstractEventProbe implements IMonitorEventProbe {

	/**
	 * Default Constructor.
	 */
	public MonitorWaitingTimeProbe() {
		super(null);
	}

	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider.
	 */
	public MonitorWaitingTimeProbe(IExtension<?> provider) {
		super(provider);
	}

	public static final MeasurementProbe<SynchronizedScope> MODEL_PROBE = new MeasurementProbe<>(MonitorWaitingTimeProbe.class.getName());

	private Object monitor;
	private long eventTimeStamp;
	private String eventType;
	private long threadId;

	@Override
	public void proceed() {
		EventTimeStampRecord record = new EventTimeStampRecord();
		StringBuilder locationBuilder = new StringBuilder();
		locationBuilder.append(monitor == null ? "null" : monitor.getClass().getName());
		if (monitor instanceof Class<?>) {
			locationBuilder.append("<");
			locationBuilder.append(((Class<?>) monitor).getName());
			locationBuilder.append(">");
		}
		locationBuilder.append("@");
		locationBuilder.append(monitor == null ? 0 : monitor.hashCode());
		record.setLocation(locationBuilder.toString());
		record.setThreadId(threadId);
		record.setEventType("monitor-" + eventType);
		record.setEventNanoTimestamp(eventTimeStamp);
		record.setCallId(GenericProbe.getNewCallID());
		record.setTimeStamp(System.currentTimeMillis());

		AbstractDataSource dataSource = org.aim.api.measurement.collector.AbstractDataSource.getDefaultDataSource();
		dataSource.newRecord(record);
	}

	@Override
	public void setThread(Thread thread) {
		if (thread == null) {
			this.threadId = -1;
		} else {
			this.threadId = thread.getId();
		}
	}

	@Override
	public void setMonitor(Object monitor) {
		this.monitor = monitor;
	}

	@Override
	public void setEventType(String type) {
		this.eventType = type;
	}

	@Override
	public void setEventTimeStamp(long eventTimeStamp) {
		this.eventTimeStamp = eventTimeStamp;
	}

}
