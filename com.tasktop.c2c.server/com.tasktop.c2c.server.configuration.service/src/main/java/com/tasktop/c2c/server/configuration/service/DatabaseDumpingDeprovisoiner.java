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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tasktop.c2c.server.common.service.io.InputPipe;
import com.tasktop.c2c.server.configuration.service.ProjectServiceManagementServiceBean.Deprovisioner;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class DatabaseDumpingDeprovisoiner implements Deprovisioner {

	private static final Logger LOG = LoggerFactory.getLogger(DatabaseDumpingDeprovisoiner.class);

	private String dbSuffix;
	private String toBaseDir;
	private String mysqldumpCommand;
	private String host;
	private String user;
	private String password;

	private DataSource rawDataSource;

	@Override
	public void deprovision(ProjectServiceConfiguration configuration) {
		String uniqueProjectIdentifier = configuration.getProperties()
				.get(ProjectServiceConfiguration.UNIQUE_IDENTIFER);

		File toBaseDirFile = new File(toBaseDir, uniqueProjectIdentifier);

		if (!toBaseDirFile.exists()) {
			toBaseDirFile.mkdirs();
		}

		String dbName = configuration.getProjectIdentifier() + "_" + dbSuffix;

		File dumpFile = new File(toBaseDirFile, dbName + ".sql");
		boolean dumped = dumpDatabase(dbName, dumpFile);
		if (dumped) {
			try {
				deleteDatabase(dbName);
			} catch (SQLException e) {
				LOG.warn(String.format("Could not delete database [%s]", dbName), e);
				return;
			}
		}
	}

	/**
	 * @param dbName
	 * @throws SQLException
	 */
	private void deleteDatabase(String dbName) throws SQLException {
		Connection c = null;
		try {
			c = rawDataSource.getConnection();
			c.createStatement().executeUpdate("drop database `" + dbName + "`;");
			c.close();
		} finally {
			if (c != null) {
				c.close();
			}
		}

	}

	/**
	 * @param dbName
	 * @param dumpFile
	 */
	private boolean dumpDatabase(String dbName, File dumpFile) {
		try {
			List<String> command = new ArrayList<String>();
			command.add(mysqldumpCommand);
			command.add("-h" + host);
			command.add("-u" + user);
			if (password != null && !password.isEmpty()) {
				command.add("-p" + password);
			}
			command.add(dbName);
			Process p = new ProcessBuilder(command).start();
			FileOutputStream ouput = new FileOutputStream(dumpFile);
			ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();
			InputPipe stdOutPipe = new InputPipe(p.getInputStream(), ouput, 1024 * 10, null);
			InputPipe errorPipe = new InputPipe(p.getErrorStream(), errorOutput, 1024 * 10, null);

			ExecutorService executorService = Executors.newFixedThreadPool(2);
			executorService.submit(stdOutPipe);
			executorService.submit(errorPipe);

			int result = p.waitFor();
			executorService.shutdown();

			ouput.close();

			if (result != 0) {
				String error = "STDERROR" + errorOutput.toString();

				LOG.warn(String.format("Could not dump database [%s]", dbName));
				LOG.warn(error);
				return false;
			}
			return true;
		} catch (Exception e) {
			LOG.warn(String.format("Could not dump database [%s]", dbName), e);
			return false;
		}
	}

	public void setToBaseDir(String toBaseDir) {
		this.toBaseDir = toBaseDir;
	}

	public void setDbSuffix(String dbSuffix) {
		this.dbSuffix = dbSuffix;
	}

	public void setMysqldumpCommand(String mysqldumpCommand) {
		this.mysqldumpCommand = mysqldumpCommand;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToBaseDir() {
		return toBaseDir;
	}

	public void setRawDataSource(DataSource rawDataSource) {
		this.rawDataSource = rawDataSource;
	}

}
