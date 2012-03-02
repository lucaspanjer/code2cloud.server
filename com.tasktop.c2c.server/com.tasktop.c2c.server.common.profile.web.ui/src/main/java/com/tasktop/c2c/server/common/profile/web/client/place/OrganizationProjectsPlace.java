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
package com.tasktop.c2c.server.common.profile.web.client.place;

import java.util.LinkedHashMap;
import java.util.List;

import net.customware.gwt.dispatch.shared.Action;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.util.WindowTitleBuilder;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetOrganizationAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetOrganizationResult;
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.util.StringUtils;
import com.tasktop.c2c.server.profile.domain.project.Organization;

/**
 * @author straxus (Tasktop Technologies Inc.)
 * 
 */
public class OrganizationProjectsPlace extends ProjectsPlace {

	public static final String QUERY = "query";
	public static final String ORG = "org";

	public static PageMapping Discover = new PageMapping(new OrganizationProjectsPlace.Tokenizer(), "o/{" + ORG + "}",
			"o/{" + ORG + "}/search/{" + QUERY + "}");

	public static class Tokenizer implements PlaceTokenizer<OrganizationProjectsPlace> {

		@Override
		public OrganizationProjectsPlace getPlace(String token) {
			Args pathArgs = PageMapping.getPathArgsForUrl(token);

			String orgId = pathArgs.getString(ORG);
			String query = pathArgs.getString(QUERY);

			if (StringUtils.hasText(query)) {
				// We have a query string.
				return createPlaceForQuery(orgId, query);
			} else {
				return createPlaceForOrg(orgId);
			}
		}

		@Override
		public String getToken(OrganizationProjectsPlace place) {
			return place.getToken();
		}
	}

	private final String organizationIdentifier;
	private Organization organization;

	private OrganizationProjectsPlace(String orgId, String query) {
		super(query);
		this.organizationIdentifier = orgId;
	}

	@Override
	public String getHeading() {
		return "Discover Projects";
	}

	@Override
	public String getToken() {
		return "";
	}

	public static OrganizationProjectsPlace createPlaceForOrg(String orgId) {
		return new OrganizationProjectsPlace(orgId, null);
	}

	public static OrganizationProjectsPlace createPlaceForQuery(String orgId, String query) {
		return new OrganizationProjectsPlace(orgId, query);
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();

		if (getQuery() != null) {
			tokenMap.put(QUERY, getQuery());
		}
		tokenMap.put(ORG, organizationIdentifier);

		return Discover.getUrlForNamedArgs(tokenMap);
	}

	@Override
	public String getWindowTitle() {
		return WindowTitleBuilder.createWindowTitle("Browse Projects");
	}

	@Override
	protected void addActions(List<Action<?>> actions) {
		super.addActions(actions);
		actions.add(new GetOrganizationAction(organizationIdentifier));
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
