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

	private HudsonWarNamingStrategy hudsonWarNamingStrategy;

	@Override
	public void deprovision(ProjectServiceConfiguration configuration) {
		String webappPath = hudsonWarNamingStrategy.getWarFilePath(configuration);
		tryDelete(new File(webappPath));
		if (webappPath.endsWith(".war")) {
			String webappDir = webappPath.substring(0, webappPath.length() - ".war".length());
			tryDelete(new File(webappDir));
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

	public void setHudsonWarNamingStrategy(HudsonWarNamingStrategy hudsonWarNamingStrategy) {
		this.hudsonWarNamingStrategy = hudsonWarNamingStrategy;
	}

}
