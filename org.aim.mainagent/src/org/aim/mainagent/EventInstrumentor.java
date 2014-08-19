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

import org.aim.api.exceptions.InstrumentationException;
import org.aim.description.InstrumentationDescription;
import org.aim.description.InstrumentationEntity;
import org.aim.description.scopes.SynchronizedScope;
import org.aim.mainagent.events.EventProbeRegistry;
import org.aim.mainagent.events.ForMonitorWaitingTimeProbe;
import org.aim.mainagent.events.MonitorEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instrumentor responsible for activating and deactivating JVM events and
 * listening on event occurance.
 * 
 * @author Alexander Wert
 * 
 */
public final class EventInstrumentor implements IInstrumentor {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventInstrumentor.class);

	private static EventInstrumentor instance;

	/**
	 * 
	 * @return singleton instance
	 */
	public static EventInstrumentor getInstance() {
		if (instance == null) {
			instance = new EventInstrumentor();
		}

		return instance;
	}

	private EventInstrumentor() {
	}

	@Override
	public void instrument(InstrumentationDescription descr) throws InstrumentationException {
		if (!CEventAgentAdapter.isInitialized()) {
			return;
		}

		boolean activated = false;

		for (InstrumentationEntity<SynchronizedScope> entitiy : descr
				.getInstrumentationEntities(SynchronizedScope.class)) {
			CEventAgentAdapter.setMonitorListener(MonitorEventListener.getInstance());
			if (entitiy.getProbes().contains(ForMonitorWaitingTimeProbe.MODEL_PROBE)) {
				LOGGER.info("Enabling event listening with SynchronizedBlocksWaitingTimeProbe");
				LOGGER.warn("Choosing the event listeing description from the instrumentation description is not implemented yet.");
				EventProbeRegistry.getInstance().addProbe(MonitorEventListener.class,
						ForMonitorWaitingTimeProbe.class);
				activated = true;
			}
		}

		if (activated) {
			CEventAgentAdapter.setRestriction(descr.getGlobalRestriction());
			CEventAgentAdapter.enableMonitorEvents();
		}
	}

	@Override
	public void undoInstrumentation() throws InstrumentationException {
		if (!CEventAgentAdapter.isInitialized()) {
			return;
		}
		LOGGER.info("Disabling event listening.");
		CEventAgentAdapter.disableMonitorEvents();
		EventProbeRegistry.getInstance().clear();
	}

}
