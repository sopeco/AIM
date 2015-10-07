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

import static org.aim.description.extension.CommonlyUsedAPIs.DATABASE_API;
import static org.aim.description.extension.CommonlyUsedProbes.MEMORY_FOOTPRINT_PROBE;
import static org.aim.description.extension.CommonlyUsedProbes.RESPONSE_TIME_PROBE;
import static org.aim.description.extension.CommonlyUsedResources.CPU;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Modifier;

import org.aim.aiminterface.description.instrumentation.InstrumentationDescription;
import org.aim.description.builder.InstrumentationDescriptionBuilder;
import org.junit.Test;

public class IDBTest {

	@Test
	public void test() {
		final InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		InstrumentationDescription id = idBuilder.newSampling(CPU, 0).build();
		assertEquals(id.getSamplingDescriptions().size(), 1);
		assertTrue(id.toString().contains("Sampling Descriptions:\n\t\t* CPU +0"));
		
		id = idBuilder.newSampling("CustomResource", 1000).build();
		assertTrue(id.toString().contains("\n\t\t* CustomResource +1000"));
		
		idBuilder.newAllocationScopeEntity("my.package.*").addProbe(MEMORY_FOOTPRINT_PROBE).entityDone();
		id = idBuilder.build();
		assertTrue(id.toString().contains("Instrumentation Entities:\n\t\t* org.aim.artifacts.scopes.AllocationScope [my.package.*]: MemoryFootprintProbe"));
		
		idBuilder.newAPIScopeEntity(DATABASE_API).addProbe("MyProbe").entityDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\n\t\t* Database API: MyProbe"));
		
		idBuilder.newConstructorScopeEntity("my.*", "your.*").addProbe(RESPONSE_TIME_PROBE).entityDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\n\t\t* org.aim.artifacts.scopes.ConstructorScope [my.*, your.*]: ResponseTimeProbe"));
		
		idBuilder.newMemoryScopeEntity().addProbe("MyProbe").entityDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\n\t\t* org.aim.artifacts.scopes.MemoryScope: MyProbe"));
		
		idBuilder.newMethodScopeEntity("my.package.*").entityDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\n\t\t* org.aim.artifacts.scopes.MethodScope [my.package.*]: "));
		
		idBuilder.newSynchronizedScopeEntity().newLocalRestriction().excludeModifier(Modifier.PRIVATE).restrictionDone().addProbe("MyProbe").entityDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\n\t\t* org.aim.artifacts.scopes.SynchronizedScope (-\"private\" methods): MyProbe"));
		
		idBuilder.newMethodScopeEntity("my.package.*").enableTrace().newLocalRestriction().includePackage("my.package").restrictionDone().entityDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\n\t\t* TraceScope [org.aim.artifacts.scopes.MethodScope [my.package.*]] (+my.package): "));
		
		idBuilder.newAPIScopeEntity("My API").enableTrace().newLocalRestriction().includeModifier(Modifier.PUBLIC).restrictionDone().entityDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\n\t\t* TraceScope [My API] (+\"public\" methods): "));
		
		idBuilder.newConstructorScopeEntity("my.classes.*").enableTrace().entityDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\n\t\t* TraceScope [org.aim.artifacts.scopes.ConstructorScope [my.classes.*]]: "));
				
		idBuilder.newGlobalRestriction().excludePackage("my.package").restrictionDone();
		id = idBuilder.build();
		assertTrue(id.toString(), id.toString().contains("\tGlobal Restriction:\n\t\t- my.package"));
	}

}
