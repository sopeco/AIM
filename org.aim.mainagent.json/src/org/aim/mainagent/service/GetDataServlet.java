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

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.aim.mainagent.service.helper.AdaptiveFacadeProvider;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.lpe.common.util.web.Service;

/**
 * Retrieves collected measurement data.
 * 
 * @author Alexander Wert
 * 
 */
public class GetDataServlet implements Service {
	private static final AIMLogger LOGGER = AIMLoggerFactory.getLogger(EnableMeasurementServlet.class);

	@Override
	public void doService(final Request req, final Response resp) throws Exception {
		LOGGER.info("Requested data transfer ...");

		final List<String> data = AdaptiveFacadeProvider.getAdaptiveInstrumentation().getMeasurementData();
		
		final OutputStream oStream = resp.getOutputStream();
		resp.setContentType("text/plain");
		resp.setStatus(HttpStatus.OK_200);


		try(
				final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(oStream))
		)
		{
			for (final String currentRecord : data) {
				writer.write(currentRecord);
				writer.newLine();
			}
		} catch (final Exception e) {
			LOGGER.error("Failed to stream Json for measurements", e);
			return;
		}

		LOGGER.info("Data transfer finished!");

	}
}
