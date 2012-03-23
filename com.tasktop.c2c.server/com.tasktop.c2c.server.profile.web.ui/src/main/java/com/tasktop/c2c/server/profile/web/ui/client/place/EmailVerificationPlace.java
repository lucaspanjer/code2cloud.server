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

import com.google.gwt.place.shared.PlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.place.LoggedInPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.ProjectsPlace;
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.notification.Message;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.navigation.PageMappings;

public class EmailVerificationPlace extends LoggedInPlace {

	public static class Tokenizer implements PlaceTokenizer<EmailVerificationPlace> {

		@Override
		public EmailVerificationPlace getPlace(String token) {
			// Tokenize our URL now.
			Args pathArgs = PageMapping.getPathArgsForUrl(token);
			return createPlace(pathArgs.getString(SignUpPlace.TOKEN));
		}

		@Override
		public String getToken(EmailVerificationPlace place) {
			return place.getToken();
		}
	}

	public static EmailVerificationPlace createPlace(String verificationToken) {
		return new EmailVerificationPlace(verificationToken);
	}

	private String verificationToken;

	private EmailVerificationPlace(String verificationToken) {
		this.verificationToken = verificationToken;
	}

	@Override
	public String getToken() {
		return "";
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();
		tokenMap.put(SignUpPlace.TOKEN, verificationToken);
		return PageMappings.VerifyEmail.getUrlForNamedArgs(tokenMap);
	}

	// TODO convert to batch fetching

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();

		AppGinjector.get.instance().getProfileService()
				.verifyEmailToken(verificationToken, new AsyncCallbackSupport<Void>() {

					@Override
					public void success(Void result) {
						ProjectsPlace
								.createPlace()
								.displayOnArrival(
										Message.createSuccessMessage("Your email address has been verified. ")).go();
					}
				});
	}
}
