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

import com.tasktop.c2c.server.common.profile.web.client.CommonProfileMessages;
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

	public static PageMapping Projects = new PageMapping(new ProjectsPlace.Tokenizer(), "projects", "search/{" + QUERY
			+ "}");

	private static class Tokenizer extends AbstractPlaceTokenizer<ProjectsPlace> {

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

	private CommonProfileMessages messages = ProfileGinjector.get.instance().getMessages();

	protected ProjectsPlace(String query) {
		this.query = query;
	}

	@Override
	public String getHeading() {
		return messages.discoverProjects();
	}

	public static ProjectsPlace createPlace() {
		return new ProjectsPlace(null);
	}

	public static ProjectsPlace createPlaceForQuery(String query) {
		return new ProjectsPlace(query);
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();

		if (query != null) {
			tokenMap.put(QUERY, query);
		}

		return Projects.getUrlForNamedArgs(tokenMap);
	}

	@Override
	public String getWindowTitle() {
		return WindowTitleBuilder.createWindowTitle(messages.browseProjects());
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();

		onPlaceDataFetched();
	}

	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}
}
