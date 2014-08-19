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
package org.aim.artifacts.probes.utils;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

import org.aim.api.instrumentation.IInstrumentationUtil;
import org.aim.api.instrumentation.InstrumentationUtilsController;

/**
 * Cache for prepared statements.
 * 
 * @author Alexander Wert
 * 
 */
public final class SQLPreparedStatementCache implements IInstrumentationUtil {
	private static SQLPreparedStatementCache instance;

	/**
	 * 
	 * @return singleton instance of the cache
	 */
	public static SQLPreparedStatementCache getInstance() {
		if (instance == null) {
			instance = new SQLPreparedStatementCache();
		}
		return instance;
	}

	private Map<PreparedStatement, String> queries;

	private SQLPreparedStatementCache() {
		queries = new HashMap<PreparedStatement, String>();
		InstrumentationUtilsController.getInstance().register(this);
	}

	/**
	 * Registers a prepared statement.
	 * 
	 * @param stmt
	 *            statements to register
	 * @param query
	 *            query to executeF
	 */
	public void register(PreparedStatement stmt, String query) {
		queries.put(stmt, query);
	}

	/**
	 * Returns the query for the given prepared statement.
	 * 
	 * @param stmt
	 *            statement for which the query should be returned.
	 * @return SQL queryF
	 */
	public String getQuery(PreparedStatement stmt) {
		return queries.get(stmt);
	}

	@Override
	public void clear() {
		queries.clear();
	}

}
