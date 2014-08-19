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
package org.aim.description;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Modifier;

import org.aim.description.builder.InstrumentationDescriptionBuilder;
import org.aim.description.probes.MeasurementProbe;
import org.aim.description.scopes.AllocationScope;
import org.aim.description.scopes.MethodsEnclosingScope;
import org.aim.description.scopes.Scope;
import org.junit.Test;

public class IDBTest {

	private static final MeasurementProbe<AllocationScope> MEMORY_FOOTPRINT_PROBE = new MeasurementProbe<>("MemoryFootprintProbe");
	private static final MeasurementProbe<Scope> RESPONSE_TIME_PROBE = new MeasurementProbe<>("ResponseTimeProbe");
	private static final MeasurementProbe<MethodsEnclosingScope> TRACING_PROBE = new MeasurementProbe<>("TracingProbe");
	private static final String CPU_RESOURCE = "CPU";
	private static final String DATABASE_API = "Database API";

	@Test
	public void test() {
		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		idBuilder.newSampling(CPU_RESOURCE, 0);
		
		InstrumentationDescription id = idBuilder.build();
		assertEquals(id.getSamplingDescriptions().size(), 1);
		assertTrue(id.toString().contains("Sampling Descriptions:\n\t\t* CPU +0"));
		
		idBuilder.newSampling("CustomResource", 1000);
		id = idBuilder.build();
		assertTrue(id.toString().contains("\n\t\t* CustomResource +1000"));
		
		idBuilder.newAllocationScopeEntity("my.package.*").addProbe(MEMORY_FOOTPRINT_PROBE).entityDone();
		id = idBuilder.build();
		assertTrue(id.toString().contains("Instrumentation Entities:\n\t\t* Allocation Scope [my.package.*]: MemoryFootprintProbe"));
		
		idBuilder.newAPIScopeEntity(DATABASE_API).addProbe("MyProbe").entityDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\n\t\t* Database API Scope: MyProbe"));
		
		idBuilder.newConstructorScopeEntity("my.*", "your.*").addProbe(RESPONSE_TIME_PROBE).entityDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\n\t\t* Constructor Scope [my.*, your.*]: ResponseTimeProbe"));
		
		idBuilder.newCustomScopeEntity("My Scope").addProbe(TRACING_PROBE).entityDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\n\t\t* My Scope: TracingProbe"));
		
		idBuilder.newMemoryScopeEntity().addProbe("MyProbe").entityDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\n\t\t* Memory Scope: MyProbe"));
		
		idBuilder.newMethodScopeEntity("my.package.*").entityDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\n\t\t* Method Scope [my.package.*]: "));
		
		idBuilder.newSynchronizedScopeEntity().newLocalRestriction().excludeModifier(Modifier.PRIVATE).restrictionDone().addProbe("MyProbe").entityDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\n\t\t* Synchronized Scope (-\"private\" methods): MyProbe"));
		
		idBuilder.newTraceScopeEntity().setMethodSubScope("my.package.*").newLocalRestriction().includePackage("my.package").restrictionDone().entityDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\n\t\t* Trace Scope [Method Scope [my.package.*]] (+my.package): "));
		
		idBuilder.newTraceScopeEntity().setAPISubScope("My API").newLocalRestriction().includeModifier(Modifier.PUBLIC).restrictionDone().entityDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\n\t\t* Trace Scope [My API Scope] (+\"public\" methods): "));
		
		idBuilder.newTraceScopeEntity().setConstructorSubScope("my.classes.*").entityDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\n\t\t* Trace Scope [Constructor Scope [my.classes.*]]: "));
		
		idBuilder.newTraceScopeEntity().setCustomSubScope("MyScope").entityDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\n\t\t* Trace Scope [MyScope]: "));
		
		idBuilder.newGlobalRestriction().excludePackage("my.package").restrictionDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\tGlobal Restriction:\n\t\t- my.package"));
	}

}
