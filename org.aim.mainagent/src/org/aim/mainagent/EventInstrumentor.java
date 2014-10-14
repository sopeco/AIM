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

import org.aim.api.events.AbstractEventProbe;
import org.aim.api.events.IMonitorEventProbe;
import org.aim.api.exceptions.InstrumentationException;
import org.aim.description.InstrumentationDescription;
import org.aim.description.InstrumentationEntity;
import org.aim.description.probes.MeasurementProbe;
import org.aim.description.scopes.SynchronizedScope;
import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.aim.mainagent.events.EventProbeRegistry;
import org.aim.mainagent.events.MonitorEventListener;
import org.lpe.common.extension.ExtensionRegistry;
import org.lpe.common.extension.IExtension;

/**
 * Instrumentor responsible for activating and deactivating JVM events and
 * listening on event occurance.
 * 
 * @author Alexander Wert
 * 
 */
public final class EventInstrumentor implements IInstrumentor {

	private static final AIMLogger LOGGER = AIMLoggerFactory.getLogger(EventInstrumentor.class);

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

	@SuppressWarnings("unchecked")
	@Override
	public void instrument(InstrumentationDescription descr) throws InstrumentationException {
		if (!CEventAgentAdapter.isInitialized()) {
			return;
		}

		boolean activated = false;

		for (InstrumentationEntity<SynchronizedScope> entitiy : descr
				.getInstrumentationEntities(SynchronizedScope.class)) {
			CEventAgentAdapter.setMonitorListener(MonitorEventListener.getInstance());
			for (MeasurementProbe<? super SynchronizedScope> mProbe : entitiy.getProbes()) {
				LOGGER.info("Enabling event listening with SynchronizedBlocksWaitingTimeProbe");
				LOGGER.warn("Choosing the event listening description from the instrumentation description is not implemented yet.");

				IExtension<?> ext = ExtensionRegistry.getSingleton().getExtension(mProbe.getName());
				if (ext == null) {
					throw new InstrumentationException("Failed loading Probe class " + mProbe.getName());
				}
				IExtension<AbstractEventProbe> probeExtension = (IExtension<AbstractEventProbe>) ext;
				AbstractEventProbe eventProbe = probeExtension.createExtensionArtifact();
				if (eventProbe instanceof IMonitorEventProbe) {
					IMonitorEventProbe monitorProbe = (IMonitorEventProbe) eventProbe;
					EventProbeRegistry.getInstance().addProbe(MonitorEventListener.class, monitorProbe.getClass());
					CEventAgentAdapter.setMonitorGranularity(entitiy.getLocalRestriction().getGranularity());
					activated = true;
					break;
				}

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
