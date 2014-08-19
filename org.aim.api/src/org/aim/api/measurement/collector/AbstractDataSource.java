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
package org.aim.api.measurement.collector;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.aim.api.exceptions.MeasurementException;
import org.aim.api.measurement.AbstractRecord;
import org.lpe.common.util.system.LpeSystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The abstract {@link Datacollector} should be the super-class for all data
 * collectors.
 * 
 * @author Alexander Wert
 * 
 */
public abstract class AbstractDataSource implements IDataCollector, IDataReader, Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataSource.class);
	protected static final int BUFFER_SIZE = 1024;
	private static final long MILLISECONDS_TO_WAIT = 500;

	private boolean finished;

	private static AbstractDataSource dataSource;
	private boolean enabled = false;

	/**
	 * Pipe for colelcted records.
	 */
	protected final BlockingQueue<AbstractRecord> records = new LinkedBlockingQueue<AbstractRecord>();

	/**
	 * Abstract Constructor.
	 * 
	 */
	public AbstractDataSource() {
		enabled = false;
		finished = true;
	}

	/**
	 * Processes the record, in particular writing it to the data sink.
	 * 
	 * @param record
	 *            record to be processed
	 */
	protected abstract void process(AbstractRecord record);

	/**
	 * Cleans up the writer, in particular the data sink.
	 */
	protected abstract void cleanUp();

	/**
	 * Initializes the data writer.
	 * 
	 * @throws MeasurementException
	 *             Thrown if initialization fails.
	 */
	protected abstract void init() throws MeasurementException;

	/**
	 * Initializes the data source.
	 * 
	 * @param properties
	 *            properties to use for initialization
	 */
	public abstract void initialize(Properties properties);

	/**
	 * Repeatedly reads data from the pipe and processes it.
	 */
	public void run() {
		while (enabled || !records.isEmpty()) {
			try {
				AbstractRecord record = records.poll(MILLISECONDS_TO_WAIT, TimeUnit.MILLISECONDS);
				if (record != null) {
					process(record);
				}
			} catch (InterruptedException e) {
				LOGGER.error("Monitoring error. Reason: {}", e);
			}
		}

		synchronized (this) {
			enabled = false;
			this.notifyAll();
		}

		finished = true;
	}

	/**
	 * Writes the new record to the pipe.
	 * 
	 * @param record
	 *            the new record to be collected.
	 */
	@Override
	public void newRecord(AbstractRecord record) {
		if (!enabled) {
			return;
		}
		boolean ok = records.offer(record);
		if (!ok) {
			LOGGER.warn("Writer queue is full. Measurement record ignored!");
		}

	}

	@Override
	public void enable() throws MeasurementException {
		LOGGER.debug("Enabling Data Collector...");
		init();
		enabled = true;

		if (finished) {
			finished = false;
			LpeSystemUtils.submitTask(this);
		}

		LOGGER.debug("Data Collector is running");

	}

	@Override
	public void disable() {
		enabled = false;
		synchronized (this) {
			while (!records.isEmpty()) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					LOGGER.error("Wait for empty queue interrupted. Cause: {}", e.getMessage());
					throw new RuntimeException("Wait for empty queue interrupted.");
				}
			}
		}

		cleanUp();
		LOGGER.debug("Data Collector disabled");
	}

	/**
	 * @return the dataCollector
	 */
	public static AbstractDataSource getDefaultDataSource() {
		return dataSource;
	}

	/**
	 * 
	 * @return Returns true, if data writer has been disabled and has finished
	 *         writing data, otherwise returns false;
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public static void setDefaultDataSource(AbstractDataSource dataSource) {
		AbstractDataSource.dataSource = dataSource;
	}

}
