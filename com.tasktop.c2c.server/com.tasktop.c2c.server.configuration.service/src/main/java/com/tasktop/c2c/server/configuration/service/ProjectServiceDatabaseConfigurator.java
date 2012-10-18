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

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

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
public class ProjectServiceDatabaseConfigurator implements Configurator, ResourceLoaderAware {

	private DatabaseNamingStrategy databaseNamingStrategy;

	private String changelog;

	private String changelogContexts;

	private DataSource rawDatasource;

	private DataSource tenantAwareDataSource;

	private ResourceLoader resourceLoader;

	private boolean needsDatabaseCreation = true;

	private void initializeNewProject(ProjectServiceConfiguration config) throws SQLException {
		String dbName = databaseNamingStrategy.getCurrentTenantDatabaseName();

		String dbType;
		if (needsDatabaseCreation) {
			dbType = createDatabase(dbName);
		} else {
			dbType = rawDatasource.getConnection().getMetaData().getDatabaseProductName();
		}

		installSchema(dbType, dbName);
	}

	private String createDatabase(String dbName) throws SQLException {
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
			return dbType;
		} finally {
			dbConnection.close();
		}
	}

	private void installSchema(String dbType, String dbName) {
		try {
			SpringLiquibase schemaInstaller = new SpringLiquibase();
			schemaInstaller.setResourceLoader(resourceLoader);
			schemaInstaller.setDataSource(tenantAwareDataSource);
			schemaInstaller.setChangeLog(changelog);
			schemaInstaller.setContexts(changelogContexts);
			if (dbType.toUpperCase().startsWith("HSQL") || dbType.toUpperCase().startsWith("ORACLE")) {
				// HSQLDB will create the DB in upper case even if we specify lower case in the escaped create schema
				// statement
				schemaInstaller.setDefaultSchema(dbName.toUpperCase());
			} else {
				schemaInstaller.setDefaultSchema(dbName);
			}
			schemaInstaller.afterPropertiesSet();
		} catch (LiquibaseException e) {
			throw new RuntimeException(e);
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
		try {
			initializeNewProject(configuration);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param changelog
	 *            the changelog to use for schema creation
	 */
	public void setChangelog(String changelog) {
		this.changelog = changelog;
	}

	/**
	 * @param changelogContexts
	 *            the changelogContexts to set
	 */
	public void setChangelogContexts(String changelogContexts) {
		this.changelogContexts = changelogContexts;
	}

	/**
	 * @param rawDatasource
	 *            the rawDatasource to set
	 */
	public void setRawDatasource(DataSource rawDatasource) {
		this.rawDatasource = rawDatasource;
	}

	/**
	 * @param tenantAwareDataSource
	 *            the tenantAwareDataSource to set
	 */
	public void setTenantAwareDataSource(DataSource tenantAwareDataSource) {
		this.tenantAwareDataSource = tenantAwareDataSource;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public void setDatabaseNamingStrategy(DatabaseNamingStrategy databaseNamingStrategy) {
		this.databaseNamingStrategy = databaseNamingStrategy;
	}

	public void setNeedsDatabaseCreation(boolean needsDatabaseCreation) {
		this.needsDatabaseCreation = needsDatabaseCreation;
	}

}
