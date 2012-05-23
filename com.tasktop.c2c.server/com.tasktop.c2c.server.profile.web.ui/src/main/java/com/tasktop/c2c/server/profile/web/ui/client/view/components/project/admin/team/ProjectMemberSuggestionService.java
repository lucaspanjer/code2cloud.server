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
package com.tasktop.c2c.server.profile.web.ui.client.view.components.project.admin.team;

import com.tasktop.c2c.server.common.profile.web.client.presenter.person.PersonUtil;
import com.tasktop.c2c.server.common.profile.web.shared.actions.FindProfilesResult;
import com.tasktop.c2c.server.common.profile.web.shared.actions.FindProfilesToAddToProjectAction;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.common.web.client.widgets.chooser.ValueSuggestionService;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;

/** Suggest new members to add to a project. */
public class ProjectMemberSuggestionService implements ValueSuggestionService {

	private String projectIdentifier;

	public ProjectMemberSuggestionService() {
	}

	@Override
	public void suggest(String query, int limit, final Callback callback) {
		AppGinjector.get.instance().getDispatchService()
				.execute(new FindProfilesToAddToProjectAction(projectIdentifier, query, limit),

				new AsyncCallbackSupport<FindProfilesResult>() {

					@Override
					protected void success(FindProfilesResult result) {
						callback.onSuggestionsReady(PersonUtil.toPeople(result.get().getResultPage()));

					}
				});

	}

	@Override
	public void suggest(Callback callback) {
		suggest("", 10, callback);
	}

	public void setProjectIdentifier(String projectIdentifier) {
		this.projectIdentifier = projectIdentifier;
	}

}
