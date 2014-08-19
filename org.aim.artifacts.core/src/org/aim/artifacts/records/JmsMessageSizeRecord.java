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

import org.aim.api.measurement.AbstractRecord;
import org.aim.api.measurement.RecordValue;

/**
 * Record for JMS message sizes.
 * 
 * @author Alexander Wert
 * 
 */
public class JmsMessageSizeRecord extends AbstractRecord {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String PAR_MSG_CORRELATION_HASH = "messageCorrelationHash";
	public static final String PAR_MSG_SIZE = "messageSize";
	public static final String PAR_MSG_BODY_SIZE = "messageBodySize";

	/**
	 * Default constructor.
	 */
	public JmsMessageSizeRecord() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param size
	 *            size of the message (in bytes)
	 * @param bodySize
	 *            size of the message body (in bytes)
	 * @param timeStamp
	 *            timestamp of record
	 * @param messageCorrelationHash
	 *            hash for message correlation
	 */
	public JmsMessageSizeRecord(long timeStamp, long size, long bodySize, String messageCorrelationHash) {
		super(timeStamp);
		this.setSize(size);
		this.setBodySize(bodySize);
		this.setMessageCorrelationHash(messageCorrelationHash);
	}

	@RecordValue(metric = true, name = PAR_MSG_SIZE)
	long size;

	@RecordValue(metric = true, name = PAR_MSG_BODY_SIZE)
	long bodySize;

	@RecordValue(metric = true, name = PAR_MSG_CORRELATION_HASH)
	String messageCorrelationHash;

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * @return the bodySize
	 */
	public long getBodySize() {
		return bodySize;
	}

	/**
	 * @param bodySize
	 *            the bodySize to set
	 */
	public void setBodySize(long bodySize) {
		this.bodySize = bodySize;
	}

	/**
	 * @return the messageCorrelationHash
	 */
	public String getMessageCorrelationHash() {
		return messageCorrelationHash;
	}

	/**
	 * @param messageCorrelationHash
	 *            the messageCorrelationHash to set
	 */
	public void setMessageCorrelationHash(String messageCorrelationHash) {
		this.messageCorrelationHash = messageCorrelationHash;
	}

}
