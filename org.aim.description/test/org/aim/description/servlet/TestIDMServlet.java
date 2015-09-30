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
package org.aim.description.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import org.aim.aiminterface.description.instrumentation.InstrumentationDescription;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

public class TestIDMServlet {

	public void doService(final Request req, final Response resp) throws Exception {
		resp.setContentType("application/json");

		final byte[] b = new byte[req.getContentLength()];
		final ObjectMapper mapper = new ObjectMapper();
		try {
			req.getInputStream().read(b,0, req.getContentLength());
			final BufferedReader reader = new BufferedReader(new InputStreamReader( new ByteArrayInputStream(b) ));
			String s;
			while ((s = reader.readLine()) != null) {
				System.out.println(s);
			}
			final InstrumentationDescription description = mapper.readValue(b,
					InstrumentationDescription.class);
			mapper.writeValue(resp.getOutputStream(), description);
		} catch (final JsonParseException jpe) {
			jpe.printStackTrace();
			throw new IllegalArgumentException("Incoming description is invalid!");
		} catch (final JsonMappingException jme) {
			jme.printStackTrace();
			throw new IllegalArgumentException("Incoming description is invalid!");
		}
	}
}
