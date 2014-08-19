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
package org.aim.mainagent.service;

import org.aim.api.instrumentation.entities.SupportedExtensions;
import org.aim.mainagent.AdaptiveInstrumentationFacade;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

/**
 * Returns information on available extensions.
 * @author Alexander Wert
 *
 */
public class GetSupportedExtensionsServlet implements Service {

	@Override
	public void doService(Request req, Response resp) throws Exception {
		JsonFactory factory = new JsonFactory();
		ObjectMapper mapper = new ObjectMapper(factory);

		SupportedExtensions extensions = AdaptiveInstrumentationFacade.getInstance().getSupportedExtensions();

		resp.setContentType("application/json");
		mapper.writeValue(resp.getOutputStream(), extensions);

	}

}
