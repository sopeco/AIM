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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.aim.api.exceptions.MeasurementException;
import org.aim.api.measurement.AbstractRecord;
import org.aim.api.measurement.MeasurementData;
import org.aim.api.measurement.collector.AbstractDataSource;

/**
 * Collector which writes measurement data to memory.
 * 
 * @author Alexander Wert
 * 
 */
public class MemoryDataSource extends AbstractDataSource {

	private final List<AbstractRecord> recordList = new ArrayList<AbstractRecord>();

	@Override
	protected void process(AbstractRecord record) {
		recordList.add(record);

	}

	@Override
	protected void cleanUp() {
		// nothing to do here

	}

	@Override
	protected void init() throws MeasurementException {
		recordList.clear();
	}

	@Override
	public MeasurementData read() throws MeasurementException {
		MeasurementData md = new MeasurementData();
		md.setRecords(recordList);
		return md;
	}

	@Override
	public void pipeToOutputStream(OutputStream oStream) throws MeasurementException {
		BufferedWriter writer = null;
		try {

			writer = new BufferedWriter(new OutputStreamWriter(oStream), BUFFER_SIZE);

			for (AbstractRecord rec : recordList) {
				writer.write(rec.toString());
				writer.newLine();
			}

		} catch (IOException e) {
			throw new MeasurementException(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new MeasurementException(e);
				}
			}
		}
	}

	@Override
	public void initialize(Properties properties) {

	}

}
