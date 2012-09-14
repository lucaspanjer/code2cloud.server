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
package com.tasktop.c2c.server.profile.web.ui.client.view.components.organization;

import java.util.Arrays;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.tasktop.c2c.server.profile.domain.project.Organization;
import com.tasktop.c2c.server.profile.domain.project.ProjectPreferences;
import com.tasktop.c2c.server.profile.domain.project.WikiMarkupLanguage;

public class OrganizationAdminEditView extends Composite implements Editor<Organization> {
	interface OrganizationAdminEditViewUiBinder extends UiBinder<HTMLPanel, OrganizationAdminEditView> {
	}

	interface Driver extends SimpleBeanEditorDriver<Organization, OrganizationAdminEditView> {
	}

	private static OrganizationAdminEditViewUiBinder ourUiBinder = GWT.create(OrganizationAdminEditViewUiBinder.class);

	private static OrganizationAdminEditView instance;
	public Driver driver = GWT.create(Driver.class);

	public static OrganizationAdminEditView getInstance() {
		if (instance == null) {
			instance = new OrganizationAdminEditView();
		}
		return instance;
	}

	public static interface Presenter {
		Organization getOrganization();

		void onSaveOrganization();

		void onCancelProjectEdit();
	}

	@UiField(provided = true)
	@Path("projectPreferences.wikiLanguage")
	protected ValueListBox<WikiMarkupLanguage> wikiLanguageListBox = new ValueListBox<WikiMarkupLanguage>(
			new AbstractRenderer<WikiMarkupLanguage>() {
				@Override
				public String render(WikiMarkupLanguage s) {
					return s.toString();
				}
			});

	protected void setOrganization(Organization organization) {
		wikiLanguageListBox.setValue(organization.getProjectPreferences().getWikiLanguage());
		wikiLanguageListBox.setAcceptableValues(Arrays.asList(WikiMarkupLanguage.values()));
	}

	@UiField
	@Path("description")
	TextArea projectDescription;
	@UiField
	@Path("name")
	TextBox projectName;
	@UiField
	Button cancelEditButton;
	@UiField
	Button saveButton;

	private Presenter presenter;

	public OrganizationAdminEditView() {
		initWidget(ourUiBinder.createAndBindUi(this));
		driver.initialize(this);
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		setOrganization(presenter.getOrganization());
		driver.edit(presenter.getOrganization());
	}

	@UiHandler("cancelEditButton")
	void onCancelEdit(ClickEvent event) {
		presenter.onCancelProjectEdit();
	}

	@UiHandler("saveButton")
	void onSave(ClickEvent event) {
		driver.flush();
		Organization organization = presenter.getOrganization();
		updateWikiLanguage(organization);

		presenter.onSaveOrganization();
	}

	private void updateWikiLanguage(Organization organization) {

		if (organization.getProjectPreferences() == null) {
			organization.setProjectPreferences(new ProjectPreferences());
		}
		organization.getProjectPreferences().setWikiLanguage(wikiLanguageListBox.getValue());
	}

}
