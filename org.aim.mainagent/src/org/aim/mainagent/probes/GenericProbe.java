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
package org.aim.mainagent.probes;

import org.aim.api.instrumentation.ProbeBeforePart;
import org.aim.api.instrumentation.ProbeVariable;
import org.aim.api.measurement.collector.IDataCollector;

/**
 * Encapsulates common code for all probes.
 * @author Alexander Wert
 *
 */
public class GenericProbe {

	private static volatile long callID = 1L;

	@ProbeVariable
	public long _GenericProbe_startTime;

	@ProbeVariable
	public long _GenericProbe_callId;

	@ProbeVariable
	public IDataCollector _GenericProbe_collector;

	/**
	 * Code for common before part.
	 */
	@ProbeBeforePart
	public void commonBeforePart() {
		_GenericProbe_startTime = System.currentTimeMillis();
		_GenericProbe_callId = getNewCallID();
		_GenericProbe_collector = org.aim.api.measurement.collector.AbstractDataSource.getDefaultDataSource();
	}

	/**
	 * 
	 * @return a new call id
	 */
	public static long getNewCallID() {
		return callID++;
	}
}
