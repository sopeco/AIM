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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.aim.api.exceptions.MeasurementException;
import org.aim.api.measurement.AbstractRecord;
import org.aim.api.measurement.MeasurementData;
import org.aim.api.measurement.collector.AbstractDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FileDataSource} writes collected monitoring records to a CSV file
 * on the hard disk.
 * 
 * @author Alexander Wert
 * 
 */
public class FileDataSource extends AbstractDataSource {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileDataSource.class);

	/**
	 * Property key for the sink directory (directory where to write the CSV
	 * files).
	 */
	public static final String SINK_DIRECTORY = "org.aim.fileDataSource.sinkDirectory";

	public static final String FILE_PREFIX = "LPE-Collector";
	public static final String ADDITIONAL_FILE_PREFIX_KEY = "org.lpe.measurement.fileWriter.prefix";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private FileReader fileReader;

	private Writer fileWriter = null;
	private String pathToFile = null;
	private String sink;

	private String prefix;

	@Override
	protected void process(AbstractRecord record) {
		try {
			fileWriter.append(record.toString());
			fileWriter.append(LINE_SEPARATOR);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void cleanUp() {
		try {
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			LOGGER.error("Monitoring error! Reason: {}", e);
		}

	}

	@Override
	protected void init() throws MeasurementException {
		LOGGER.debug("Initializing file writer ...");

		removePreviousFile();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-mm");
		Date resultdate = new Date(System.currentTimeMillis());

		sink = pathToFile + FILE_PREFIX + "_" + prefix + "_" + this.getClass().getSimpleName() + "_"
				+ sdf.format(resultdate) + ".txt";

		try {
			fileWriter = new FileWriter(sink);
		} catch (IOException e) {
			throw new MeasurementException("Measurement error! Failed creating FileDataWriter.", e);
		}
		LOGGER.debug("File writer initialized");

	}

	/**
	 * Removes collector files from previous monitoring phase.
	 */
	private void removePreviousFile() {
		if (sink == null) {
			return;
		}
		File file = new File(sink);
		if (file.exists() && file.canWrite()) {
			file.delete();
		}
	}

	/**
	 * Reads monitoring data from the CSV file.
	 * 
	 * @return A list of records wrapped in the {@link MeasurementData} object
	 * 
	 * @throws MeasurementException
	 *             thrown if data cannot be read from the data sink
	 */
	@Override
	public MeasurementData read() throws MeasurementException {
		try {
			if (sink == null) {
				throw new MeasurementException("Sink is not specified!");
			}
			List<AbstractRecord> result = new ArrayList<AbstractRecord>();
			fileReader = new FileReader(sink);

			BufferedReader bReader = new BufferedReader(fileReader);

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
			throw new MeasurementException("Failed reading measurement data!", e);
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					throw new MeasurementException(e);
				}
			}

		}
	}

	@Override
	public void pipeToOutputStream(OutputStream oStream) throws MeasurementException {
		if (sink == null) {
			throw new MeasurementException("Sink is not specified!");
		}
		BufferedReader br = null;
		BufferedWriter writer = null;
		try {
			FileReader fReader = new FileReader(sink);
			br = new BufferedReader(fReader, BUFFER_SIZE);
			writer = new BufferedWriter(new OutputStreamWriter(oStream), BUFFER_SIZE);
			String line;
			while ((line = br.readLine()) != null) {
				writer.write(line);
				writer.newLine();
			}
			writer.flush();
		} catch (IOException e) {
			throw new MeasurementException(e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}

				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				throw new MeasurementException(e);
			}
		}
	}

	@Override
	public void initialize(Properties properties) {
		String tmpPrefix = null;
		if (properties != null) {
			this.pathToFile = properties.getProperty(SINK_DIRECTORY);
			tmpPrefix = properties.getProperty(ADDITIONAL_FILE_PREFIX_KEY);
		}

		prefix = tmpPrefix == null ? "" : tmpPrefix;

		if (pathToFile == null || pathToFile.isEmpty()) {
			pathToFile = System.getProperty("java.io.tmpdir");
		}

		if (!pathToFile.endsWith(System.getProperty("file.separator"))) {
			pathToFile = pathToFile + System.getProperty("file.separator");
		}
		LOGGER.debug("Created data collector. Writing data to directory: {}", pathToFile);
	}

}
