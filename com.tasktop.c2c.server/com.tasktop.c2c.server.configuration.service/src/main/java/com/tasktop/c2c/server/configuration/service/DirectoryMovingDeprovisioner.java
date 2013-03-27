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
public class DirectoryMovingDeprovisioner implements Deprovisioner {

	private static final Logger LOG = LoggerFactory.getLogger(DirectoryMovingDeprovisioner.class);

	private String fromBaseDir;
	private String toBaseDir;
	private String toDirName = "";
	private boolean perOrg = false;

	@Override
	public void deprovision(ProjectServiceConfiguration configuration) {

		String uniqueIdentifier = configuration.getProperties().get(ProjectServiceConfiguration.UNIQUE_IDENTIFER);
		if (perOrg) {
			uniqueIdentifier = configuration.getOrganizationIdentifier() + "-" + uniqueIdentifier;
		}
		File toBaseDirFile = new File(toBaseDir, uniqueIdentifier);
		File fromDir = new File(fromBaseDir + "/"
				+ (perOrg ? configuration.getOrganizationIdentifier() : configuration.getProjectIdentifier()));

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

	public void setPerOrg(boolean perOrg) {
		this.perOrg = perOrg;
	}

}
