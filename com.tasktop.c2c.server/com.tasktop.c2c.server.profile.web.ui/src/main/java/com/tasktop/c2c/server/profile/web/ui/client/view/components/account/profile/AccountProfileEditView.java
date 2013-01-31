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
package com.tasktop.c2c.server.profile.web.ui.client.view.components.account.profile;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.FieldSetElement;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.ValueListBox;
import com.tasktop.c2c.server.profile.domain.project.Profile;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.account.presenter.IAccountView;

public class AccountProfileEditView extends AbstractAccountProfileView implements Editor<Profile>,
		IAccountView<IAccountView.AccountProfilePresenter> {

	interface ProfileViewUiBinder extends UiBinder<HTMLPanel, AccountProfileEditView> {
	}

	interface Driver extends SimpleBeanEditorDriver<Profile, AccountProfileEditView> {
	}

	private static AccountProfileEditView instance;

	public static AccountProfileEditView getInstance() {
		if (instance == null) {
			instance = new AccountProfileEditView();
		}
		return instance;
	}

	private static ProfileViewUiBinder ourUiBinder = GWT.create(ProfileViewUiBinder.class);
	@UiField
	@Path("username")
	Label userNameField;
	@UiField
	@Path("email")
	TextBox emailField;
	@UiField
	@Path("firstName")
	TextBox firstNameField;
	@UiField
	@Path("lastName")
	TextBox lastNameField;
	@UiField(provided = true)
	@Path("language")
	ValueListBox<String> languageField = new ValueListBox<String>(new AbstractRenderer<String>() {
		@Override
		public String render(String value) {
			return value != null ? LocaleInfo.getLocaleNativeDisplayName(value) : "";
		}
	});
	@UiField
	Button cancelButton;
	@UiField
	Button saveButton;
	@UiField
	@Ignore
	HTML imageFromGravatarLabel;
	@UiField
	FieldSetElement languageFieldSet;

	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	private static Driver driver = GWT.create(Driver.class);

	public AccountProfileEditView() {
		initWidget(ourUiBinder.createAndBindUi(this));
		String[] localeNames = LocaleInfo.getAvailableLocaleNames();
		// sort the locale names and filter out the "default" value
		List<String> filteredLocaleNames = new ArrayList<String>();
		for (String name : localeNames) {
			if (!"default".equals(name)) {
				filteredLocaleNames.add(name);
			}
		}
		if (!filteredLocaleNames.isEmpty()) {
			languageField.setValue(filteredLocaleNames.get(0));
			languageField.setAcceptableValues(filteredLocaleNames);
		}
		if (filteredLocaleNames.size() < 2) {
			UIObject.setVisible(languageFieldSet, false);
		}
		imageFromGravatarLabel.setHTML(profileMessages.imageFromGravatar());
		driver.initialize(this);
	}

	@Override
	public void setPresenter(AccountProfilePresenter presenter) {
		super.setPresenter(presenter);
		driver.edit(presenter.getProfile());
	}

	@UiHandler("saveButton")
	void onSave(ClickEvent event) {
		if (driver.isDirty()) {
			driver.flush();
			presenter.saveProfile();
		}
	}

	@UiHandler("cancelButton")
	void onEditCancel(ClickEvent event) {
		driver.edit(presenter.getProfile());
	}

}
