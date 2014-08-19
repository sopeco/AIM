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
package org.aim.artifacts.measurement.collector;

import java.io.OutputStream;
import java.util.Properties;

import org.aim.api.exceptions.MeasurementException;
import org.aim.api.measurement.AbstractRecord;
import org.aim.api.measurement.MeasurementData;
import org.aim.api.measurement.collector.AbstractDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link DummyDataSource} droppes all collected records to simulate a
 * nearly infinitely fast hard disk.
 * 
 * @author Henning Schulz
 * 
 */
public class DummyDataSource extends AbstractDataSource {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileDataSource.class);

	@Override
	protected void process(AbstractRecord record) {
		// Nothing to do
	}

	@Override
	protected void cleanUp() {
		// Nothing to do
	}

	@Override
	protected void init() throws MeasurementException {
		// Nothing to do
	}

	@Override
	public MeasurementData read() throws MeasurementException {
		return new MeasurementData();
	}

	@Override
	public void pipeToOutputStream(OutputStream oStream) throws MeasurementException {
		// Nothing to do
	}

	@Override
	public void initialize(Properties properties) {
		// Nothing to do
		LOGGER.debug("Created dummy data collector dropping all records.");
	}

}
