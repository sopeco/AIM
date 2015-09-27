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
package org.aim.artifacts.records;

import org.aim.aiminterface.entities.measurements.AbstractRecord;
import org.aim.aiminterface.entities.measurements.RecordValue;

/**
 * Records for monitoring waiting times, like monitor waits.
 * 
 * @author Henning Schulz
 * 
 */
public class EventTimeStampRecord extends AbstractRecord {

	/**
	 * 
	 */
	private static final long serialVersionUID = 22887790642061789L;

	public static final String PAR_EVENT_TYPE = "eventType";

	public static final String PAR_LOCATION = "location";

	public static final String PAR_THREAD_ID = "threadId";
	
	public static final String PAR_NANO_TIMESTAMP = "eventNanoTimestamp";

	/**
	 * Default constructor required for programmatic instantiation.
	 */
	public EventTimeStampRecord() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param timestamp
	 *            timestamp of record
	 * @param eventType
	 *            type of the event
	 * @param location
	 *            location of the monitor
	 * @param eventNanoTimestamp
	 *            event timestamp in nanoseconds
	 */
	public EventTimeStampRecord(long timestamp, String eventType, String location, long eventNanoTimestamp) {
		super(timestamp);
		this.eventType = eventType;
		this.location = location;
		this.eventNanoTimestamp = eventNanoTimestamp;
	}

	@RecordValue(name = PAR_EVENT_TYPE)
	String eventType;

	@RecordValue(name = PAR_LOCATION)
	String location;

	@RecordValue(name = PAR_NANO_TIMESTAMP, metric=true)
	long eventNanoTimestamp;
	
	@RecordValue(name = PAR_THREAD_ID, metric=true)
	private
	long threadId;

	/**
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType;
	}

	/**
	 * @param eventType
	 *            the eventType to set
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the monitorClass
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the monitorClass to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the microTimestamp
	 */
	public long getEventNanoTimestamp() {
		return eventNanoTimestamp;
	}

	/**
	 * @param microTimestamp
	 *            the microTimestamp to set
	 */
	public void setEventNanoTimestamp(long microTimestamp) {
		this.eventNanoTimestamp = microTimestamp;
	}

	/**
	 * @return the threadId
	 */
	public long getThreadId() {
		return threadId;
	}

	/**
	 * @param threadId the threadId to set
	 */
	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

}
