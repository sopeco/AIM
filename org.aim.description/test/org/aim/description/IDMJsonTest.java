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

import java.io.IOException;
import java.lang.reflect.Modifier;

import javax.ws.rs.core.MediaType;

import org.aim.description.builder.InstrumentationDescriptionBuilder;
import org.aim.description.probes.MeasurementProbe;
import org.aim.description.scopes.MethodsEnclosingScope;
import org.aim.description.scopes.Scope;
import org.aim.description.servlet.TestIDMServlet;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lpe.common.util.web.LpeWebUtils;

import com.sun.jersey.api.client.WebResource;

public class IDMJsonTest {

	private static final MeasurementProbe<MethodsEnclosingScope> ME_PROBE = new MeasurementProbe<>("MEProbe");
	private static final MeasurementProbe<Scope> ALL_PROBE = new MeasurementProbe<>("AllProbe");

	private static final String PORT = "8123";

	private WebResource webResource;

	/**
	 * starts server.
	 * 
	 * @throws IOException
	 */
	@BeforeClass
	public static void startServer() throws IOException {
		HttpServer server = HttpServer.createSimpleServer("", Integer.parseInt(PORT));
		final TestIDMServlet servlet = new TestIDMServlet();
		server.getServerConfiguration().addHttpHandler(new HttpHandler() {

			@Override
			public void service(Request req, Response resp) throws Exception {
				servlet.doService(req, resp);

			}
		}, "/testDescription");
		server.start();
	}

	@Before
	public void getWebResource() {
		webResource = LpeWebUtils.getWebClient().resource("http://localhost:" + PORT);
	}

	@Test
	public void testEmptyDescription() {
		InstrumentationDescription description = new InstrumentationDescription();
		assertEquals(description, sendAndReceiveDescription(description));
	}

	@Test
	public void testSamplingDescription() {
		InstrumentationDescriptionBuilder idb = new InstrumentationDescriptionBuilder();
		idb.newSampling("CPU", 100);
		assertEquals(idb.build(), sendAndReceiveDescription(idb.build()));
		idb.newSampling("", 0);
		assertEquals(idb.build(), sendAndReceiveDescription(idb.build()));
		idb.newSampling(null, -1);
		assertEquals(idb.build(), sendAndReceiveDescription(idb.build()));
	}

	@Test
	public void testInstrumentationEntities() {
		InstrumentationDescriptionBuilder idb = new InstrumentationDescriptionBuilder();
		idb.newMethodScopeEntity("foo.*").entityDone();
		assertEquals(idb.build(), sendAndReceiveDescription(idb.build()));
		idb.newMethodScopeEntity("foo.*").addProbe(ME_PROBE).entityDone();
		assertEquals(idb.build(), sendAndReceiveDescription(idb.build()));
	}

	@Test
	public void testScopes() {
		InstrumentationDescriptionBuilder idb = new InstrumentationDescriptionBuilder();
		idb.newAllocationScopeEntity("classes.*").addProbe(ALL_PROBE).entityDone();
		assertEquals(idb.build(), sendAndReceiveDescription(idb.build()));
		idb.newAPIScopeEntity("My").addProbe(ALL_PROBE).entityDone();
		assertEquals(idb.build(), sendAndReceiveDescription(idb.build()));
		idb.newConstructorScopeEntity("classes.*").addProbe(ALL_PROBE).entityDone();
		assertEquals(idb.build(), sendAndReceiveDescription(idb.build()));
		idb.newCustomScopeEntity("MyScope").addProbe(ALL_PROBE).entityDone();
		assertEquals(idb.build(), sendAndReceiveDescription(idb.build()));
		idb.newMemoryScopeEntity().addProbe(ALL_PROBE).entityDone();
		assertEquals(idb.build(), sendAndReceiveDescription(idb.build()));
		idb.newMethodScopeEntity("foo.*").addProbe(ALL_PROBE).entityDone();
		assertEquals(idb.build(), sendAndReceiveDescription(idb.build()));
		idb.newSynchronizedScopeEntity().addProbe(ALL_PROBE).entityDone();
		assertEquals(idb.build(), sendAndReceiveDescription(idb.build()));
		idb.newTraceScopeEntity().setMethodSubScope("foo.*").addProbe(ALL_PROBE).entityDone();
		assertEquals(idb.build(), sendAndReceiveDescription(idb.build()));
	}

	@Test
	public void testRestrictions() {
		InstrumentationDescriptionBuilder idb = new InstrumentationDescriptionBuilder();
		idb.newGlobalRestriction().restrictionDone();
		assertEquals(idb.build(), sendAndReceiveDescription(idb.build()));
		idb.newGlobalRestriction().excludePackage("foo.*").restrictionDone();
		assertEquals(idb.build(), sendAndReceiveDescription(idb.build()));
		idb.newGlobalRestriction().excludeModifier(Modifier.PRIVATE).restrictionDone();
		assertEquals(idb.build(), sendAndReceiveDescription(idb.build()));
		idb.newGlobalRestriction().includeModifier(Modifier.PUBLIC).restrictionDone();
		assertEquals(idb.build(), sendAndReceiveDescription(idb.build()));
		idb.newGlobalRestriction().includePackage("foo.bar.*").restrictionDone();
		assertEquals(idb.build(), sendAndReceiveDescription(idb.build()));
	}

	private InstrumentationDescription sendAndReceiveDescription(InstrumentationDescription description) {
		return webResource.path("testDescription").accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.post(InstrumentationDescription.class, description);
	}

}
