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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.aim.api.exceptions.MeasurementException;
import org.aim.api.measurement.AbstractRecord;
import org.aim.api.measurement.MeasurementData;
import org.aim.api.measurement.collector.IDataReader;
import org.lpe.common.util.LpeStreamUtils;

/**
 * Reads datasets from a stream.
 * 
 * @author Alexander Wert
 * 
 */
public class StreamReader implements IDataReader {

	private InputStream inStream;

	/**
	 * Sets input stream.
	 * 
	 * @param source
	 *            input stream to set as source
	 */
	public void setSource(InputStream source) {
		inStream = source;

	}

	@Override
	public MeasurementData read() throws MeasurementException {
		List<AbstractRecord> result = new ArrayList<AbstractRecord>();
		BufferedReader bReader = null;
		try {
			bReader = new BufferedReader(new InputStreamReader(inStream));
			String line = bReader.readLine();
			while (line != null) {
				AbstractRecord record = AbstractRecord.fromString(line);
				if (record != null) {
					result.add(record);
				}
				line = bReader.readLine();
			}
			MeasurementData data = new MeasurementData();
			data.setRecords(result);
			return data;
		} catch (IOException e) {
			throw new MeasurementException(e);
		} finally {
			if (bReader != null) {
				try {
					bReader.close();
				} catch (IOException e) {
					throw new MeasurementException(e);
				}
			}
		}

	}

	@Override
	public void pipeToOutputStream(OutputStream oStream) throws MeasurementException {

		try {
			LpeStreamUtils.pipe(inStream, oStream);
		} catch (IOException e) {
			throw new MeasurementException(e);
		}
	}

}
