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
package org.overhead;

import java.util.ArrayList;
import java.util.List;

import org.aim.api.exceptions.InstrumentationException;
import org.aim.api.instrumentation.entities.OverheadData;
import org.aim.api.instrumentation.entities.OverheadRecord;
import org.aim.description.InstrumentationDescription;
import org.aim.description.builder.InstrumentationDescriptionBuilder;
import org.aim.mainagent.AdaptiveInstrumentationFacade;

/**
 * Estimates the measurement overhead of probes.
 * 
 * @author Alexander Wert
 * 
 */
public final class OverheadEstimator {

	private static final int NUM_CALLS_FOR_WARM_UP = 10;
	private static final int NUM_CALLS = 1010;
	private static final int REPETITIONS = 100;

	private OverheadEstimator() {
	}

	/**
	 * Measures the overhead of the probe specified by its name.
	 * 
	 * @param probeTypeName
	 *            probe to check
	 * @return a list of overhead records.
	 * @throws InstrumentationException
	 *             if instrumentation cannot be done
	 */
	public static List<OverheadRecord> measureOverhead(String probeTypeName) throws InstrumentationException {

		InstrumentationDescriptionBuilder idBuilder = new InstrumentationDescriptionBuilder();
		idBuilder.newMethodScopeEntity(OverheadTargetClass.class.getName() + ".called()").addProbe(probeTypeName)
				.entityDone();

		List<OverheadRecord> records = new ArrayList<>();

		for (int i = 0; i < REPETITIONS; i++) {
			records.add(runExperiment(idBuilder.build()));
		}

		return records;
	}

	/**
	 * Executes one experiment.
	 * 
	 * @param instDescr
	 *            instrumentaiton description
	 * @return an overhead record
	 * @throws InstrumentationException
	 *             if instrumentation fails
	 */
	public static OverheadRecord runExperiment(InstrumentationDescription instDescr) throws InstrumentationException {
		List<OverheadRecord> records = new ArrayList<>();
		AdaptiveInstrumentationFacade.getInstance().instrument(instDescr);

		OverheadTargetClass target = new OverheadTargetClass();
		for (int i = 0; i < NUM_CALLS; i++) {
			OverheadRecord rec = target.caller();
			if (i > NUM_CALLS_FOR_WARM_UP) {
				records.add(rec);
			}

		}

		AdaptiveInstrumentationFacade.getInstance().undoInstrumentation();

		OverheadRecord record = new OverheadRecord();
		OverheadData data = new OverheadData();
		data.setoRecords(records);
		record.setOverallNanoTimeSpan((long) data.getMeanOverall());
		record.setBeforeNanoTimeSpan((long) data.getMeanBefore());
		record.setAfterNanoTimeSpan((long) data.getMeanAfter());
		return record;
	}

}
