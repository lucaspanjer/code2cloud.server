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

import org.apache.commons.io.FileUtils;

import com.tasktop.c2c.server.configuration.service.ProjectServiceManagementServiceBean.MetricCollector;

/**
 * Generic metric which collects total disk size for a given directory.
 * 
 * @author clint.morgan@tasktop.com (Tasktop Technologies Inc.)
 * 
 */
public class DatabaseMetricCollector implements MetricCollector {

	public static final String PROJECT_ID_VAR = "{projectIdentifier}";

	private String metricName;
	private DataSource dataSource;
	private String sqlSizeQuery;

	@Override
	public void collect(ProjectServiceStatus status) {
		String query = sqlSizeQuery.replace(PROJECT_ID_VAR, status.getProjectIdentifier());

		try {
			Connection c = dataSource.getConnection();
			ResultSet resultSet = c.createStatement().executeQuery(query);
			if (resultSet.next()) {
				int totalByteSize = resultSet.getInt(1);

				status.getMetrics().put(metricName, totalByteSize + "");
				status.getMetrics().put(metricName + ".humanReadable", FileUtils.byteCountToDisplaySize(totalByteSize));
			}
		} catch (SQLException e) {
			e.printStackTrace();
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

}
