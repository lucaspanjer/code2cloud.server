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
package com.tasktop.c2c.server.common.internal.tenancy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import liquibase.exception.LiquibaseException;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.tenancy.datasource.TenantAwareDataSource;

public class ProjectTenantAwareDataSource extends TenantAwareDataSource implements ResourceLoaderAware {

	private DatabaseNamingStrategy databaseNamingStrategy;

	private Set<String> updatedDatabasenames = new HashSet<String>();

	private String changelog = null;

	private ResourceLoader resourceLoader;

	@Override
	protected String getDatabaseName() {

		String dbName = databaseNamingStrategy.getCurrentTenantDatabaseName();
		if (language == Language.HSQL || language == Language.ORACLE) {
			dbName = dbName.toUpperCase();
		}
		return dbName;
	}

	public void setDatabaseNamingStrategy(DatabaseNamingStrategy databaseNamingStrategy) {
		this.databaseNamingStrategy = databaseNamingStrategy;
	}

	@Override
	public Connection getConnection() throws SQLException {
		Connection connection = super.getConnection();

		maybeRunLiquibaseUpdate(connection);

		return connection;
	}

	private void maybeRunLiquibaseUpdate(Connection connection) throws SQLException {
		if (changelog == null) {
			return;
		}

		String dbName = getDatabaseName();
		String dbType = connection.getMetaData().getDatabaseProductName();

		synchronized (updatedDatabasenames) {
			if (updatedDatabasenames.contains(dbName)) {
				return;
			}
			updatedDatabasenames.add(dbName);
		}

		try {
			LiquibaseRunner schemaInstaller = new LiquibaseRunner();
			schemaInstaller.setResourceLoader(resourceLoader);
			schemaInstaller.setDataSource(this);
			schemaInstaller.setChangeLog(changelog);

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

	/** Remove the (transient) memory that a schema has been updated. */
	public void forgetSchema(String schemaName) {
		updatedDatabasenames.remove(schemaName);
	}

	/**
	 * @param changelog
	 *            the changelog to use for schema creation
	 */
	public void setChangelog(String changelog) {
		this.changelog = changelog;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
}
