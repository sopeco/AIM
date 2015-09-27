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
package org.aim.description.extension;

import org.aim.aiminterface.description.measurementprobe.MeasurementProbe;

/**
 * Utility Class wrapping commonly used Measurement Probes..
 * 
 * @author Alexander Wert
 * 
 */
public final class CommonlyUsedProbes {

	public static final MeasurementProbe RESPONSE_TIME_PROBE = new MeasurementProbe("ResponseTimeProbe",
			CommonlyUsedScopeTypes.METHOD_ENCLOSING_SCOPE_TYPE);
	public static final MeasurementProbe TRACING_PROBE = new MeasurementProbe("TracingProbe",
			CommonlyUsedScopeTypes.METHOD_ENCLOSING_SCOPE_TYPE);
	public static final MeasurementProbe MEMORY_FOOTPRINT_PROBE = new MeasurementProbe("MemoryFootprintProbe");
	public static final MeasurementProbe WAITING_TIME_PROBE = new MeasurementProbe("WaitingTimeProbe");

	private CommonlyUsedProbes() {
		// should not be instantiated
	}

}
