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

import java.io.OutputStream;

import org.aim.api.exceptions.MeasurementException;
import org.aim.api.measurement.MeasurementData;

/**
 * The interface {@link IDataReader} is the counterpart to the
 * {@link org.lpe.common.measurement.collector.AbstractDataWriter}.
 * 
 * @author Alexander Wert
 * 
 */
public interface IDataReader {

	/**
	 * Reads data from the sink.
	 * 
	 * @return A list of records wrapped in the {@link MeasurementData} object
	 * @throws MeasurementException
	 *             thrown if data cannot be read from the data sink
	 */
	MeasurementData read() throws MeasurementException;

	/**
	 * Pipes the input to the given output stream.
	 * 
	 * @param oStream
	 *            outputStream where to pipe to
	 * @throws MeasurementException
	 *             if something goes wrong
	 */
	void pipeToOutputStream(OutputStream oStream) throws MeasurementException;
}
