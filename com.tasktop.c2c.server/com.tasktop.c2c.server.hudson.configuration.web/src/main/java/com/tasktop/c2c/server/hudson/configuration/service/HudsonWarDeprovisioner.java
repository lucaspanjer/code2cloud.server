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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
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

			tryDelete(new File(hudsonWebappsDir, webappName));
			tryDelete(new File(hudsonWebappsDir, webappName + ".war"));

		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * @param file
	 */
	private void tryDelete(File file) {
		if (!file.exists()) {
			LOG.info(String.format("Could not delete [%s], does not exist", file.getPath()));
			return;
		}

		if (file.isDirectory()) {
			try {
				FileUtils.deleteDirectory(file);
			} catch (IOException e) {
				LOG.info(String.format("Could not delete [%s]", file.getPath()), e);
			}
		} else {
			boolean succeeded = file.delete();
			if (!succeeded) {
				LOG.info(String.format("Could not delete [%s]", file.getPath()));
			}

		}
	}

	public void setHudsonWebappsDir(String hudsonWebappsDir) {
		this.hudsonWebappsDir = hudsonWebappsDir;
	}

}
