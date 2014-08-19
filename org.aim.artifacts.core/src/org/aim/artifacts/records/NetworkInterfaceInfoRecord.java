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
 * Information anout network interfaces.
 * 
 * @author Alexander Wert
 * 
 */
public class NetworkInterfaceInfoRecord extends AbstractRecord {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6138851058294275671L;

	/**
	 * 
	 */

	public static final String PAR_NETWORK_INTERFACE = "networkInterface";

	public static final String PAR_INTERFACE_SPEED = "speed";

	@RecordValue(metric = false, name = PAR_NETWORK_INTERFACE)
	String networkInterface;

	@RecordValue(metric = true, name = PAR_INTERFACE_SPEED)
	long speed;

	/**
	 * Default constructor required for programmatic instantiation.
	 */
	public NetworkInterfaceInfoRecord() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param networkInterface
	 *            id of the network interface
	 * @param timeStamp
	 *            timestamp
	 * @param speed
	 *            speed of the underlying network interface in bits/s
	 */
	public NetworkInterfaceInfoRecord(long timeStamp, String networkInterface, long speed) {
		super(timeStamp);
		this.networkInterface = networkInterface;
		this.setSpeed(speed);
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

	/**
	 * @return the speed [bits/s]
	 */
	public long getSpeed() {
		return speed;
	}

	/**
	 * @param speed
	 *            the speed to set [bits/s]
	 */
	public void setSpeed(long speed) {
		this.speed = speed;
	}
}
