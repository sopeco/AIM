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
package org.aim.artifacts.probes;

import java.sql.PreparedStatement;

import org.aim.api.instrumentation.AbstractEnclosingProbe;
import org.aim.api.instrumentation.ProbeAfterPart;
import org.aim.api.instrumentation.ProbeBeforePart;
import org.aim.api.instrumentation.ProbeVariable;
import org.aim.artifacts.probes.utils.SQLPreparedStatementCache;
import org.aim.artifacts.records.SQLQueryRecord;
import org.aim.description.probes.MeasurementProbe;
import org.aim.description.scopes.MethodsEnclosingScope;
import org.lpe.common.extension.IExtension;

/**
 * Gathers the query string of a JDBC request.
 * 
 * @author Alexander Wert
 * 
 */
public class SQLQueryProbe extends AbstractEnclosingProbe {
	public static final MeasurementProbe<MethodsEnclosingScope> MODEL_PROBE = new MeasurementProbe<>(
			SQLQueryProbe.class.getName());

	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider.
	 */
	public SQLQueryProbe(IExtension<?> provider) {
		super(provider);
	}

	@ProbeVariable
	public SQLQueryRecord _SQLQueryProbe_record;

	@ProbeVariable
	public String _SQLQueryProbe_query;

	/**
	 * Before part for JDBC execute(String query, ...) methods.
	 */
	@ProbeBeforePart(requiredMethodName = { "execute(java.lang.String", "executeQuery(java.lang.String",
			"executeUpdate(java.lang.String" })
	public void beforePart() {
		_SQLQueryProbe_record = new SQLQueryRecord();
		_SQLQueryProbe_record.setTimeStamp(_GenericProbe_startTime);
		_SQLQueryProbe_record.setCallId(_GenericProbe_callId);
		_SQLQueryProbe_record.setQueryString((String) __parameter[1]);
		_GenericProbe_collector.newRecord(_SQLQueryProbe_record);
	}

	/**
	 * Before part for JDBC execute() methods.
	 */
	@ProbeBeforePart(requiredMethodName = { "execute()", "executeQuery()", "executeUpdate()" })
	public void beforePartForPreparedStatement() {
		_SQLQueryProbe_record = new SQLQueryRecord();
		_SQLQueryProbe_record.setTimeStamp(_GenericProbe_startTime);
		_SQLQueryProbe_record.setCallId(_GenericProbe_callId);
		_SQLQueryProbe_record.setQueryString(SQLPreparedStatementCache.getInstance().getQuery(
				(PreparedStatement) __parameter[0]));
		_GenericProbe_collector.newRecord(_SQLQueryProbe_record);
	}

	/**
	 * After part for JDBC prepareStatement methods.
	 */
	@ProbeAfterPart(requiredMethodName = "prepareStatement(java.lang.String)")
	public void afterPartForPrepareStatement() {
		SQLPreparedStatementCache.getInstance().register((PreparedStatement) __returnObject, (String) __parameter[1]);
	}
}
