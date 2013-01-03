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
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.tasktop.c2c.server.common.profile.web.client.CommonProfileMessages;
import com.tasktop.c2c.server.common.profile.web.client.util.TextBoxUtil;
import com.tasktop.c2c.server.common.web.client.view.AbstractComposite;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.presenter.components.SignInPresenter;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;

public class SignInView extends AbstractComposite {

	private static SignInView instance = null;

	public static SignInView getInstance() {
		if (instance == null) {
			instance = new SignInView();
		}
		return instance;
	}

	interface LogonViewUiBinder extends UiBinder<Widget, SignInView> {
	}

	private static LogonViewUiBinder uiBinder = GWT.create(LogonViewUiBinder.class);

	@UiField
	public TextBox username;
	@UiField
	public PasswordTextBox password;
	@UiField
	public Button logonButton;
	@UiField
	public CheckBox rememberMe;
	@UiField
	public Anchor requestPasswordReset;
	@UiField
	public Button githubButton;
	@UiField(provided = true)
	public FormPanel githubForm;
	@UiField
	DivElement gitHubDiv;
	@UiField
	HTML gitHubSignInDescription;

	private SignInPresenter presenter;

	private CommonProfileMessages commonProfileMessages = AppGinjector.get.instance().getCommonProfileMessages();
	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	private SignInView() {
		// Give our Github form a target of "_self" - that will ensure that it replaces the current page when the
		// redirect to GitHub happens (which is what we want).
		this.githubForm = new FormPanel(new NamedFrame("_self"));

		initWidget(uiBinder.createAndBindUi(this));
		hookDefaultButton(logonButton);
		TextBoxUtil.turnOffAutoCorrect(username);

		if (!AppGinjector.get.instance().getConfiguration().isEnableGitHubAuth()) {
			UIObject.setVisible(gitHubDiv, false);
		}

		gitHubSignInDescription.setHTML(profileMessages.gitHubSignInDescription(commonProfileMessages.code2Cloud()));
	}

	@UiHandler("githubButton")
	void submitForm(ClickEvent clickEvt) {
		githubForm.submit();
	}

	@UiHandler("logonButton")
	void logonButton(ClickEvent clickEvent) {
		presenter.doLogon();
	}

	public void clearForm() {
		username.setText("");
		password.setText("");
	}

	public void setPresenter(SignInPresenter presenter) {
		this.presenter = presenter;
	}
}
