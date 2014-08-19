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
 * The {@link NetworkRecord} is a record class for monitoring of the network
 * transfer.
 * 
 * @author Alexander Wert
 * 
 */
public class NetworkRecord extends AbstractRecord {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3453882100988590112L;

	public static final String PAR_NETWORK_INTERFACE = "networkInterface";

	public static final String PAR_RECEIVED_BYTES = "totalReceivedBytes";
	public static final String PAR_TRANSFERRED_BYTES = "totalTransferredBytes";

	@RecordValue(metric = false, name = PAR_NETWORK_INTERFACE)
	String networkInterface;

	@RecordValue(metric = true, name = PAR_RECEIVED_BYTES)
	long totalReceivedBytes;
	@RecordValue(metric = true, name = PAR_TRANSFERRED_BYTES)
	long totalTransferredBytes;
	


	/**
	 * Default constructor required for programmatic instantiation.
	 */
	public NetworkRecord() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param networkInterface
	 *            id of the network interface
	 * @param timeStamp
	 *            timestamp
	 * @param totalReceivedBytes
	 *            total number of received bytes
	 * @param totalTransferredBytes
	 *            total number of transferred bytes
	 */
	public NetworkRecord(long timeStamp, String networkInterface, long totalReceivedBytes, long totalTransferredBytes) {
		super(timeStamp);
		this.networkInterface = networkInterface;
		this.totalReceivedBytes = totalReceivedBytes;
		this.totalTransferredBytes = totalTransferredBytes;
	}



	/**
	 * 
	 * @return total number of received bytes
	 */
	public long getTotalReceivedBytes() {
		return totalReceivedBytes;
	}

	/**
	 * Sets total number of bytes received.
	 * 
	 * @param totalReceivedBytes
	 *            number of bytes received.
	 */
	public void setTotalReceivedBytes(long totalReceivedBytes) {
		this.totalReceivedBytes = totalReceivedBytes;
	}

	/**
	 * 
	 * @return total number of transferred bytes
	 */
	public long getTotalTransferredBytes() {
		return totalTransferredBytes;
	}

	/**
	 * Sets total number of bytes transferred.
	 * 
	 * @param totalTransferredBytes
	 *            number of bytes transferred.
	 */
	public void setTotalTransferredBytes(long totalTransferredBytes) {
		this.totalTransferredBytes = totalTransferredBytes;
	}

	/**
	 * 
	 * @return id of the network interface
	 */
	public String getNetworkInterface() {
		return networkInterface;
	}

	/**
	 * Sets the id of the network interface.
	 * 
	 * @param networkInterface
	 *            id of observed network interface
	 */
	public void setNetworkInterface(String networkInterface) {
		this.networkInterface = networkInterface;
	}

}
