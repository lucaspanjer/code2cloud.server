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
package com.tasktop.c2c.server.profile.web.ui.client.view.components.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.tasktop.c2c.server.profile.domain.project.Project;

public class NewProjectView extends AbstractProjectView {

	public interface Presenter {
		void createProject(Project p);

		void doCancel();
	}

	private static NewProjectView instance = null;

	public static NewProjectView getInstance() {
		if (instance == null) {
			instance = new NewProjectView();
		}
		return instance;
	}

	interface NewProjectViewUiBinder extends UiBinder<HTMLPanel, NewProjectView> {
	}

	interface Driver extends SimpleBeanEditorDriver<Project, NewProjectView> {
	}

	private static Driver driver = GWT.create(Driver.class);

	private static NewProjectViewUiBinder uiBinder = GWT.create(NewProjectViewUiBinder.class);

	@UiField
	protected Panel newProjectForm;
	@UiField
	protected TextBox name;
	@UiField
	protected TextArea description;
	@UiField
	protected Button createButton;
	@UiField
	protected Button cancelButton;
	@UiField
	public Panel maxProjectsMessagePanel;
	private Project project;

	private Presenter presenter;

	private NewProjectView() {
		initWidget(uiBinder.createAndBindUi(this));
		driver.initialize(this);
		createButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.createProject(getProject());
			}
		});

		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.doCancel();
			}
		});
	}

	public void displayMaxProjectsMessage(boolean show) {
		// Only show one of these two controls at a time.
		this.maxProjectsMessagePanel.setVisible(show);
		this.newProjectForm.setVisible(!show);
	}

	public void setProject(Project project) {
		this.project = project;
		driver.edit(project);
		super.setProject(project);
	}

	public Project getProject() {
		driver.flush();
		super.updateProject(project);
		return project;
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}
