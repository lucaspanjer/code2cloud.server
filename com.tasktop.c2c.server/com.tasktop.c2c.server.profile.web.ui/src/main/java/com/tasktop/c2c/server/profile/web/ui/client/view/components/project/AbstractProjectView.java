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

import java.util.Arrays;

import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.ValueListBox;
import com.tasktop.c2c.server.profile.domain.project.Project;
import com.tasktop.c2c.server.profile.domain.project.ProjectAccessibility;
import com.tasktop.c2c.server.profile.domain.project.ProjectPreferences;
import com.tasktop.c2c.server.profile.domain.project.WikiMarkupLanguage;

/**
 * @author clint.morgan@tasktop.com (Tasktop Technologies Inc.)
 * 
 */
public abstract class AbstractProjectView extends Composite implements Editor<Project> {

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
	@UiField(provided = true)
	@Path("projectPreferences.wikiLanguage")
	protected ValueListBox<WikiMarkupLanguage> wikiLanguageListBox = new ValueListBox<WikiMarkupLanguage>(
			new AbstractRenderer<WikiMarkupLanguage>() {

				@Override
				public String render(WikiMarkupLanguage s) {
					return s.toString();
				}

			});

	protected void setProject(Project project) {
		privacyPublicOption.setValue(project.getAccessibility().equals(ProjectAccessibility.PUBLIC));
		privacyPrivateOption.setValue(project.getAccessibility().equals(ProjectAccessibility.PRIVATE));
		privacyOrgPrivateOption.setValue(project.getAccessibility().equals(ProjectAccessibility.ORGANIZATION_PRIVATE));
		UIObject.setVisible(orgPrivatePElement, project.getOrganization() != null);

		wikiLanguageListBox.setValue(project.getProjectPreferences().getWikiLanguage());
		wikiLanguageListBox.setAcceptableValues(Arrays.asList(WikiMarkupLanguage.values()));
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

		if (project.getProjectPreferences() == null) {
			project.setProjectPreferences(new ProjectPreferences());
		}

		project.getProjectPreferences().setWikiLanguage(wikiLanguageListBox.getValue());
	}

}
