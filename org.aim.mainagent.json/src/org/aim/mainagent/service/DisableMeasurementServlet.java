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

import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.aim.mainagent.service.helper.AdaptiveFacadeProvider;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.lpe.common.util.web.Service;

/**
 * Disables data collection.
 * 
 * @author Alexander Wert
 * 
 */
public class DisableMeasurementServlet implements Service {
	private static final AIMLogger LOGGER = AIMLoggerFactory.getLogger(DisableMeasurementServlet.class);

	@Override
	public void doService(final Request req, final Response resp) throws Exception {
		LOGGER.info("Disabling measurement ...");
		AdaptiveFacadeProvider.getAdaptiveInstrumentation().disableMonitoring();
		MeasurementStateServlet.setMeasurementState(false);
		LOGGER.info("Measurement disabled!");
	}
}
