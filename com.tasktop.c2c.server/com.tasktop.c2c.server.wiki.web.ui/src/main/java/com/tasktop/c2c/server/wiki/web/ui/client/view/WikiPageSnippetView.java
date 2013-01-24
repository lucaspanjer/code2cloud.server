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
package com.tasktop.c2c.server.wiki.web.ui.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tasktop.c2c.server.common.web.client.widgets.Format;
import com.tasktop.c2c.server.wiki.domain.Page;
import com.tasktop.c2c.server.wiki.web.ui.client.WikiMessages;
import com.tasktop.c2c.server.wiki.web.ui.client.place.ProjectWikiViewPagePlace;

public class WikiPageSnippetView extends Composite {

	interface Binder extends UiBinder<Widget, WikiPageSnippetView> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);
	private WikiMessages wikiMessages = GWT.create(WikiMessages.class);

	@UiField
	Anchor title;
	@UiField
	Label snippet;
	@UiField
	Label createdLabel;
	@UiField
	Label modifiedLabel;

	public WikiPageSnippetView(String projectIdentifier, Page page) {
		initWidget(uiBinder.createAndBindUi(this));

		title.setText(page.getPath());
		title.setHref(ProjectWikiViewPagePlace.createPlaceForPage(projectIdentifier, page.getPath()).getHref());
		Integer length = page.getContent().length();
		if (page.getContent().length() >= 250) {
			length = 250;
		}
		snippet.setText(page.getContent().substring(0, length));
		createdLabel.setText(wikiMessages.createdBy(Format.stringValueDateTime(page.getCreationDate()), page
				.getOriginalAuthor().getName()));
		modifiedLabel.setText(wikiMessages.changedBy(Format.stringValueDateTime(page.getModificationDate()), page
				.getLastAuthor().getName()));
	}
}
