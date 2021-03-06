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
import com.tasktop.c2c.server.common.profile.web.client.place.AnonymousPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HeadingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.IPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.WindowTitlePlace;
import com.tasktop.c2c.server.common.profile.web.client.util.WindowTitleBuilder;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProfileDataFromGitubConnectionAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProfileResult;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectForInvitationTokenAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectInvitationTokenAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectInvitationTokenResult;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetProjectResult;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetSignupTokenAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetSignupTokenRequiredAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetSignupTokenRequiredResult;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetSignupTokenResult;
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.notification.Message;
import com.tasktop.c2c.server.common.web.client.util.StringUtils;
import com.tasktop.c2c.server.profile.domain.project.Profile;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.domain.project.ProjectInvitationToken;
import com.tasktop.c2c.server.profile.domain.project.SignUpToken;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;

/**
 * @author straxus (Tasktop Technologies Inc.)
 * 
 */
public class SignUpPlace extends AnonymousPlace implements HeadingPlace, WindowTitlePlace {
	public static final String TOKEN = "token";

	public static PageMapping SignUp = new PageMapping(new SignUpPlace.Tokenizer(), "signup", "signup/{"
			+ SignUpPlace.TOKEN + "}");

	private static class Tokenizer extends AbstractPlaceTokenizer<SignUpPlace> {

		@Override
		public SignUpPlace getPlace(String token) {
			// Tokenize our URL now.
			Args pathArgs = getPathArgsForUrl(token);

			String signupToken = pathArgs.getString(TOKEN);

			if (StringUtils.hasText(signupToken)) {
				return createPlace(signupToken);
			} else {
				return createPlace();
			}
		}

	}

	private boolean tokenRequired;
	private final String signUpToken;
	private IPlace postSignUpPlace;
	private SignUpToken signUpTokenData;
	private ProjectInvitationToken projectInvitationTokenData;
	private Project projectForInvitationToken;
	private Profile githubProfile;
	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	public String getPrefix() {
		if (StringUtils.hasText(signUpToken)) {
			LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();
			tokenMap.put(TOKEN, signUpToken);
			return SignUp.getUrlForNamedArgs(tokenMap);
		} else {
			return SignUp.getUrl();
		}
	}

	public IPlace getPostSignUpPlace() {
		return postSignUpPlace;
	}

	public boolean isTokenRequired() {
		return tokenRequired;
	}

	public SignUpToken getSignUpTokenData() {
		return signUpTokenData;
	}

	public ProjectInvitationToken getProjectInvitationTokenData() {
		return projectInvitationTokenData;
	}

	public Profile getGithubProfile() {
		return githubProfile;
	}

	@Override
	public String getHeading() {
		return profileMessages.signUp();
	}

	private SignUpPlace(String signUpToken, IPlace postSignUpPlace) {
		if (postSignUpPlace == null) {
			postSignUpPlace = AppGinjector.get.instance().getPlaceProvider().getDefaultPlace();
		}

		this.signUpToken = signUpToken;
		this.postSignUpPlace = postSignUpPlace;
	}

	public static SignUpPlace createPlace() {
		return new SignUpPlace(null, null);
	}

	public static SignUpPlace createPlace(String signUpToken) {
		return new SignUpPlace(signUpToken, null);
	}

	public static SignUpPlace createPlace(String signUpToken, IPlace postSignUpPlace) {
		return new SignUpPlace(signUpToken, postSignUpPlace);
	}

	public String getSignUpToken() {
		return signUpToken;
	}

	@Override
	protected void addActions() {
		super.addActions();
		addAction(new GetSignupTokenRequiredAction());
		if (signUpToken != null) {
			addAction(new GetSignupTokenAction(signUpToken));
			addAction(new GetProjectInvitationTokenAction(signUpToken));
			addAction(new GetProjectForInvitationTokenAction(signUpToken));
		}
		addAction(new GetProfileDataFromGitubConnectionAction());
	}

	protected boolean handleExceptionInResults(Action<?> action, DispatchException dispatchException) {
		return true;
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		tokenRequired = getResult(GetSignupTokenRequiredResult.class).get();

		GetSignupTokenResult signupTokenResult = getResult(GetSignupTokenResult.class);
		if (signupTokenResult != null) {
			signUpTokenData = signupTokenResult.get();
		}
		GetProjectInvitationTokenResult projectTokenResult = getResult(GetProjectInvitationTokenResult.class);
		if (projectTokenResult != null) {
			projectInvitationTokenData = projectTokenResult.get();
		}
		GetProjectResult projectResult = getResult(GetProjectResult.class);
		if (projectResult != null) {
			projectForInvitationToken = projectResult.get();
			postSignUpPlace = ProjectInvitationPlace.createPlace(signUpToken);
		}
		GetProfileResult gitHubProfileResult = getResult(GetProfileResult.class);
		if (gitHubProfileResult != null) {
			githubProfile = gitHubProfileResult.get();
		}

		if (tokenRequired && !StringUtils.hasText(signUpToken)) {
			ProfileGinjector.get.instance().getPlaceProvider().getDefaultPlace()
					.displayOnArrival(Message.createErrorMessage(profileMessages.tokenRequiredForSignUp())).go();
			return;
		} else if (tokenRequired && projectInvitationTokenData == null && signUpTokenData == null) {
			ProfileGinjector.get.instance().getPlaceProvider().getDefaultPlace()
					.displayOnArrival(Message.createErrorMessage(profileMessages.invitationTokenNotValid())).go();
			return;
		}

		onPlaceDataFetched();
	}

	@Override
	public String getWindowTitle() {
		return WindowTitleBuilder.createWindowTitle(profileMessages.signUp());
	}

	/**
	 * @return the projectForInvitationToken
	 */
	public Project getProjectForInvitationToken() {
		return projectForInvitationToken;
	}

}
