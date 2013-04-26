/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies
 * Copyright (c) 2010, 2011 SpringSource, a division of VMware
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 ******************************************************************************/
package com.tasktop.c2c.server.configuration.service;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import javax.sql.DataSource;

import com.tasktop.c2c.server.common.internal.tenancy.DatabaseNamingStrategy;
import com.tasktop.c2c.server.configuration.service.ProjectServiceManagementServiceBean.Configurator;

/**
 * Used to configure a new database for a service for a project. Will create a database named from the projectIdentifier
 * plus a suffix, and then run Liquibase for schema creation
 * 
 * @author Clint Morgan <clint.morgan@tasktop.com> (Tasktop Technologies Inc.)
 * @author Jennifer Hickey
 * 
 */
public class ProjectServiceDatabaseConfigurator implements Configurator {

	private DatabaseNamingStrategy databaseNamingStrategy;

	private DataSource rawDatasource;

	private boolean needsDatabaseCreation = true;

	private void initializeNewProject(ProjectServiceConfiguration config) {
		String dbName = databaseNamingStrategy.getCurrentTenantDatabaseName();

		if (needsDatabaseCreation) {
			try {
				createDatabase(dbName);
			} catch (SQLException e) {
				if (shouldIgnore(e)) {
					return;
				}
				throw new RuntimeException(e);
			}
		}
	}

	private boolean shouldIgnore(SQLException e) {
		// Mysql
		if (e.getMessage().contains("database exists")) {
			return true;
		}
		// OracleDB
		if (e.getMessage().contains("ORA-01920")) {
			return true;
		}

		return false;
	}

	private void createDatabase(String dbName) throws SQLException {
		java.sql.Connection dbConnection = rawDatasource.getConnection();
		try {
			Statement s = dbConnection.createStatement();
			String dbType = dbConnection.getMetaData().getDatabaseProductName();
			String createStmt = "create database `" + dbName + "`";
			if (dbType.toUpperCase().startsWith("HSQL")) {
				createStmt = "create schema " + dbName;
			} else if (dbType.toUpperCase().startsWith("ORACLE")) {
				dbName = dbName.toUpperCase();
				createStmt = "create user \"" + dbName + "\" identified by \""
						+ UUID.randomUUID().toString().substring(0, 7) + "\" account lock";
				s.execute(createStmt);
				createStmt = "grant unlimited tablespace to \"" + dbName + "\"";
			}
			s.execute(createStmt);

			if (!dbConnection.getAutoCommit()) {
				dbConnection.commit();
			}
		} finally {
			dbConnection.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tasktop.c2c.server.configuration.service.NodeConfigurationServiceBean.Configurator#configure(org.
	 * cloudfoundry.code.server.configuration.service.NodeConfigurationService.NodeConfiguration)
	 */
	@Override
	public void configure(ProjectServiceConfiguration configuration) {
		initializeNewProject(configuration);
	}

	/**
	 * @param rawDatasource
	 *            the rawDatasource to set
	 */
	public void setRawDatasource(DataSource rawDatasource) {
		this.rawDatasource = rawDatasource;
	}

	public void setDatabaseNamingStrategy(DatabaseNamingStrategy databaseNamingStrategy) {
		this.databaseNamingStrategy = databaseNamingStrategy;
	}

	public void setNeedsDatabaseCreation(boolean needsDatabaseCreation) {
		this.needsDatabaseCreation = needsDatabaseCreation;
	}

}
