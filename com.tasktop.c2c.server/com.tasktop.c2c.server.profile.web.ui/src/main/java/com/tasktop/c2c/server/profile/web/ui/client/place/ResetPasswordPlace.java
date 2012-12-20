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

import com.tasktop.c2c.server.common.profile.web.client.navigation.AbstractPlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.place.AnonymousPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HeadingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.SignInPlace;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetPasswordResetTokenAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetPasswordResetTokenResult;
import com.tasktop.c2c.server.common.web.client.navigation.Args;
import com.tasktop.c2c.server.common.web.client.util.ExceptionsUtil;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;

public class ResetPasswordPlace extends AnonymousPlace implements HeadingPlace {

	public static PageMapping ResetPassword = new PageMapping(new ResetPasswordPlace.Tokenizer(), "resetPassword/{"
			+ SignUpPlace.TOKEN + "}");

	private static class Tokenizer extends AbstractPlaceTokenizer<ResetPasswordPlace> {

		@Override
		public ResetPasswordPlace getPlace(String token) {
			// Tokenize our URL now.
			Args pathArgs = getPathArgsForUrl(token);
			return createPlace(pathArgs.getString(SignUpPlace.TOKEN));
		}

	}

	public static ResetPasswordPlace createPlace(String resetToken) {
		return new ResetPasswordPlace(resetToken);
	}

	private String resetToken;
	private String username;
	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	private ResetPasswordPlace(String resetToken) {
		this.resetToken = resetToken;
	}

	public String getResetToken() {
		return resetToken;
	}

	public String getUsername() {
		return username;
	}

	@Override
	public String getHeading() {
		return profileMessages.passwordReset();
	}

	@Override
	public String getPrefix() {
		LinkedHashMap<String, String> tokenMap = new LinkedHashMap<String, String>();
		tokenMap.put(SignUpPlace.TOKEN, resetToken);
		return ResetPassword.getUrlForNamedArgs(tokenMap);
	}

	@Override
	protected void addActions() {
		super.addActions();
		addAction(new GetPasswordResetTokenAction(resetToken));
	}

	protected boolean handleExceptionInResults(Action<?> action, DispatchException dispatchException) {
		if (ExceptionsUtil.isEntityNotFound(dispatchException)) {
			SignInPlace.createPlace().go();
			return false;
		}
		return super.handleExceptionInResults(action, dispatchException);
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();

		GetPasswordResetTokenResult result = getResult(GetPasswordResetTokenResult.class);

		if (result.get() != null && result.get().getProfile() != null
				&& result.get().getProfile().getUsername() != null) {
			username = result.get().getProfile().getUsername();
			onPlaceDataFetched();
		} else {
			SignInPlace.createPlace().go();
		}

	}
}
