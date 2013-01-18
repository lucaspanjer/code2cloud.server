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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import liquibase.util.JdbcUtils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tasktop.c2c.server.cloud.domain.ProjectServiceStatus;
import com.tasktop.c2c.server.cloud.domain.ProjectServiceStatus.ServiceState;
import com.tasktop.c2c.server.common.internal.tenancy.DatabaseNamingStrategy;
import com.tasktop.c2c.server.configuration.service.ProjectServiceManagementServiceBean.MetricCollector;

/**
 * Generic metric which collects total disk size for a given directory.
 * 
 * @author clint.morgan@tasktop.com (Tasktop Technologies Inc.)
 * 
 */
public class DatabaseMetricCollector implements MetricCollector {

	private static final Logger LOG = LoggerFactory.getLogger(DatabaseMetricCollector.class);

	public static final String DATABSE_NAME_VAR = "{databaseName}";

	private String metricName;
	private DataSource dataSource;
	private String sqlSizeQuery;
	private DatabaseNamingStrategy databaseNamingStrategy;
	private boolean uppercaseDatbaseName = false;

	@Override
	public void collect(ProjectServiceStatus status) {
		String databaseName = databaseNamingStrategy.getCurrentTenantDatabaseName();
		if (uppercaseDatbaseName) {
			databaseName = databaseName.toUpperCase();
		}
		String query = sqlSizeQuery.replace(DATABSE_NAME_VAR, databaseName);
		LOG.debug(String.format("Collecting database metrics with query: [%s]", query));
		Connection c = null;
		ResultSet resultSet = null;
		try {
			c = dataSource.getConnection();
			resultSet = c.createStatement().executeQuery(query);
			if (resultSet.next()) {
				int totalByteSize = resultSet.getInt(1);
				status.setServiceState(ServiceState.RUNNING);
				status.getMetrics().put(metricName, totalByteSize + "");
				status.getMetrics().put(metricName + "_humanReadable", FileUtils.byteCountToDisplaySize(totalByteSize));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			status.setServiceState(ServiceState.UNAVAILABLE);
		} finally {
			JdbcUtils.closeResultSet(resultSet);
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public void setSqlSizeQuery(String sqlSizeQuery) {
		this.sqlSizeQuery = sqlSizeQuery;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setUppercaseDatbaseName(boolean uppercaseProjectIdentifier) {
		this.uppercaseDatbaseName = uppercaseProjectIdentifier;
	}

	public void setDatabaseNamingStrategy(DatabaseNamingStrategy databaseNamingStrategy) {
		this.databaseNamingStrategy = databaseNamingStrategy;
	}

}
