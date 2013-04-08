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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.cloud.domain.ServiceType;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.common.service.domain.QueryResult;
import com.tasktop.c2c.server.profile.domain.internal.ProjectService;
import com.tasktop.c2c.server.profile.service.provider.WikiServiceProvider;
import com.tasktop.c2c.server.wiki.domain.Page;
import com.tasktop.c2c.server.wiki.service.WikiService;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
@Component
public class WikiServiceCloner extends BaseProjectServiceCloner {

	private static final Logger LOGGER = LoggerFactory.getLogger(WikiServiceCloner.class);

	@Autowired
	private WikiServiceProvider wikiServiceProvider;

	/**
	 * @param serviceType
	 */
	protected WikiServiceCloner() {
		super(ServiceType.WIKI);
	}

	@Override
	public void doClone(CloneContext context) {
		ProjectService templateService = context.getTemplateService();
		ProjectService targetService = context.getTargetService();
		// Note there is non-obvious ordering deps here:
		// * Can only get at one wS at a time (due to static tenancy context),
		// * Security context is used at service provider time only
		AuthUtils.assumeSystemIdentity(templateService.getProjectServiceProfile().getProject().getIdentifier());
		WikiService templateWikiService = wikiServiceProvider.getWikiService(templateService.getProjectServiceProfile()
				.getProject().getIdentifier());

		QueryResult<Page> pages = templateWikiService.findPages(null, null);

		AuthUtils.assumeSystemIdentity(targetService.getProjectServiceProfile().getProject().getIdentifier());
		WikiService targetWikiService = wikiServiceProvider.getWikiService(targetService.getProjectServiceProfile()
				.getProject().getIdentifier());

		for (Page page : pages.getResultPage()) {
			page.setId(null);
			try {
				targetWikiService.createPage(page);
			} catch (ValidationException e) {
				LOGGER.info(String.format("Error cloning wiki page [%s]", page.getPath()), e);
			} catch (EntityNotFoundException e) {
				LOGGER.info(String.format("Error cloning wiki page [%s]", page.getPath()), e);
			}
		}

	}

}
