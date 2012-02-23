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
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Kicks off a process.
 * 
 */
public class ProcessRunningConfigurator implements ProjectServiceManagementServiceBean.Configurator {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessRunningConfigurator.class.getName());

	private List<String> command;
	private String workingDirectory = null;

	private static final String CURRENT_APPLICATION_ID_PLACEHOLDER = "#profile.application.identifier";

	@Override
	public void configure(ProjectServiceConfiguration configuration) {
		List<String> processedCommand = process(command, configuration);
		LOGGER.info("Running process " + processedCommand);
		try {
			ProcessBuilder process = new ProcessBuilder().command(processedCommand);
			if (workingDirectory != null) {
				String processedWorkingDirectory = workingDirectory.replace(CURRENT_APPLICATION_ID_PLACEHOLDER,
						configuration.getProjectIdentifier());
				process.directory(new File(processedWorkingDirectory));
			}
			process.start().waitFor();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List<String> process(List<String> command, ProjectServiceConfiguration configuration) {
		List<String> processed = new ArrayList<String>(command.size());
		for (String arg : command) {
			String processedArg = arg;

			processedArg = processedArg.replace(CURRENT_APPLICATION_ID_PLACEHOLDER, configuration.getProjectIdentifier());

			processed.add(processedArg);
		}
		return processed;
	}

	public void setCommand(List<String> command) {
		this.command = command;
	}

	public void setWorkingDirectory(String workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

}
