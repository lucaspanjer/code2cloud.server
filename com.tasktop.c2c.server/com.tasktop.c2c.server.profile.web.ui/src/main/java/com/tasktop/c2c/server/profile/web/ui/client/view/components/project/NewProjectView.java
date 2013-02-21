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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.FieldSetElement;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.ValueListBox;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.domain.project.ProjectTemplate;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;

public class NewProjectView extends AbstractProjectView {

	public interface Presenter {
		void createProject(Project p, ProjectTemplate templateOrNull);

		void doCancel();

		/**
		 * @param project
		 */
		void createProject(Project project);
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

	@UiField(provided = true)
	@Ignore
	public ValueListBox<ProjectTemplate> projectTemplate = new ValueListBox<ProjectTemplate>(
			new AbstractRenderer<ProjectTemplate>() {

				@Override
				public String render(ProjectTemplate template) {
					if (template == null) {
						return AppGinjector.get.instance().getProfileMessages().selectATemplate();
					}
					return template.getName();
				}
			});
	@UiField
	public FieldSetElement projectTemplateFieldSet;
	@UiField
	@Ignore
	public CheckBox createFromTemplate;

	private Project project;

	private Presenter presenter;

	private NewProjectView() {
		initWidget(uiBinder.createAndBindUi(this));
		driver.initialize(this);
		createButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (createFromTemplate.getValue()) {
					presenter.createProject(getProject(), projectTemplate.getValue());
				} else {
					presenter.createProject(getProject());
				}
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

	public Button getCreateButton() {
		return createButton;
	}

	public Project getProject() {
		driver.flush();
		super.updateProject(project);
		return project;
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	/**
	 * @param projectTemplates
	 */
	public void setProjectTemplates(List<ProjectTemplate> projectTemplates) {

		boolean enabled = projectTemplates != null && !projectTemplates.isEmpty();
		UIObject.setVisible(projectTemplateFieldSet, enabled);
		createFromTemplate.setValue(false);
		onTemplateToggle(null); // propagate events on above call does not seem to

		if (enabled) {
			projectTemplate.setValue(null);
			ArrayList<ProjectTemplate> values = new ArrayList<ProjectTemplate>(projectTemplates);
			values.add(0, null);
			projectTemplate.setAcceptableValues(values);
		}
	}

	@UiHandler("createFromTemplate")
	public void onTemplateToggle(ClickEvent e) {
		DOM.setElementPropertyBoolean(projectTemplate.getElement(), "disabled", !createFromTemplate.getValue());
	}
}
