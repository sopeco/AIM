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
package org.aim.artifacts.probes.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;

import org.aim.api.instrumentation.IInstrumentationUtil;
import org.aim.api.instrumentation.InstrumentationUtilsController;

/**
 * Cache for memory management beans.
 * 
 * @author Alexander Wert
 * 
 */
public final class MemoryMXBeanCache implements IInstrumentationUtil {
	private static MemoryMXBeanCache instance;

	/**
	 * 
	 * @return singleton instance of the cache
	 */
	public static MemoryMXBeanCache getInstance() {
		if (instance == null) {
			instance = new MemoryMXBeanCache();
		}
		return instance;
	}

	private static final String[] EDEN_SPACE_NAMES = { "Eden Space", "PS Eden Space", "Par Eden Space", "G1 Eden" };
	private static final String[] SURVIVOR_SPACE_NAMES = { "Survivor Space", "PS Survivor Space", "Par Survivor Space",
			"G1 Survivor" };
	private static final String[] TENURED_SPACE_NAMES = { "Tenured Gen", "PS Old Gen", "CMS Old Gen", "G1 Old Gen" };

	private MemoryPoolMXBean edenBean;
	private MemoryPoolMXBean survivorBean;
	private MemoryPoolMXBean oldBean;

	private MemoryMXBeanCache() {
		beanLoop: for (MemoryPoolMXBean bean : ManagementFactory.getMemoryPoolMXBeans()) {
			for (String name : EDEN_SPACE_NAMES) {
				if (bean.getName().equals(name)) {
					edenBean = bean;
					continue beanLoop;
				}
			}

			for (String name : SURVIVOR_SPACE_NAMES) {
				if (bean.getName().equals(name)) {
					survivorBean = bean;
					continue beanLoop;
				}
			}

			for (String name : TENURED_SPACE_NAMES) {
				if (bean.getName().equals(name)) {
					oldBean = bean;
					continue beanLoop;
				}
			}
		}

		InstrumentationUtilsController.getInstance().register(this);
	}

	/**
	 * @return the edenBean
	 */
	public MemoryPoolMXBean getEdenBean() {
		return edenBean;
	}

	/**
	 * @return the survivorBean
	 */
	public MemoryPoolMXBean getSurvivorBean() {
		return survivorBean;
	}

	/**
	 * @return the oldBean
	 */
	public MemoryPoolMXBean getOldBean() {
		return oldBean;
	}

	@Override
	public void clear() {
		// nothing to do
	}
}
