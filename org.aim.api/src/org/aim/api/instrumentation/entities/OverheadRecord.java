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
package org.aim.api.instrumentation.entities;

/**
 * Record containing the overhead data.
 * 
 * @author Alexander Wert
 * 
 */
public class OverheadRecord {
	private long overallNanoTimeSpan;
	private long beforeNanoTimeSpan;
	private long afterNanoTimeSpan;

	/**
	 * @return the overallNanoTimeSpan
	 */
	public long getOverallNanoTimeSpan() {
		return overallNanoTimeSpan;
	}

	/**
	 * @param overallNanoTimeSpan
	 *            the overallNanoTimeSpan to set
	 */
	public void setOverallNanoTimeSpan(long overallNanoTimeSpan) {
		this.overallNanoTimeSpan = overallNanoTimeSpan;
	}

	/**
	 * @return the beforeNanoTimeSpan
	 */
	public long getBeforeNanoTimeSpan() {
		return beforeNanoTimeSpan;
	}

	/**
	 * @param beforeNanoTimeSpan
	 *            the beforeNanoTimeSpan to set
	 */
	public void setBeforeNanoTimeSpan(long beforeNanoTimeSpan) {
		this.beforeNanoTimeSpan = beforeNanoTimeSpan;
	}

	/**
	 * @return the afterNanoTimeSpan
	 */
	public long getAfterNanoTimeSpan() {
		return afterNanoTimeSpan;
	}

	/**
	 * @param afterNanoTimeSpan
	 *            the afterNanoTimeSpan to set
	 */
	public void setAfterNanoTimeSpan(long afterNanoTimeSpan) {
		this.afterNanoTimeSpan = afterNanoTimeSpan;
	}

	@Override
	public String toString() {
		return getOverallNanoTimeSpan() + " \t|  " + getBeforeNanoTimeSpan() + " \t|  " + getAfterNanoTimeSpan();
	}

}
