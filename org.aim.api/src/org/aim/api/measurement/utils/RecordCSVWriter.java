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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.aim.api.measurement.AbstractRecord;
import org.aim.api.measurement.MeasurementData;
import org.aim.api.measurement.dataset.Dataset;
import org.aim.api.measurement.dataset.DatasetCollection;
import org.aim.api.measurement.dataset.DatasetRow;
import org.aim.api.measurement.dataset.Parameter;
import org.lpe.common.util.LpeFileUtils;
import org.lpe.common.util.LpeStringUtils;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * CSV exporter for measurement data.
 * 
 * @author Alexander Wert
 * 
 */
public final class RecordCSVWriter {
	public static final String CSV_FILE_EXTENSION = ".csv";
	public static final String RECORD_TYPE_INFO_FILE = "RecordTypeInfo";
	public static final char VALUE_SEPARATOR = ';';
	public static final int TYPE_INFO_CSV_FILE_INDEX = 0;
	public static final int TYPE_INFO_CSV_TYPE_INDEX = 1;
	private static RecordCSVWriter instance;

	/**
	 * 
	 * @return singleton instance
	 */
	public static RecordCSVWriter getInstance() {
		if (instance == null) {
			instance = new RecordCSVWriter();
		}
		return instance;
	}

	/**
	 * Private constructor due to singleton class.
	 */
	private RecordCSVWriter() {

	}

	/**
	 * Writes the given list of records to a set of CSV files in the specified
	 * target directory.
	 * 
	 * @param data
	 *            measurement data containing a list of records to persist
	 * @param targetDir
	 *            target directory
	 * @param useTimestampAsSubdir
	 *            if true, than the current timestamp is used as sub directory
	 * @throws IOException
	 *             if export fails
	 */
	public void writeDataToDir(MeasurementData data, String targetDir, boolean useTimestampAsSubdir) throws IOException {
		targetDir = preProcessTargetDir(targetDir, useTimestampAsSubdir);

		Set<Class<? extends AbstractRecord>> recordTypes = data.getDifferentRecordTypes();
		for (Class<? extends AbstractRecord> recordType : recordTypes) {
			String fileName = addRecordTypeInfo(recordType, targetDir + RECORD_TYPE_INFO_FILE + CSV_FILE_EXTENSION);
			List<? extends AbstractRecord> recs = data.getRecords(recordType);
			writeRecordsToFile(recs, targetDir + fileName + CSV_FILE_EXTENSION);
		}
	}

	/**
	 * Writers records to file.
	 * 
	 * @param records
	 *            records to store
	 * @param targetFile
	 *            target file
	 * @throws IOException
	 *             if writing fails
	 */
	public void writeRecordsToFile(List<? extends AbstractRecord> records, String targetFile) throws IOException {
		FileWriter fWriter = null;
		CSVWriter csvWriter = null;
		try {
			fWriter = new FileWriter(targetFile);
			csvWriter = new CSVWriter(fWriter, VALUE_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);

			boolean headerSet = false;
			for (AbstractRecord record : records) {
				if (!headerSet) {
					String[] headers = record.getAllParameterNames().toArray(new String[0]);
					csvWriter.writeNext(headers);
					headerSet = true;
				}
				String[] strArray = record.toStringArray();
				csvWriter.writeNext(strArray);
			}
			csvWriter.flush();
		} finally {
			if (csvWriter != null) {
				csvWriter.close();
			}
			if (fWriter != null) {
				fWriter.close();
			}
		}

	}

