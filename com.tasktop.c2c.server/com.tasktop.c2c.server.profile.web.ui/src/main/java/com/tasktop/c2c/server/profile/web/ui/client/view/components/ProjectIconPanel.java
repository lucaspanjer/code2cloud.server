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
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.tasktop.c2c.server.common.profile.web.client.AuthenticationHelper;
import com.tasktop.c2c.server.profile.domain.project.Project;

public class ProjectIconPanel extends BaseProjectIconPanel {

	interface ProjectIconPanelUiBinder extends UiBinder<Widget, ProjectIconPanel> {
	}

	private static ProjectIconPanelUiBinder uiBinder = GWT.create(ProjectIconPanelUiBinder.class);

	@UiField
	public Anchor options;
	@UiField
	public DivElement optionsWrapper;
	protected ProjectOptionsPopupPanel popupPanel = null;

	public ProjectIconPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		popupPanel = new ProjectOptionsPopupPanel();
		popupPanel.addAutoHidePartner(options.getElement());
		popupPanel.setStyleName("");
	}

	private void setOptionsVisible(boolean visible) {
		UIObject.setVisible(optionsWrapper, visible);
	}

	@Override
	public void setProject(Project project) {
		super.setProject(project);

		setOptionsVisible(!AuthenticationHelper.isAnonymous());
		if (project != null) {
			popupPanel.setProject(project);
		}
	}

	@UiHandler("options")
	public void showMenu(ClickEvent e) {
		if (popupPanel.isShowing()) {
			popupPanel.hide();
		} else {
			popupPanel.showRelativeTo(options);
		}
	}

}
