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
package org.lpe.common.resourcemonitoring.service;

import java.io.OutputStream;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.aim.api.exceptions.MeasurementException;
import org.aim.description.InstrumentationDescription;
import org.aim.description.sampling.SamplingDescription;
import org.lpe.common.resourcemonitoring.SystemMonitor;

import com.sun.jersey.spi.resource.Singleton;

/**
 * This class provides a REST-service to control the System Monitoring Utility.
 * It is capable of configuring different recorders, starting and stopping them
 * as well as collecting and returning their results.
 * 
 * @author Henning Muszynski
 * 
 */
@Path("MonitoringService")
@Singleton
public class MonitoringService {

	/**
	 * Initializes and starts the monitoring application. It allows to configure
	 * the recorders which shall be used. This is done by passing a
	 * RecorderConfig object to this method. This is NOT optional there are no
	 * default values for the configuration.
	 * 
	 * @param instDescription
	 *            instrumentation description containing a sampling
	 *            configuration
	 * @throws MeasurementException
	 *             if monitoring fails
	 */
	@POST
	@Path("startMonitor")
	@Consumes(MediaType.APPLICATION_JSON)
	public void startMonitor(InstrumentationDescription instDescription) throws MeasurementException {
		Set<SamplingDescription> samplingDescriptions = instDescription.getSamplingDescriptions();

		if (!samplingDescriptions.isEmpty()) {
			SystemMonitor.getInstance().addMonitoringJob(samplingDescriptions);
			SystemMonitor.getInstance().start();
		}

	}

	/**
	 * Stops all recorder. Throws an error when no recorder has been initialized
	 * / started.
	 * 
	 */
	@POST
	@Path("stopMonitor")
	public void stopMonitor() {
		SystemMonitor.getInstance().stop();
		SystemMonitor.getInstance().clearMonitoringJobs();
	}

	/**
	 * Simply returns all gathered information from monitoring application.
	 * Returns them in a wrapper object for full JSON support.
	 * 
	 * @return all results from all recorders
	 */
	@GET
	@Path("getResult")
	@Produces({ MediaType.TEXT_PLAIN })
	public Response getResult() {

		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream os) {
				SystemMonitor.getInstance().pipeResultToOutputStream(os);
			}
		};
		Response resp = Response.ok(stream).build();
		return resp;
	}

	/**
	 * 
	 * @return current local timestamp
	 */
	@GET
	@Path("currentTime")
	@Produces({ MediaType.APPLICATION_JSON })
	public long getTimestamp() {
		return System.currentTimeMillis();
	}

	/**
	 * 
	 * @return true if connection established
	 */
	@GET
	@Path("testConnection")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean testConnection() {
		return true;
	}

}
