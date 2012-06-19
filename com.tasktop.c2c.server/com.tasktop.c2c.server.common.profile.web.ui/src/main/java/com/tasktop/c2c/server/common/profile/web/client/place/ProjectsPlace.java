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

import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.profile.web.client.navigation.AbstractPlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.util.WindowTitleBuilder;
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.util.StringUtils;

/**
 * @author straxus (Tasktop Technologies Inc.)
 * 
 */
public class ProjectsPlace extends AbstractBatchFetchingPlace implements HeadingPlace, WindowTitlePlace {

	public static final String QUERY = "query";

	public static PageMapping Discover = new PageMapping(new ProjectsPlace.Tokenizer(), "projects", "search/{" + QUERY
			+ "}");

	public static class Tokenizer extends AbstractPlaceTokenizer<ProjectsPlace> {

		@Override
		public ProjectsPlace getPlace(String token) {
			Args pathArgs = getPathArgsForUrl(token);

			// Only one of the following three can be defined at a time, so use this to determine which place to create.
			String query = pathArgs.getString(QUERY);

			if (StringUtils.hasText(query)) {
				// We have a query string.
				return createPlaceForQuery(query);
			} else {
				return createPlace();
			}
		}

	}

	private final String query;
	private final boolean assumeUserIsAnonymous;

	protected ProjectsPlace(boolean assumeUserIsAnon) {
		this.assumeUserIsAnonymous = assumeUserIsAnon;
		query = null;
	}

	protected ProjectsPlace(String query) {
		this.query = query;
		assumeUserIsAnonymous = false;
	}

	@Override
	public String getHeading() {
		return "Discover Projects";
	}

	public static ProjectsPlace createPlace() {
		return new ProjectsPlace(false);
	}

	public static ProjectsPlace createPlaceForQuery(String query) {
		return new ProjectsPlace(query);
	}

	public static ProjectsPlace createPlaceForAfterLogout() {
		return new ProjectsPlace(true);
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();

		if (query != null) {
			tokenMap.put(QUERY, query);
		}

		return Discover.getUrlForNamedArgs(tokenMap);
	}

	@Override
	public String getWindowTitle() {
		return WindowTitleBuilder.createWindowTitle("Browse Projects");
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		if (assumeUserIsAnonymous) {
			ProfileGinjector.get.instance().getAppState().setCredentials(null);
		}
		onPlaceDataFetched();
	}

	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}
}
