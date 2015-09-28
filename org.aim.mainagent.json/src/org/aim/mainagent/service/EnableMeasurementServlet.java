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

import org.aim.aiminterface.exceptions.MeasurementException;
import org.aim.api.measurement.collector.AbstractDataSource;
import org.aim.api.measurement.collector.IDataCollector;
import org.aim.logging.AIMLogger;
import org.aim.logging.AIMLoggerFactory;
import org.aim.mainagent.sampling.Sampling;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.lpe.common.util.web.Service;

/**
 * Enables measurement and data collection.
 * 
 * @author Alexander Wert
 * 
 */
public class EnableMeasurementServlet implements Service {
	private static final AIMLogger LOGGER = AIMLoggerFactory.getLogger(EnableMeasurementServlet.class);

	@Override
	public void doService(final Request req, final Response resp) throws Exception {
		try {
			LOGGER.info("Enabling measurement ...");
			final IDataCollector collector = AbstractDataSource.getDefaultDataSource();

			collector.enable();
			Sampling.getInstance().start();
			MeasurementStateServlet.setMeasurementState(true);
			LOGGER.info("Measurement enabled!");
		} catch (final MeasurementException e) {
			LOGGER.error("Error during enabling measurement! Reason: {}", e);
			throw new Exception(e);
		}
	}
}
