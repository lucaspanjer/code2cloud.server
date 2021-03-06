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
package com.tasktop.c2c.server.scm.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.configuration.service.ProjectServiceConfiguration;
import com.tasktop.c2c.server.configuration.service.ProjectServiceManagementServiceBean.Configurator;

@Component
public class GitRepositoryConfigurator implements Configurator {

	private static final Logger LOG = LoggerFactory.getLogger(GitRepositoryConfigurator.class.getName());

	@Autowired
	private GitService gitService;

	@Value("${git.root}")
	private String basePath;

	@Override
	public void configure(ProjectServiceConfiguration configuration) {
		createInitialRepo(configuration);
		setupSshConfig(new File(basePath, configuration.getProjectIdentifier()));
	}

	private void setupSshConfig(File projectRoot) {
		File sshDir = new File(projectRoot, ".ssh");

		if (!sshDir.exists()) {
			sshDir.mkdirs();
		}
		try {

			File config = new File(sshDir, "config");
			if (!config.exists()) {
				FileWriter writer = null;

				try {
					writer = new FileWriter(config);

					writer.write("Host *\n");
					writer.write("        StrictHostKeyChecking no");
				} finally {
					if (writer != null) {
						writer.close();
					}

				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			KeyPair kp = KeyPair.genKeyPair(new JSch(), KeyPair.RSA);
			if (!new File(sshDir, "id_rsa.pub").exists()) {
				kp.writePublicKey(sshDir.getAbsolutePath() + "/id_rsa.pub",
						"Used for Code2Cloud fetching of external repos");
			}
			if (!new File(sshDir, "id_rsa").exists()) {
				kp.writePrivateKey(sshDir.getAbsolutePath() + "/id_rsa");
			}
		} catch (JSchException e) {
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private void createInitialRepo(ProjectServiceConfiguration configuration) {
		String gitRepositoryName = configuration.getProperties().get(
				ProjectServiceConfiguration.APPLICATION_GIT_PROPERTY);

		if (gitRepositoryName == null) {
			return;
		}
		AuthUtils.assumeSystemIdentity(null);

		File projectDir = new File(basePath, configuration.getProjectIdentifier());
		File gitDir = new File(projectDir, GitConstants.HOSTED_GIT_DIR + "/" + gitRepositoryName);

		if (!gitDir.exists()) {
			gitService.createEmptyRepository(gitRepositoryName);
		}
	}

}
