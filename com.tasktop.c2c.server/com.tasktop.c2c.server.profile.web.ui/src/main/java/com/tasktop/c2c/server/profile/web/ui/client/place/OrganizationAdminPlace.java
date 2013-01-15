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

import com.tasktop.c2c.server.common.profile.web.client.AuthenticationHelper;
import com.tasktop.c2c.server.common.profile.web.client.navigation.AbstractPlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.place.Breadcrumb;
import com.tasktop.c2c.server.common.profile.web.client.place.BreadcrumbPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HeadingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.LoggedInPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.WindowTitlePlace;
import com.tasktop.c2c.server.common.profile.web.client.util.WindowTitleBuilder;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetOrganizationAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetOrganizationResult;
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.navigation.Path;
import com.tasktop.c2c.server.profile.domain.project.Organization;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;

/**
 * @author Myles Feichtinger (Tasktop Technologies Inc.)
 * 
 */
public class OrganizationAdminPlace extends LoggedInPlace implements HeadingPlace, WindowTitlePlace, BreadcrumbPlace {

	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	private Organization organization;

	private String organizationId;

	private List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();

	public static PageMapping OrgAdminPlace = new PageMapping(new OrganizationAdminPlace.Tokenizer(), "o/{"
			+ Path.ORGANIZATION_ID + "}/admin");

	private static class Tokenizer extends AbstractPlaceTokenizer<OrganizationAdminPlace> {

		@Override
		public OrganizationAdminPlace getPlace(String token) {
			Args pathArgs = getPathArgsForUrl(token);

			String orgId = pathArgs.getString(Path.ORGANIZATION_ID);

			return OrganizationAdminPlace.createPlace(orgId);
		}

	}

	@Override
	public String getHeading() {
		return profileMessages.editOrganization();
	}

	protected OrganizationAdminPlace(String organizationId) {
		this.organizationId = organizationId;
	}

	public static OrganizationAdminPlace createPlace(String organizationId) {
		return new OrganizationAdminPlace(organizationId);
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();

		tokenMap.put(Path.ORGANIZATION_ID, organizationId);

		return OrgAdminPlace.getUrlForNamedArgs(tokenMap);
	}

	public Organization getOrganization() {
		return organization;
	}

	@Override
	public String getWindowTitle() {
		return WindowTitleBuilder.createWindowTitle(profileMessages.editOrganization());
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
		breadcrumbs.add(new Breadcrumb(getHref(), commonProfileMessages.edit()));
	}

	@Override
	public List<Breadcrumb> getBreadcrumbs() {
		return breadcrumbs;
	}
}
