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
package org.aim.mainagent.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry contating event instrumentation information for event listening.
 * 
 * @author Alexander Wert
 * 
 */
public final class EventProbeRegistry {

	private static EventProbeRegistry instance;

	/**
	 * 
	 * @return singleton instance of the registry
	 */
	public static EventProbeRegistry getInstance() {
		if (instance == null) {
			instance = new EventProbeRegistry();
		}

		return instance;
	}

	private final Map<Class<? extends IEventListener<?>>, List<Class<? extends IEventProbe>>> activatedProbes = new HashMap<>();

	private EventProbeRegistry() {
	}

	/**
	 * Adds a probe for a certain event listener type.
	 * 
	 * @param listenerClass
	 *            listener type
	 * @param probeClass
	 *            probe type to add
	 * @param <P>
	 *            Probe type
	 */
	public <P extends IEventProbe> void addProbe(Class<? extends IEventListener<? super P>> listenerClass,
			Class<P> probeClass) {
		List<Class<? extends IEventProbe>> probeList = activatedProbes.get(listenerClass);
		if (probeList == null) {
			probeList = new ArrayList<>();
			activatedProbes.put(listenerClass, probeList);
		}

		if (!probeList.contains(probeClass)) {
			probeList.add(probeClass);
		}
	}

	/**
	 * Returns all probe classes for the given event listener type.
	 * 
	 * @param listenerClass
	 *            type of the listener
	 * @param <P>
	 *            probe type
	 * @return list of probe types.
	 */
	@SuppressWarnings("unchecked")
	public <P extends IEventProbe> List<Class<? extends P>> getProbeClasses(
			Class<? extends IEventListener<? super P>> listenerClass) {
		List<Class<? extends IEventProbe>> probeClassList = activatedProbes.get(listenerClass);
		if (probeClassList == null) {
			return null;
		}

		List<Class<? extends P>> castedProbeClassList = new ArrayList<>();

		for (Class<? extends IEventProbe> pc : probeClassList) {
			castedProbeClassList.add((Class<? extends P>) pc);
		}

		return (List<Class<? extends P>>) castedProbeClassList;
	}

	/**
	 * Removes all listener-to-probe class mappings.
	 */
	public void clear() {
		activatedProbes.clear();
	}

}
