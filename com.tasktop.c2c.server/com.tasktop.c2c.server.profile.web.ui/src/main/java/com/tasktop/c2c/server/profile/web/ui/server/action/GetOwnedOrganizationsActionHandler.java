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
package com.tasktop.c2c.server.profile.web.ui.server.action;

import java.util.LinkedList;
import java.util.List;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.common.profile.web.shared.actions.GetOwnedOrganizationsAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetOwnedOrganizationsResult;
import com.tasktop.c2c.server.profile.domain.internal.Organization;

/**
 * @author Myles (Tasktop Technologies Inc.)
 * 
 */
@Component
public class GetOwnedOrganizationsActionHandler extends
		AbstractProfileActionHandler<GetOwnedOrganizationsAction, GetOwnedOrganizationsResult> {

	@Override
	public GetOwnedOrganizationsResult execute(GetOwnedOrganizationsAction action, ExecutionContext context)
			throws DispatchException {

		List<com.tasktop.c2c.server.profile.domain.internal.Organization> ownedOrgs = profileService
				.getOwnedOrganizations();

		List<com.tasktop.c2c.server.profile.domain.project.Organization> organizations = new LinkedList<com.tasktop.c2c.server.profile.domain.project.Organization>();
		for (Organization org : ownedOrgs) {
			com.tasktop.c2c.server.profile.domain.project.Organization organization = new com.tasktop.c2c.server.profile.domain.project.Organization();
			organization.setDescription(org.getDescription());
			organization.setIdentifier(org.getIdentifier());
			organization.setId(org.getId());
			organization.setName(org.getName());

			com.tasktop.c2c.server.profile.domain.project.ProjectPreferences preferences = new com.tasktop.c2c.server.profile.domain.project.ProjectPreferences();
			preferences.setId(org.getProjectPreferences().getId());
			preferences.setWikiLanguage(org.getProjectPreferences().getWikiLanguage());
			organization.setProjectPreferences(preferences);
			organizations.add(organization);
			// organization.setProjects(org.getProjects());
		}

		return new GetOwnedOrganizationsResult(organizations);
	}
}
