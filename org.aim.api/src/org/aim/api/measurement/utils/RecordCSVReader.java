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
package org.aim.api.measurement.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.aim.api.measurement.AbstractRecord;
import org.aim.api.measurement.dataset.Dataset;
import org.aim.api.measurement.dataset.DatasetBuilder;
import org.aim.api.measurement.dataset.DatasetCollection;
import org.aim.api.measurement.dataset.DatasetCollectionBuilder;
import org.aim.api.measurement.dataset.Parameter;
import org.lpe.common.util.LpeStringUtils;
import org.lpe.common.util.LpeSupportedTypes;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Reads persisted Records from CSV Files.
 * 
 * @author Alexander Wert
 * 
 */
public final class RecordCSVReader {
	private static RecordCSVReader instance;

	/**
	 * 
	 * @return singleton instance
	 */
	public static RecordCSVReader getInstance() {
		if (instance == null) {
			instance = new RecordCSVReader();
		}
		return instance;
	}

	private RecordCSVReader() {
	}

	/**
	 * Reads all records from a directory.
	 * 
	 * @param dir
	 *            target directory
	 * @return list of records
	 */
	public List<AbstractRecord> readFromDirectory(String dir) {
		try {
			LpeStringUtils.correctFileSeparator(dir);
			if (!dir.endsWith(System.getProperty("file.separator"))) {
				dir = dir + System.getProperty("file.separator");
			}

			Map<String, Class<? extends AbstractRecord>> typeMapping = loadFileTypeMapping(dir);

			List<AbstractRecord> records = new ArrayList<AbstractRecord>();
			for (String file : typeMapping.keySet()) {
				readRecordsFromFile(dir + file + RecordCSVWriter.CSV_FILE_EXTENSION, typeMapping.get(file), records);
			}
			return records;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reads all records of the passed record type from a directory.
	 * 
	 * @param dir
	 *            target directory
	 * @param recordType
	 *            recordType of interest
	 * @return list of records
	 */
	public List<AbstractRecord> readFromDirectory(String dir, Class<? extends AbstractRecord> recordType) {
		try {
			LpeStringUtils.correctFileSeparator(dir);
			if (!dir.endsWith(System.getProperty("file.separator"))) {
				dir = dir + System.getProperty("file.separator");
			}
			Map<String, Class<? extends AbstractRecord>> typeMapping = loadFileTypeMapping(dir);
			String targetFile = null;
			for (String file : typeMapping.keySet()) {
				if (typeMapping.get(file).getName().equals(recordType.getName())) {
					targetFile = file;
				}
			}
			List<AbstractRecord> records = new ArrayList<AbstractRecord>();

			readRecordsFromFile(dir + targetFile + RecordCSVWriter.CSV_FILE_EXTENSION, recordType, records);

			return records;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reads all records from a directory and its subdirectories.
	 * 
	 * @param dir
	 *            target directory
	 * @return wrapped measurement data
	 */
	public DatasetCollection readDatasetCollectionFromDirectory(String dir) {


		DatasetCollectionBuilder dscBuilder = new DatasetCollectionBuilder();
		try {
			readDataSetsFromDirectory(dir, dscBuilder);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return dscBuilder.build();

	}

	private void readDataSetsFromDirectory(String dir, DatasetCollectionBuilder dscBuilder)
			throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
		dir = LpeStringUtils.correctFileSeparator(dir);
		if (!dir.endsWith(System.getProperty("file.separator"))) {
			dir = dir + System.getProperty("file.separator");
		}
		if (hasTypeInfoFile(dir)) {
			Map<String, Class<? extends AbstractRecord>> typeMapping = loadFileTypeMapping(dir);

			for (String file : typeMapping.keySet()) {
				Dataset dataset = readDatasetFromFile(dir + file + RecordCSVWriter.CSV_FILE_EXTENSION,
						typeMapping.get(file));
				dscBuilder.addDataSet(dataset);
			}
		}

		File directory = new File(dir);
		for (File child : directory.listFiles()) {
			if (child.isDirectory()) {
				readDataSetsFromDirectory(child.getAbsolutePath(), dscBuilder);
			}

		}

	}

	private boolean hasTypeInfoFile(String dir) {
		File file = new File(dir + RecordCSVWriter.RECORD_TYPE_INFO_FILE + RecordCSVWriter.CSV_FILE_EXTENSION);
		return file.exists();
	}

	@SuppressWarnings("unchecked")
	private Map<String, Class<? extends AbstractRecord>> loadFileTypeMapping(String dir) throws IOException,
			ClassNotFoundException {
		FileReader fReader = null;
		CSVReader csvReader = null;
		try {
			String typeInfoFile = dir + RecordCSVWriter.RECORD_TYPE_INFO_FILE + RecordCSVWriter.CSV_FILE_EXTENSION;
			fReader = new FileReader(typeInfoFile);

			csvReader = new CSVReader(fReader, RecordCSVWriter.VALUE_SEPARATOR);

			Map<String, Class<? extends AbstractRecord>> mapping = new HashMap<String, Class<? extends AbstractRecord>>();
			String[] line = csvReader.readNext();

			while (line != null) {

				String fileName = line[RecordCSVWriter.TYPE_INFO_CSV_FILE_INDEX];
				Class<? extends AbstractRecord> recordType = (Class<? extends AbstractRecord>) Class
						.forName(line[RecordCSVWriter.TYPE_INFO_CSV_TYPE_INDEX]);
				mapping.put(fileName, recordType);

				line = csvReader.readNext();
			}
			return mapping;
		} finally {
			if (csvReader != null) {
				csvReader.close();
			}
			if (fReader != null) {
				fReader.close();
			}
		}
	}

	private void readRecordsFromFile(String csvFile, Class<? extends AbstractRecord> recordType,
			List<AbstractRecord> records) throws InstantiationException, IllegalAccessException, IOException {
		FileReader fReader = null;
		CSVReader csvReader = null;
		try {
			fReader = new FileReader(csvFile);

			csvReader = new CSVReader(fReader, RecordCSVWriter.VALUE_SEPARATOR);

			String[] line = csvReader.readNext();
			// skip header
			csvReader.readNext();

			while (line != null) {
				AbstractRecord record = recordType.newInstance();

				record.fromStringArray(line);

				if (record != null) {
					records.add(record);
				}

				line = csvReader.readNext();
			}
		} finally {
			if (csvReader != null) {
				csvReader.close();
			}
			if (fReader != null) {
				fReader.close();
			}
		}

	}

	private Dataset readDatasetFromFile(String csvFile, Class<? extends AbstractRecord> recordType)
			throws InstantiationException, IllegalAccessException, IOException {
		FileReader fReader = null;
		CSVReader csvReader = null;
		try {
			fReader = new FileReader(csvFile);

			csvReader = new CSVReader(fReader, RecordCSVWriter.VALUE_SEPARATOR);

			String[] header = csvReader.readNext();
			String[] types = csvReader.readNext();
			String[] line = csvReader.readNext();
			DatasetBuilder dsBuilder = new DatasetBuilder(recordType);
			AbstractRecord templateRec = recordType.newInstance();
			int numParameters = templateRec.getAllParameterFields().length;

			while (line != null) {
				AbstractRecord record = recordType.newInstance();
				record.fromStringArray(line);

				dsBuilder.addRecord(record, getAdditionalParameters(recordType, header, types, line, numParameters));
				line = csvReader.readNext();
			}

			return dsBuilder.build();
		} finally {
			if (csvReader != null) {
				csvReader.close();
			}
			if (fReader != null) {
				fReader.close();
			}
		}
	}

	private Set<Parameter> getAdditionalParameters(Class<? extends AbstractRecord> recordType, String[] header,
			String[] types, String[] line, int numParameters) {
		Set<Parameter> parameters = new TreeSet<>();
		for (int i = numParameters; i < line.length; i++) {
			parameters.add(new Parameter(header[i], LpeSupportedTypes.getValueOfType(line[i],
					LpeSupportedTypes.get(types[i]))));
		}
		return parameters;
	}
}
