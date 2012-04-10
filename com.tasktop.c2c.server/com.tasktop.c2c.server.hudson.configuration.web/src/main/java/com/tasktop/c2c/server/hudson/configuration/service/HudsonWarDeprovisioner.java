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
package com.tasktop.c2c.server.hudson.configuration.service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tasktop.c2c.server.configuration.service.ProjectServiceConfiguration;
import com.tasktop.c2c.server.configuration.service.ProjectServiceManagementServiceBean.Deprovisioner;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class HudsonWarDeprovisioner implements Deprovisioner {

	private static final Logger LOG = LoggerFactory.getLogger(HudsonWarDeprovisioner.class.getName());

	private String hudsonWebappsDir;

	@Override
	public void deprovision(ProjectServiceConfiguration configuration) {
		try {
			String deployedUrl = configuration.getProperties()
					.get(ProjectServiceConfiguration.PROFILE_BASE_SERVICE_URL) + "hudson/";
			deployedUrl.replace("//", "/");
			URL deployedHudsonUrl = new URL(deployedUrl);
			String webappName = deployedHudsonUrl.getPath();
			if (webappName.startsWith("/")) {
				webappName = webappName.substring(1);
			}
			if (webappName.endsWith("/")) {
				webappName = webappName.substring(0, webappName.length() - 1);
			}
			webappName = webappName.replace("/", "#");
			webappName = webappName + ".war";

			String deployLocation = hudsonWebappsDir + webappName;

			File hudsonDeployFile = new File(deployLocation);

			if (!hudsonDeployFile.exists()) {
				LOG.info(String.format("Could not delete [%s], does not exist", hudsonDeployFile.getPath()));
				return;
			}

			boolean succeeded = hudsonDeployFile.delete();
			if (!succeeded) {
				LOG.info(String.format("Could not delete [%s]", hudsonDeployFile.getPath()));
			}

		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

	}

	public void setHudsonWebappsDir(String hudsonWebappsDir) {
		this.hudsonWebappsDir = hudsonWebappsDir;
	}

}
