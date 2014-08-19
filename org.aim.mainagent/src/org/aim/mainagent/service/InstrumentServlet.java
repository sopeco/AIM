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

import org.aim.description.InstrumentationDescription;
import org.aim.mainagent.AdaptiveInstrumentationFacade;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

/**
 * Instruments the java byte code according to the passed Instrumentation
 * description.
 * 
 * @author Alexander Wert
 * 
 */
public class InstrumentServlet implements Service {
	@Override
	public void doService(Request req, Response resp) throws Exception {
			ObjectMapper mapper = new ObjectMapper();
			InstrumentationDescription description = mapper.readValue(req.getInputStream(),
					InstrumentationDescription.class);
			AdaptiveInstrumentationFacade.getInstance().instrument(description);


	}
}
