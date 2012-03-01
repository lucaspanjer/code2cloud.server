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

import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.editor.client.Editor.Ignore;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.UIObject;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.domain.project.ProjectAccessibility;

/**
 * @author clint.morgan@tasktop.com (Tasktop Technologies Inc.)
 * 
 */
public abstract class AbstractProjectView extends Composite {

	@UiField
	@Ignore
	protected RadioButton privacyPublicOption;
	@UiField
	@Ignore
	protected RadioButton privacyPrivateOption;
	@UiField
	@Ignore
	protected RadioButton privacyOrgPrivateOption;
	@UiField
	protected ParagraphElement orgPrivatePElement;

	protected void setProject(Project project) {
		privacyPublicOption.setValue(project.getAccessibility().equals(ProjectAccessibility.PUBLIC));
		privacyPrivateOption.setValue(project.getAccessibility().equals(ProjectAccessibility.PRIVATE));
		privacyOrgPrivateOption.setValue(project.getAccessibility().equals(ProjectAccessibility.ORGANIZATION_PRIVATE));
		UIObject.setVisible(orgPrivatePElement, project.getOrganization() != null);
	}

	/**
	 * @param project
	 */
	protected void updateProject(Project project) {
		if (privacyPublicOption.getValue()) {
			project.setAccessibility(ProjectAccessibility.PUBLIC);
		} else if (privacyPrivateOption.getValue()) {
			project.setAccessibility(ProjectAccessibility.PRIVATE);
		} else if (privacyOrgPrivateOption.getValue()) {
			project.setAccessibility(ProjectAccessibility.ORGANIZATION_PRIVATE);
		}
	}

}
