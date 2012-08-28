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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

public class ProjectIconPanel extends BaseProjectIconPanel {

	interface ProjectIconPanelUiBinder extends UiBinder<Widget, ProjectIconPanel> {
	}

	private static ProjectIconPanelUiBinder uiBinder = GWT.create(ProjectIconPanelUiBinder.class);

	public ProjectIconPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		super.initSuper();
	}
}
