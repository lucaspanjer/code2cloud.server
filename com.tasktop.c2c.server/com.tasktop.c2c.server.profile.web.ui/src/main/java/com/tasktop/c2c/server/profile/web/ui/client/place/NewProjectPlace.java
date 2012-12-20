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

import com.tasktop.c2c.server.common.profile.web.client.navigation.AbstractPlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.place.HeadingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.LoggedInPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.WindowTitlePlace;
import com.tasktop.c2c.server.common.profile.web.client.util.WindowTitleBuilder;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectCreateAvailableAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectCreateAvailableResult;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;

/**
 * @author straxus (Tasktop Technologies Inc.)
 * 
 */
public class NewProjectPlace extends LoggedInPlace implements HeadingPlace, WindowTitlePlace {

	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	private boolean createAvailable = false;

	public static PageMapping NewProject = new PageMapping(new NewProjectPlace.Tokenizer(), "newProject");

	private static class Tokenizer extends AbstractPlaceTokenizer<NewProjectPlace> {

		@Override
		public NewProjectPlace getPlace(String token) {
			return NewProjectPlace.createPlace();
		}

	}

	@Override
	public String getHeading() {
		return profileMessages.createProject();
	}

	protected NewProjectPlace() {
	}

	public static NewProjectPlace createPlace() {
		return new NewProjectPlace();
	}

	@Override
	public String getPrefix() {
		return NewProject.getUrl();
	}

	public boolean isCreateAvailable() {
		return createAvailable;
	}

	@Override
	public String getWindowTitle() {
		return WindowTitleBuilder.createWindowTitle(profileMessages.createProject());
	}

	@Override
	protected void addActions() {
		super.addActions();
		addAction(new GetProjectCreateAvailableAction());
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		createAvailable = getResult(GetProjectCreateAvailableResult.class).get();
		onPlaceDataFetched();
	}
}
