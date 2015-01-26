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

import org.aim.description.probes.MeasurementProbe;
import org.aim.description.scopes.MemoryScope;
import org.aim.description.scopes.MethodsEnclosingScope;
import org.aim.description.scopes.SynchronizedScope;

/**
 * Utility Class wrapping commonly used Measurement Probes..
 * 
 * @author Alexander Wert
 * 
 */
public final class CommonlyUsedProbes {

	public static final MeasurementProbe<MethodsEnclosingScope> RESPONSE_TIME_PROBE = new MeasurementProbe<>(
			"ResponseTimeProbe");
	public static final MeasurementProbe<MethodsEnclosingScope> TRACING_PROBE = new MeasurementProbe<>("TracingProbe");
	public static final MeasurementProbe<MemoryScope> MEMORY_FOOTPRINT_PROBE = new MeasurementProbe<>(
			"MemoryFoortprintProbe");
	public static final MeasurementProbe<SynchronizedScope> WAITING_TIME_PROBE = new MeasurementProbe<>(
			"WaitingTimeProbe");

	private CommonlyUsedProbes() {
		// should not be instantiated
	}

}
