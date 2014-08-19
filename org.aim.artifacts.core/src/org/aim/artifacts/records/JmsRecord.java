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
 * Record for messaging with JMS.
 * 
 * @author Alexander Wert
 * 
 */
public class JmsRecord extends AbstractRecord {

	/** */
	private static final long serialVersionUID = 5783868651847587429L;

	public static final String PAR_CLIENT_ID = "clientId";

	public static final String PAR_THREAD_ID = "threadId";

	public static final String PAR_MSG_CORRELATION_HASH = "messageCorrelationHash";

	public static final String PAR_STACK_TRACE = "stackTrace";

	public static final String PAR_WAS_SENT = "wasSent";

	/**
	 * Default constructor required for programmatic instantiation.
	 */
	public JmsRecord() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param threadId
	 *            id of the thread created that record
	 * @param timeStamp
	 *            timestamp of record
	 */
	public JmsRecord(long timeStamp, long threadId) {
		super(timeStamp);
		this.threadId = threadId;
	}

	@RecordValue(metric = true, name = PAR_CLIENT_ID)
	String clientId;

	@RecordValue(metric = true, name = PAR_THREAD_ID)
	long threadId;

	@RecordValue(metric = true, name = PAR_MSG_CORRELATION_HASH)
	String messageCorrelationHash;

	@RecordValue(metric = true, name = PAR_STACK_TRACE)
	String stackTrace;

	@RecordValue(metric = true, name = PAR_WAS_SENT)
	byte wasSent;

	/**
	 * 
	 * @return Sent = 1 / Receied = 0
	 */
	public byte wasSent() {
		return wasSent;
	}

	/**
	 * 
	 * @param wasSent
	 *            wasSent
	 */
	public void setWasSent(byte wasSent) {
		this.wasSent = wasSent;
	}

	/**
	 * 
	 * @return thread id
	 */
	public long getThreadId() {
		return threadId;
	}

	/**
	 * 
	 * @param threadId
	 *            thread id
	 */
	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	/**
	 * 
	 * @return client Id
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * 
	 * @param clientId
	 *            client id
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * 
	 * @return message correlation hash
	 */
	public String getMessageCorrelationHash() {
		return messageCorrelationHash;
	}

	/**
	 * 
	 * @param messageCorrelationHash
	 *            message correlation hash
	 */
	public void setMessageCorrelationHash(String messageCorrelationHash) {
		this.messageCorrelationHash = messageCorrelationHash;
	}

	/**
	 * 
	 * @return stack trace
	 */
	public String getStackTrace() {
		return stackTrace;
	}

	/**
	 * 
	 * @param stackTrace
	 *            stack trace
	 */
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}


}
