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
package org.aim.artifacts.records;

import org.aim.api.measurement.AbstractRecord;
import org.aim.api.measurement.RecordValue;

/**
 * Record for capturing SQL query invocations.
 * 
 * @author Alexander Wert
 * 
 */
public class SQLQueryRecord extends AbstractRecord {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String PAR_QUERY_STRING = "queryString";

	@RecordValue(metric = true, name = PAR_QUERY_STRING)
	String queryString;

	/**
	 * Constructor.
	 */
	public SQLQueryRecord() {
		super();
	}

	/**
	 * @return the queryString
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * @param queryString
	 *            the queryString to set
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

}
