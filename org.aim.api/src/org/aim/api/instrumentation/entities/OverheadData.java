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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Container for overhead data.
 * 
 * @author Alexander Wert
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OverheadData {
	private List<OverheadRecord> oRecords;

	/**
	 * @return the oRecords
	 */
	public List<OverheadRecord> getoRecords() {
		return oRecords;
	}

	/**
	 * @param oRecords
	 *            the oRecords to set
	 */
	public void setoRecords(List<OverheadRecord> oRecords) {
		this.oRecords = oRecords;
	}

	/**
	 * Returns the overhead mean of the before part of a probe.
	 * 
	 * @return mean overhead of the before part
	 */
	@JsonIgnore
	public double getMeanBefore() {
		double mean = 0.0;
		for (OverheadRecord rec : oRecords) {
			mean += ((double) rec.getBeforeNanoTimeSpan()) / ((double) oRecords.size());
		}
		return mean;
	}

	/**
	 * Returns the overhead mean of the after part of a probe.
	 * 
	 * @return mean overhead of the after part
	 */
	@JsonIgnore
	public double getMeanAfter() {
		double mean = 0.0;
		for (OverheadRecord rec : oRecords) {
			mean += ((double) rec.getAfterNanoTimeSpan()) / ((double) oRecords.size());
		}
		return mean;
	}

	/**
	 * Returns the overhead mean of the probe.
	 * 
	 * @return mean overhead
	 */
	@JsonIgnore
	public double getMeanOverall() {
		double mean = 0.0;
		for (OverheadRecord rec : oRecords) {
			mean += ((double) rec.getOverallNanoTimeSpan()) / ((double) oRecords.size());
		}
		return mean;
	}

	@Override
	public String toString() {
		String s = "";
		for (OverheadRecord rec : oRecords) {
			s += rec.toString() + "\n";
		}
		return s;
	}
}
