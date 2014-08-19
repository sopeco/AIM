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

import org.aim.api.instrumentation.entities.OverheadRecord;

/**
 * Target class on which overhead is measured.
 * 
 * @author Alexander Wert
 * 
 */
public class OverheadTargetClass {

	long start = 0L;
	long intermediate = 0L;
	long end = 0L;

	/**
	 * Emulates a method call.
	 * 
	 * @return overhead record
	 */
	public synchronized OverheadRecord caller() {

		start = System.nanoTime();
		called();
		end = System.nanoTime();
		OverheadRecord oRecord = new OverheadRecord();
		oRecord.setBeforeNanoTimeSpan(intermediate - start);
		oRecord.setAfterNanoTimeSpan(end - intermediate);
		oRecord.setOverallNanoTimeSpan(end - start);
		return oRecord;
	}

	private void called() {
		intermediate = System.nanoTime();
	}
}
