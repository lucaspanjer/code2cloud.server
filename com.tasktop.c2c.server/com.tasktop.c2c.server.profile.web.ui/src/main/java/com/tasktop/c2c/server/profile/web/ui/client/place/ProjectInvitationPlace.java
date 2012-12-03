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

import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.profile.web.client.navigation.AbstractPlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.place.AbstractBatchFetchingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HasProjectPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HeadingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.ProjectsPlace;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectForInvitationTokenAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectResult;
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.notification.Message;
import com.tasktop.c2c.server.profile.domain.project.Project;

public class ProjectInvitationPlace extends AbstractBatchFetchingPlace implements HeadingPlace, HasProjectPlace {

	public static PageMapping ProjectInvitation = new PageMapping(new ProjectInvitationPlace.Tokenizer(),
			"invitation/{" + SignUpPlace.TOKEN + "}");

	public static class Tokenizer extends AbstractPlaceTokenizer<ProjectInvitationPlace> {

		@Override
		public ProjectInvitationPlace getPlace(String token) {
			// Tokenize our URL now.
			Args pathArgs = getPathArgsForUrl(token);
			return createPlace(pathArgs.getString(SignUpPlace.TOKEN));
		}

	}

	private final String invitationToken;
	private Project project;

	public static ProjectInvitationPlace createPlace(String invitationToken) {
		return new ProjectInvitationPlace(invitationToken);
	}

	private ProjectInvitationPlace(String invitationToken) {
		this.invitationToken = invitationToken;
	}

	@Override
	public Project getProject() {
		return project;
	}

	@Override
	public String getHeading() {
		return "Invitation to join " + project.getName();
	}

	public String getInvitationToken() {
		return invitationToken;
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();
		tokenMap.put(SignUpPlace.TOKEN, invitationToken);
		return ProjectInvitation.getUrlForNamedArgs(tokenMap);
	}

	@Override
	protected void addActions() {
		super.addActions();
		addAction(new GetProjectForInvitationTokenAction(invitationToken));
	}

	protected boolean handleExceptionInResults(Action<?> action, DispatchException dispatchException) {
		// Assume its a token not found
		ProjectsPlace.createPlace().displayOnArrival(Message.createErrorMessage("Invitation token is not valid.")).go();
		return false;
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		project = getResult(GetProjectResult.class).get();

		if (ProfileGinjector.get.instance().getAppState().isUserAnonymous()) {
			SignUpPlace.createPlace(getInvitationToken()).go(); // Will come back here after signup.
		} else {
			onPlaceDataFetched();
		}
	}
}
