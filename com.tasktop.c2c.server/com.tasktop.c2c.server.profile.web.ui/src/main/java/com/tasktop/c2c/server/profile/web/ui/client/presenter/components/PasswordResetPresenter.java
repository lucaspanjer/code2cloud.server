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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.profile.web.shared.Credentials;
import com.tasktop.c2c.server.common.web.client.notification.Message;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.common.web.shared.NoSuchEntityException;
import com.tasktop.c2c.server.profile.web.ui.client.event.LogonEvent;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.place.ResetPasswordPlace;
import com.tasktop.c2c.server.profile.web.ui.client.presenter.AbstractProfilePresenter;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.PasswordResetView;

public class PasswordResetPresenter extends AbstractProfilePresenter implements ClickHandler {

	private final PasswordResetView passwordResetView;
	private String passwordResetToken;
	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	public PasswordResetPresenter(PasswordResetView view, String passwordResetToken) {
		super(view);
		passwordResetView = view;
		this.passwordResetToken = passwordResetToken;

	}

	@Override
	protected void bind() {
		passwordResetView.submitButton.addClickHandler(this);
	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source == passwordResetView.submitButton) {
			String passwordConfirm = passwordResetView.passwordConfirm.getText();
			String password = passwordResetView.password.getText();
			if (password != null && password.length() > 0 && !password.equals(passwordConfirm)) {
				ProfileGinjector.get.instance().getNotifier()
						.displayMessage(Message.createErrorMessage(profileMessages.passwordAndConfirmationMustMatch()));
			} else {
				doPasswordReset();
			}
		}
	}

	private void doPasswordReset() {
		String newPassword = passwordResetView.password.getText();
		String token = this.passwordResetToken;
		getProfileService().resetPassword(token, newPassword, new AsyncCallbackSupport<Credentials>() {
			@Override
			public void success(Credentials result) {
				getEventBus().fireEvent(new LogonEvent(result));
				ProfileGinjector.get.instance().getPlaceProvider().getDefaultPlace()
						.displayOnArrival(Message.createSuccessMessage(profileMessages.passwordUpdatedSignedIn())).go();
			}

			@Override
			public void failure(Throwable exception) {
				if (exception instanceof NoSuchEntityException) {
					AppGinjector.get.instance().getNotifier()
							.displayMessage(Message.createErrorMessage(profileMessages.profileNotFound()));
				} else {
					super.failure(exception);
				}
			}
		});
	}

	/**
	 * @param place
	 */
	public void setPlace(Place place) {
		ResetPasswordPlace resetPasswordPlace = (ResetPasswordPlace) place;
		passwordResetView.username.setText(resetPasswordPlace.getUsername());
	}
}
