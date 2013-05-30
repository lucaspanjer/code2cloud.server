/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies
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

import hudson.model.Hudson;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.filters.StringInputStream;

import com.tasktop.c2c.server.configuration.service.ProjectServiceConfiguration;
import com.tasktop.c2c.server.configuration.service.ProjectServiceManagementServiceBean.Configurator;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public class HudsonJobConfigurator implements Configurator {

	private String templateJobsDirectory = "/opt/code2cloud/configuration/hudson-jobs";

	@Override
	public void configure(ProjectServiceConfiguration configuration) {
		try {
			Map<String, String> jobNameToConfig = getJobNameToConfigMap(configuration);

			for (Entry<String, String> nameAndConig : jobNameToConfig.entrySet()) {
				Hudson.getInstance().createProjectFromXML(nameAndConig.getKey(),
						new StringInputStream(nameAndConig.getValue()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return
	 * @throws IOException
	 */
	private Map<String, String> getJobNameToConfigMap(ProjectServiceConfiguration configuration) throws IOException {
		Map<String, String> result = new HashMap<String, String>();

		File jobsDir = new File(templateJobsDirectory);

		if (!jobsDir.exists() || !jobsDir.isDirectory()) {
			return result;
		}

		for (File jobDir : jobsDir.listFiles()) {
			String name = jobDir.getName();
			String configXml = FileUtils.readFileToString(new File(jobDir, "config.xml"));
			configXml = rewriteConfigForProject(configXml, configuration);
			result.put(name, configXml);
		}

		return result;
	}

	/**
	 * @param configXml
	 * @param configuration
	 * @return
	 */
	private String rewriteConfigForProject(String configXml, ProjectServiceConfiguration configuration) {
		for (Entry<String, String> entry : configuration.getProperties().entrySet()) {
			String value = entry.getValue();
			if (value == null) {
				value = "";
			}
			configXml = configXml.replace("${" + entry.getKey() + "}", value);
		}
		return configXml;
	}

}
