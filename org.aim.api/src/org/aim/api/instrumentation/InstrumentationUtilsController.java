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
package org.aim.api.instrumentation;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for instrumentation utils.
 * 
 * @author Alexander Wert
 * 
 */
public final class InstrumentationUtilsController {
	private static InstrumentationUtilsController instance;

	/**
	 * 
	 * @return singleton instance of the controller
	 */
	public static InstrumentationUtilsController getInstance() {
		if (instance == null) {
			instance = new InstrumentationUtilsController();
		}
		return instance;
	}

	private List<IInstrumentationUtil> utils;

	private InstrumentationUtilsController() {
		utils = new ArrayList<>();
	}

	/**
	 * Registers an instrumentation util.
	 * 
	 * @param util
	 *            util to register
	 */
	public void register(IInstrumentationUtil util) {
		utils.add(util);
	}

	/**
	 * Clears all instrumentation utils.
	 */
	public void clear() {
		for (IInstrumentationUtil util : utils) {
			util.clear();
		}
	}
}
