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
package com.tasktop.c2c.server.profile.web.ui.client.view.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.tasktop.c2c.server.common.web.client.view.AbstractComposite;

public class PasswordResetRequestView extends AbstractComposite {

	public interface Presenter {
		void requestPasswordReset(String email);
	}

	private static PasswordResetRequestView instance = null;

	public static PasswordResetRequestView getInstance() {
		if (instance == null) {
			instance = new PasswordResetRequestView();
		}
		return instance;
	}

	interface Binder extends UiBinder<Widget, PasswordResetRequestView> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	private Presenter presenter;

	@UiField
	protected TextBox email;
	@UiField
	protected Button submitButton;

	private PasswordResetRequestView() {
		initWidget(uiBinder.createAndBindUi(this));
		hookDefaultButton(submitButton);
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		email.setText("");
	}

	@UiHandler("submitButton")
	protected void onSubmit(ClickEvent ce) {
		presenter.requestPasswordReset(email.getValue());
	}
}
