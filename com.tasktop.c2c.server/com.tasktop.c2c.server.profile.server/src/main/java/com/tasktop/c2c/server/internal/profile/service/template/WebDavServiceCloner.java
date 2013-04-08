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
package com.tasktop.c2c.server.internal.profile.service.template;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.jackrabbit.webdav.DavException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.service.provider.WebDavService;
import com.tasktop.c2c.server.profile.service.provider.WebDavServiceProvider;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
@Component
public class WebDavServiceCloner extends BaseProjectServiceCloner {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebDavServiceCloner.class);

	@Autowired
	private WebDavServiceProvider webdavServiceProvider;

	/**
	 * @param serviceType
	 */
	protected WebDavServiceCloner() {
		super(ServiceType.MAVEN);
	}

	@Override
	public void doClone(CloneContext context) {

		List<String> templateFiles;
		try {
			templateFiles = getClient(context.getTemplateService()).listAllFiles();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (DavException e) {
			throw new RuntimeException(e);
		}

		for (String file : templateFiles) {
			try {
				InputStream content = getClient(context.getTemplateService()).getFileContent(file);
				getClient(context.getTargetService()).writeFile(file, content);
			} catch (IOException e) {
				LOGGER.warn(String.format("Error tranfering [%s], will continue", file), e);
			}
		}

	}

	// Note there is non-obvious ordering deps here:
	// * Can only get at one wS at a time (due to static tenancy context),
	// * Security context is used at service provider time only
	private WebDavService getClient(ProjectService service) {
		AuthUtils.assumeSystemIdentity(service.getProjectServiceProfile().getProject().getIdentifier());
		return webdavServiceProvider.getWebdavService(service.getProjectServiceProfile().getProject().getIdentifier());
	}

}
