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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aim.mainagent.events.MonitorEventListener;

public class DummyJVMTIFacade {

	public DummyJVMTIFacade() throws NoSuchMethodException, SecurityException {
	}

	public void newMonitorWaitEvent(Thread thread, Object monitor, long timestamp) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		CEventAgentAdapter.onMonitorWait(thread, monitor, timestamp);
	}

	public void newMonitorEnteredEvent(Thread thread, Object monitor, long timestamp) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		CEventAgentAdapter.onMonitorEntered(thread, monitor, timestamp);
	}

}
