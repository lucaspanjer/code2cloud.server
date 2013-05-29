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
package com.tasktop.c2c.server.profile.web.ui.client.presenter.components;

import com.tasktop.c2c.server.common.profile.web.client.ValidationUtils;
import com.tasktop.c2c.server.common.profile.web.client.place.SignInPlace;
import com.tasktop.c2c.server.common.web.client.notification.Message;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.common.web.shared.NoSuchEntityException;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.place.RequestPasswordResetPlace;
import com.tasktop.c2c.server.profile.web.ui.client.presenter.AbstractProfilePresenter;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.PasswordResetRequestView;

public class PasswordResetRequestPresenter extends AbstractProfilePresenter implements
		PasswordResetRequestView.Presenter {

	private final PasswordResetRequestView passwordResetRequestView;
	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	public PasswordResetRequestPresenter(PasswordResetRequestView view, RequestPasswordResetPlace place) {
		super(view);
		passwordResetRequestView = view;

	}

	@Override
	protected void bind() {
		passwordResetRequestView.setPresenter(this);
	}

	@Override
	public void requestPasswordReset(final String email) {

		if (email == null || email.isEmpty()) {
			AppGinjector.get.instance().getNotifier()
					.displayMessage(Message.createErrorMessage(profileMessages.enterEmail()));
			return;
		} else if (!ValidationUtils.isValidEmail(email)) {
			AppGinjector.get.instance().getNotifier()
					.displayMessage(Message.createErrorMessage(profileMessages.enterValidEmail()));
			return;
		}

		getProfileService().requestPasswordReset(email, new AsyncCallbackSupport<Boolean>() {
			@Override
			public void success(Boolean result) {
				onSuccessfulRequest(email);
			}

			@Override
			public void failure(Throwable exception) {
				if (exception instanceof NoSuchEntityException) {
					// Show the same message even if we don't find the email. Don't reveal who is actually a user.
					onSuccessfulRequest(email);
				} else {
					super.failure(exception);
				}
			}
		});
	}

	private void onSuccessfulRequest(String email) {
		SignInPlace.createPlace()
				.displayOnArrival(Message.createSuccessMessage(profileMessages.passwordResetInstructionsSent())).go();
	}
}
