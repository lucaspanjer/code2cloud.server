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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tasktop.c2c.server.configuration.service.ProjectServiceManagementServiceBean.Deprovisioner;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class DirectoryMovingDeprovisoiner implements Deprovisioner {

	private static final Logger LOG = LoggerFactory.getLogger(DirectoryMovingDeprovisoiner.class);

	private String fromBaseDir;
	private String toBaseDir;
	private String toDirName = "";

	@Override
	public void deprovision(ProjectServiceConfiguration configuration) {
		String projectIdentifier = configuration.getProjectIdentifier();
		String uniqueProjectIdentifier = configuration.getProperties()
				.get(ProjectServiceConfiguration.UNIQUE_IDENTIFER);
		File toBaseDirFile = new File(toBaseDir, uniqueProjectIdentifier);
		File fromDir = new File(fromBaseDir + "/" + projectIdentifier);

		File toDir = new File(toBaseDirFile, toDirName);

		if (!fromDir.exists()) {
			LOG.info(String.format("Could not move [%s], does not exist", fromDir.getPath()));
			return;
		}

		if (!toBaseDirFile.exists()) {
			toBaseDirFile.mkdirs();
		}

		boolean succeeded = fromDir.renameTo(toDir);
		if (!succeeded) {
			LOG.info(String.format("Could not move [%s] to [%s]", fromDir.getPath(), toDir.getPath()));
		}

	}

	public void setFromBaseDir(String fromBaseDir) {
		this.fromBaseDir = fromBaseDir;
	}

	public void setToBaseDir(String toBaseDir) {
		this.toBaseDir = toBaseDir;
	}

	public void setToDirName(String toDirName) {
		this.toDirName = toDirName;
	}

}
