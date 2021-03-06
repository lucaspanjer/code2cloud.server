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
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.scm.domain.ScmRepository;

public class ProjectScmRepositoryRow extends Composite {

	interface Binder extends UiBinder<Widget, ProjectScmRepositoryRow> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	private String organizationIdentifier;

	@UiField
	Label repoLabel;
	@UiField
	TextBox url1TextBox;
	@UiField
	TextBox url2TextBox;

	public ProjectScmRepositoryRow(ScmRepository repository, String organizationIdentifier) {
		this.organizationIdentifier = organizationIdentifier;
		initWidget(uiBinder.createAndBindUi(this));

		String repositoryName = repository.getUrl();
		if (repositoryName.lastIndexOf('/') != -1) {
			repositoryName = repositoryName.substring(repositoryName.lastIndexOf('/') + 1);
		}
		repoLabel.setText(repositoryName);

		url1TextBox.setValue(addUserToUrl(repository, repository.getUrl()));

		if (repository.getAlternateUrl() != null) {
			url2TextBox.setText(addUserToUrl(repository, repository.getAlternateUrl()));
		} else {
			url2TextBox.setVisible(false);
		}

		ProjectView.setupSelectOnClick(url1TextBox);
		ProjectView.setupSelectOnClick(url2TextBox);
	}

	private String addUserToUrl(ScmRepository repository, String url) {
		return AppGinjector.get.instance().getScmRepositoryUrlBuilder().buildUrl(repository, url, organizationIdentifier);
	}
}
