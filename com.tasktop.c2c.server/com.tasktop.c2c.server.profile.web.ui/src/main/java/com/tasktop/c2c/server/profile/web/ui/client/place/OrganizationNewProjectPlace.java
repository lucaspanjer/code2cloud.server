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
package com.tasktop.c2c.server.profile.web.ui.client.place;

import java.util.LinkedHashMap;

import com.tasktop.c2c.server.common.profile.web.client.navigation.AbstractPlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetOrganizationAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetOrganizationResult;
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.profile.domain.project.Organization;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class OrganizationNewProjectPlace extends NewProjectPlace {

	public static final String ORG = "org";

	public static PageMapping OrganizationNewProject = new PageMapping(new OrganizationNewProjectPlace.Tokenizer(),
			"o/{" + ORG + "}/newProject");

	public static class Tokenizer extends AbstractPlaceTokenizer<OrganizationNewProjectPlace> {

		@Override
		public OrganizationNewProjectPlace getPlace(String token) {
			Args pathArgs = getPathArgsForUrl(token);

			String orgId = pathArgs.getString(ORG);
			return createPlace(orgId);
		}

	}

	private String organizationIdentifier;

	private Organization organization;

	protected OrganizationNewProjectPlace(String organizationIdentifier) {
		this.organizationIdentifier = organizationIdentifier;
	}

	public static OrganizationNewProjectPlace createPlace(String orgId) {
		return new OrganizationNewProjectPlace(orgId);
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();

		tokenMap.put(ORG, organizationIdentifier);

		return OrganizationNewProject.getUrlForNamedArgs(tokenMap);
	}

	@Override
	protected void addActions() {
		super.addActions();
		addAction(new GetOrganizationAction(organizationIdentifier));
	}

	@Override
	protected void handleBatchResults() {
		this.organization = getResult(GetOrganizationResult.class).get();
		super.handleBatchResults();
	}

	/**
	 * @return the query
	 */
	public String getOrganizationIdentifer() {
		return organizationIdentifier;
	}

	public Organization getOrganization() {
		return organization;
	}

}
