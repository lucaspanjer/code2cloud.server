/*******************************************************************************
 * Copyright (c) 2012, Oracle and/or its affiliates. 
 * All rights reserved. 
 ******************************************************************************/
package com.tasktop.c2c.server.common.jpa;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Simple class to run a script (semi-colon separated sql) on a datasource.
 * 
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class DatasourceScriptRunner implements InitializingBean {

	private DataSource dataSource;
	private String script;
	private boolean continueOnFailure = true;

	private static final Logger LOG = LoggerFactory.getLogger(DatasourceScriptRunner.class);

	public void runScript() throws SQLException {
		Connection c = null;

		try {
			c = dataSource.getConnection();

			for (String sqlStament : getStatements()) {
				Statement s = c.createStatement();

				LOG.info(String.format("Executing: [%s]", sqlStament));
				try {
					s.execute(sqlStament);
				} catch (SQLException e) {
					LOG.warn("Caught exception", e);
					if (!continueOnFailure) {
						break;
					}
				} finally {
					s.close();
				}
			}
		} finally {
			c.close();
		}

	}

	/**
	 * @return
	 */
	private List<String> getStatements() {
		List<String> result = new ArrayList<String>();
		for (String split : script.split(";")) {
			String statement = split.trim();
			if (!statement.isEmpty()) {
				result.add(statement);
			}
		}
		return result;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public boolean isContinueOnFailure() {
		return continueOnFailure;
	}

	public void setContinueOnFailure(boolean continueOnFailure) {
		this.continueOnFailure = continueOnFailure;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		runScript();

	}
}
