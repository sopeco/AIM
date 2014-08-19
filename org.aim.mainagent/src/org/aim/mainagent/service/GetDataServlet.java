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

import java.io.OutputStream;

import org.aim.api.measurement.collector.AbstractDataSource;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.HttpStatus;

/**
 * Retrieves collected measurement data.
 * 
 * @author Alexander Wert
 * 
 */
public class GetDataServlet implements Service {
	@Override
	public void doService(Request req, Response resp) throws Exception {

		AbstractDataSource dataSource = AbstractDataSource.getDefaultDataSource();

		final OutputStream oStream = resp.getOutputStream();
		resp.setContentType("text/plain");
		resp.setStatus(HttpStatus.OK_200);
		dataSource.pipeToOutputStream(oStream);

	}
}
