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
package org.aim.artifacts.scopes;

import org.aim.api.instrumentation.AbstractInstAPIScope;
import org.lpe.common.extension.IExtension;

/**
 * The JDBC scope comprises all java.sql and javax.sql API methods that retrieve
 * connections or execute database operations.
 * 
 * @author Alexander Wert
 * 
 */
public class JDBCScope extends AbstractInstAPIScope {
	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            extension provider
	 */
	public JDBCScope(IExtension<?> provider) {
		super(provider);
	}

	@Override
	protected void init() {
		// java.sql entities

		addMethod("java.sql.Statement", "execute(java.lang.String)");
		addMethod("java.sql.Statement", "execute(java.lang.String,int)");
		addMethod("java.sql.Statement", "execute(java.lang.String,int[])");
		addMethod("java.sql.Statement", "execute(java.lang.String,java.lang.String[])");
		addMethod("java.sql.Statement", "executeBatch()");
		addMethod("java.sql.Statement", "executeQuery(java.lang.String)");
		addMethod("java.sql.Statement", "executeUpdate(java.lang.String)");
		addMethod("java.sql.Statement", "executeUpdate(java.lang.String,int)");
		addMethod("java.sql.Statement", "executeUpdate(java.lang.String,int[])");
		addMethod("java.sql.Statement", "executeUpdate(java.lang.String,java.lang.String[])");
		addMethod("java.sql.Statement", "getResultSet()");
		addMethod("java.sql.Statement", "getGeneratedKeys()");

		addMethod("java.sql.PreparedStatement", "execute()");
		addMethod("java.sql.PreparedStatement", "executeQuery()");
		addMethod("java.sql.PreparedStatement", "executeUpdate()");

		addMethod("java.sql.Driver", "connect()");

		addMethod("java.sql.DriverManager", "getConnection(java.lang.String)");
		addMethod("java.sql.DriverManager", "getConnection(java.lang.String,java.util.Properties)");
		addMethod("java.sql.DriverManager", "getConnection(java.lang.String,java.lang.String,java.lang.String)");
		addMethod("java.sql.DriverManager", "getDriver(java.lang.String)");
		addMethod("java.sql.DriverManager", "getDrivers()");

		addMethod("java.sql.Connection", "commit()");
		addMethod("java.sql.Connection", "close()");
		addMethod("java.sql.Connection", "prepareStatement(java.lang.String)");

		// javax.sql entities
		addMethod("javax.sql.ConnectionPoolDataSource", "getPooledConnection()");
		addMethod("javax.sql.ConnectionPoolDataSource", "getPooledConnection(java.lang.String,java.lang.String)");

		addMethod("javax.sql.DataSource", "getConnection()");
		addMethod("javax.sql.DataSource", "getConnection(java.lang.String,java.lang.String)");

		addMethod("javax.sql.PooledConnection", "getConnection()");
		addMethod("javax.sql.PooledConnection", "close()");

		addMethod("javax.sql.XADataSource", "getXAConnection()");
		addMethod("javax.sql.XADataSource", "getXAConnection(java.lang.String,java.lang.String)");
	}
}
