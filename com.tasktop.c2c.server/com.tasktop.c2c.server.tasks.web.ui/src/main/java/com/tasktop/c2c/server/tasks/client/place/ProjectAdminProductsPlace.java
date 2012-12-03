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
package com.tasktop.c2c.server.tasks.client.place;

import java.util.LinkedHashMap;

import com.tasktop.c2c.server.common.profile.web.client.navigation.AbstractPlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.navigation.Path;

public class ProjectAdminProductsPlace extends AbstractProjectAdminTasksPlace {

	public static PageMapping ProjectTaskAdminProducts = new PageMapping(new Tokenizer(), Path.PROJECT_BASE + "/{"
			+ Path.PROJECT_ID + "}/admin/tasks/products");

	private static class Tokenizer extends AbstractPlaceTokenizer<ProjectAdminProductsPlace> {

		@Override
		public ProjectAdminProductsPlace getPlace(String token) {
			// Tokenize our URL now.
			Args pathArgs = getPathArgsForUrl(token);

			return createPlace(pathArgs.getString(Path.PROJECT_ID));
		}

	}

	public ProjectAdminProductsPlace(String projectIdentifier) {
		super(projectIdentifier);
	}

	public static ProjectAdminProductsPlace createPlace(String projectId) {
		return new ProjectAdminProductsPlace(projectId);
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();

		tokenMap.put(Path.PROJECT_ID, projectIdentifer);

		return ProjectTaskAdminProducts.getUrlForNamedArgs(tokenMap);
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		onPlaceDataFetched();
	}

}
