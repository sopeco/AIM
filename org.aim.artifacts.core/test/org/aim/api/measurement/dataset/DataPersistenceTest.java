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
package org.aim.api.measurement.dataset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.aim.api.measurement.AbstractRecord;
import org.aim.api.measurement.utils.RecordCSVReader;
import org.aim.api.measurement.utils.RecordCSVWriter;
import org.junit.Assert;
import org.junit.Test;
import org.lpe.common.util.LpeFileUtils;
import org.lpe.common.util.system.LpeSystemUtils;

public class DataPersistenceTest {

	@Test
	public void testDatasetCollectionStoreAndLoad() throws IOException {
		DatasetCollection dsCollection = SmallDatasetCollection.createDataset();

		File dir = new File("tempJUnit");
		if (dir.exists()) {
			LpeFileUtils.removeDir(dir.getAbsolutePath());
		}
		LpeFileUtils.createDir(dir.getAbsolutePath());

		RecordCSVWriter.getInstance().writedDatasetCollectionToDir(dsCollection, dir.getAbsolutePath());
		DatasetCollection loadedData = RecordCSVReader.getInstance().readDatasetCollectionFromDirectory(
				dir.getAbsolutePath());

		Assert.assertEquals(dsCollection.size(), loadedData.size());
		Assert.assertEquals(dsCollection.getDataSets().size(), loadedData.getDataSets().size());
		Assert.assertEquals(dsCollection.getDifferentRecordTypes(), dsCollection.getDifferentRecordTypes());

		LpeFileUtils.removeDir(dir.getAbsolutePath());
	}

	@Test
	public void testDatasetStorePipe() throws IOException {
		final DatasetCollection dsCollection = LargeDatasetCollection.createDataset();

		File dir = new File("tempJUnit");
		if (dir.exists()) {
			LpeFileUtils.removeDir(dir.getAbsolutePath());
		}
		LpeFileUtils.createDir(dir.getAbsolutePath());

		final PipedOutputStream outStream = new PipedOutputStream();
		final PipedInputStream inStream = new PipedInputStream(outStream);
		final BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(outStream));
		LpeSystemUtils.submitTask(new Runnable() {

			@Override
			public void run() {
				try {
					for (AbstractRecord record : dsCollection.getRecords()) {

						bWriter.write(record.toString());
						bWriter.newLine();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (bWriter != null) {
						try {
							bWriter.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}
		});

		RecordCSVWriter.getInstance().pipeDataToDatasetFiles(inStream, dir.getAbsolutePath(),
				LargeDatasetCollection.additionalParameters);

		DatasetCollection loadedData = RecordCSVReader.getInstance().readDatasetCollectionFromDirectory(
				dir.getAbsolutePath());

		Assert.assertEquals(dsCollection.size(), loadedData.size());
		Assert.assertEquals(dsCollection.getDataSets().size(), loadedData.getDataSets().size());
		Assert.assertEquals(dsCollection.getDifferentRecordTypes(), dsCollection.getDifferentRecordTypes());

		LpeFileUtils.removeDir(dir.getAbsolutePath());
	}

}
