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

import com.google.gwt.editor.client.Editor;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ValueListBox;
import com.tasktop.c2c.server.profile.domain.project.Organization;
import com.tasktop.c2c.server.profile.domain.project.WikiMarkupLanguage;

/**
 * @author Myles (Tasktop Technologies Inc.)
 * 
 */
public abstract class AbstractOrganizationAdminView extends Composite implements Editor<Organization> {

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

}
