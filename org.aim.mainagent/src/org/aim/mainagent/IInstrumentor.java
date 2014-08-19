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

/**
 * Common instrumentation interface.
 * 
 * @author Alexander Wert
 * 
 */
interface IInstrumentor {

	/**
	 * Instruments target application according to the passed instrumentation
	 * description.
	 * 
	 * @param descr
	 *            instrumentation description
	 * @throws InstrumentationException
	 *             if instrumentation fails
	 */
	void instrument(InstrumentationDescription descr) throws InstrumentationException;

	/**
	 * Reverts all instrumentation steps.
	 * 
	 * @throws InstrumentationException
	 *             if instrumentation fails
	 */
	void undoInstrumentation() throws InstrumentationException;
}
