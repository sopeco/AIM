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

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

/**
 * A grizzly REST Service interface.
 * 
 * @author Alexander Wert
 * 
 */
public interface Service {
	/**
	 * Services a request.
	 * 
	 * @param req
	 *            HTTP request
	 * @param resp
	 *            HTTP response
	 * @throws Exception
	 *             if something goes wrong.
	 */
	void doService(Request req, Response resp) throws Exception;
}
