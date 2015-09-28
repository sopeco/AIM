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

import org.aim.aiminterface.description.instrumentation.InstrumentationDescription;
import org.aim.aiminterface.exceptions.InstrumentationException;
import org.aim.aiminterface.exceptions.MeasurementException;
import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.aim.mainagent.sampling.Sampling;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.lpe.common.util.web.Service;

/**
 * Instruments the java byte code according to the passed Instrumentation
 * description.
 * 
 * @author Alexander Wert
 * 
 */
public class InstrumentServlet implements Service {
	private static final AIMLogger LOGGER = AIMLoggerFactory.getLogger(EnableMeasurementServlet.class);

	@Override
	public void doService(final Request req, final Response resp) throws Exception {
		LOGGER.info("Requested instrumentation ...");
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final InstrumentationDescription description = mapper.readValue(req.getInputStream(),
					InstrumentationDescription.class);
			//AdaptiveFacadeProvider.getAdaptiveInstrumentation().instrument(description);
			if (!description.getSamplingDescriptions().isEmpty()) {
				try {
					Sampling.getInstance().addMonitoringJob(description.getSamplingDescriptions());
				} catch (final MeasurementException e) {
					throw new InstrumentationException(e);
				}
			}

		} catch(final Exception e) {
			LOGGER.error("Instrumentation failed with exception {}", e);
			throw e;
		}
		LOGGER.info("Instrumentation finished!");

	}
}
