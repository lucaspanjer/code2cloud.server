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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.AuthenticationHelper;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.place.Breadcrumb;
import com.tasktop.c2c.server.common.profile.web.client.place.BreadcrumbPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HeadingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.LoggedInPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.WindowTitlePlace;
import com.tasktop.c2c.server.common.profile.web.client.util.WindowTitleBuilder;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetOrganizationAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetOrganizationResult;
import com.tasktop.c2c.server.common.service.domain.Role;
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.navigation.Path;
import com.tasktop.c2c.server.profile.domain.project.Organization;
import com.tasktop.c2c.server.profile.web.ui.client.navigation.PageMappings;

/**
 * @author Myles Feichtinger (Tasktop Technologies Inc.)
 * 
 */
public class OrganizationAdminPlace extends LoggedInPlace implements HeadingPlace, WindowTitlePlace, BreadcrumbPlace {

	private Organization organization;

	private String organizationId;

	private final String orgAdminRole = Role.Admin + "/ORG_";
	private List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();

	public static class Tokenizer implements PlaceTokenizer<OrganizationAdminPlace> {

		@Override
		public OrganizationAdminPlace getPlace(String token) {
			Args pathArgs = PageMapping.getPathArgsForUrl(token);

			String orgId = pathArgs.getString(Path.ORGANIZATION_ID);

			return OrganizationAdminPlace.createPlace(orgId);
		}

		@Override
		public String getToken(OrganizationAdminPlace place) {
			return place.getToken();
		}
	}

	@Override
	public String getHeading() {
		return "Edit Organization";
	}

	protected OrganizationAdminPlace(String organizationId) {
		this.organizationId = organizationId;
	}

	public static OrganizationAdminPlace createPlace(String organizationId) {
		return new OrganizationAdminPlace(organizationId);
	}

	@Override
	public String getToken() {
		return "";
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();

		tokenMap.put(Path.ORGANIZATION_ID, organizationId);

		return PageMappings.OrgAdminPlace.getUrlForNamedArgs(tokenMap);
	}

	public Organization getOrganization() {
		return organization;
	}

	@Override
	public String getWindowTitle() {
		return WindowTitleBuilder.createWindowTitle("Edit Organization");
	}

	@Override
	protected void addActions() {
		super.addActions();
		addAction(new GetOrganizationAction(organizationId));
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		organization = getResult(GetOrganizationResult.class).get();
		createBreadcrumbs(organization);
		onPlaceDataFetched();
	}

	@Override
	protected boolean isNotAuthorized() {
		return !AuthenticationHelper.isOrgAdmin(organizationId);
	}

	private void createBreadcrumbs(Organization organization) {
		breadcrumbs = Breadcrumb.getOrganizationSpecificBreadcrumbs(organization);
		breadcrumbs.add(new Breadcrumb(getHistoryToken(), "Edit"));
	}

	@Override
	public List<Breadcrumb> getBreadcrumbs() {
		return breadcrumbs;
	}
}