	/**
	 * Pipes input stream to the target directory.
	 * 
	 * @param inStream
	 *            input stream to read data records from
	 * @param targetDir
	 *            target directory where to write the dataset files
	 * @param parameters
	 *            additional parameters
	 * @throws IOException
	 *             if something goes wrong
	 */
	public void pipeDataToDatasetFiles(InputStream inStream, String targetDir, Set<Parameter> parameters)
			throws IOException {
		targetDir = preProcessTargetDir(targetDir, false);
		Map<Integer, CSVWriter> csvWriters = new HashMap<Integer, CSVWriter>();
		try {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
			String line = bReader.readLine();
			while (line != null) {
				AbstractRecord record = AbstractRecord.fromString(line);
				if (record != null) {
					CSVWriter csvWriter = null;
					int structureHash = getStructureHash(record, parameters);
					if (!csvWriters.containsKey(structureHash)) {
						String fileName = targetDir + getFileName(targetDir, record) + CSV_FILE_EXTENSION;
						FileWriter fWriter = new FileWriter(fileName);
						csvWriter = new CSVWriter(fWriter, VALUE_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
						csvWriter.writeNext(DatasetRow.getHeader(record, parameters));
						csvWriter.writeNext(DatasetRow.getTypes(record, parameters));
						csvWriters.put(structureHash, csvWriter);
					}
					csvWriters.get(structureHash).writeNext(DatasetRow.getValueArray(record, parameters));
				}

				line = bReader.readLine();
			}
		} finally {
			for (CSVWriter csvWriter : csvWriters.values()) {
				if (csvWriter != null) {
					csvWriter.close();
				}
			}
		}
	}

	private String getFileName(String targetDir, AbstractRecord record) throws IOException {
		int i = -1;
		File file = null;
		do {
			i++;
			file = new File(targetDir + record.getClass().getSimpleName() + "_" + String.valueOf(i)
					+ CSV_FILE_EXTENSION);

		} while (file.exists());
		String fileName = addRecordTypeInfo(record.getClass(), targetDir + RECORD_TYPE_INFO_FILE + CSV_FILE_EXTENSION,
				String.valueOf(i));
		return fileName;
	}

	private Integer getStructureHash(AbstractRecord record, Set<Parameter> parameters) {
		Set<String> fixParameterNames = new TreeSet<>();
		for (Parameter par : parameters) {
			fixParameterNames.add(par.getName());
		}
		fixParameterNames.addAll(record.getNonMetricParameterNames());

		return fixParameterNames.hashCode() + record.getClass().hashCode();
	}

	/**
	 * Writes wrapped data to directory.
	 * 
	 * @param datasetCollection
	 *            datasets to store
	 * @param targetDir
	 *            target directory
	 * @throws IOException
	 *             if storing fails
	 */
	public void writedDatasetCollectionToDir(DatasetCollection datasetCollection, String targetDir) throws IOException {
		targetDir = preProcessTargetDir(targetDir, false);

		Set<Class<? extends AbstractRecord>> recordTypes = datasetCollection.getDifferentRecordTypes();
		for (Class<? extends AbstractRecord> recordType : recordTypes) {
			int i = 0;
			for (Dataset dataset : datasetCollection.getDataSets(recordType)) {
				String fileName = addRecordTypeInfo(recordType, targetDir + RECORD_TYPE_INFO_FILE + CSV_FILE_EXTENSION,
						String.valueOf(i));
				writeDataSetToFile(dataset, targetDir + fileName + CSV_FILE_EXTENSION);
				i++;
			}

		}
	}

	private void writeDataSetToFile(Dataset dataset, String targetFile) throws IOException {
		String[] csvHeader = dataset.getHeader();
		if (csvHeader == null) {
			return;
		}

		FileWriter fWriter = null;
		CSVWriter csvWriter = null;
		try {
			fWriter = new FileWriter(targetFile);
			csvWriter = new CSVWriter(fWriter, VALUE_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);

			csvWriter.writeNext(csvHeader);
			csvWriter.writeNext(dataset.getTypes());

			for (String[] values : dataset) {
				csvWriter.writeNext(values);
			}

			csvWriter.flush();
		} finally {
			if (csvWriter != null) {
				csvWriter.close();
			}
			if (fWriter != null) {
				fWriter.close();
			}
		}

	}

	/**
	 * appends a file separator if needed and creates directory
	 */
	private String preProcessTargetDir(String targetDir, boolean useTimestampAsSubdir) {
		targetDir = LpeStringUtils.correctFileSeparator(targetDir);
		if (!targetDir.endsWith(System.getProperty("file.separator"))) {
			targetDir = targetDir + System.getProperty("file.separator");
		}
		if (useTimestampAsSubdir) {
			String subDir = LpeStringUtils.getDetailedTimeStamp(new Date());
			subDir = subDir.replace(" - ", "_");
			subDir = subDir.replace(".", "-");
			subDir = subDir.replace(":", "-");
			targetDir = targetDir + subDir + System.getProperty("file.separator");
		}

		LpeFileUtils.createDir(targetDir);

		return targetDir;
	}

	private String addRecordTypeInfo(Class<? extends AbstractRecord> type, String targetFile, String filePostfix)
			throws IOException {
		FileWriter fWriter = null;
		CSVWriter csvWriter = null;
		try {
			fWriter = new FileWriter(targetFile, true);
			csvWriter = new CSVWriter(fWriter, VALUE_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);

			String[] strArray = new String[2];
			strArray[TYPE_INFO_CSV_FILE_INDEX] = type.getSimpleName()
					+ ((filePostfix == null || filePostfix.isEmpty()) ? "" : ("_" + filePostfix));
			strArray[TYPE_INFO_CSV_TYPE_INDEX] = type.getName();
			csvWriter.writeNext(strArray);

			csvWriter.flush();
			return strArray[TYPE_INFO_CSV_FILE_INDEX];
		} finally {
			if (csvWriter != null) {
				csvWriter.close();
			}
			if (fWriter != null) {
				fWriter.close();
			}
		}
	}

	private String addRecordTypeInfo(Class<? extends AbstractRecord> type, String targetFile) throws IOException {
		return addRecordTypeInfo(type, targetFile, null);
	}

}
