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
package com.tasktop.c2c.server.profile.web.ui.client.view;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

public class FooterView extends Composite implements Footer {

	interface Binder extends UiBinder<HTMLPanel, FooterView> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	@UiField
	protected Label year;

	public FooterView() {

		initWidget(uiBinder.createAndBindUi(this));

		// Automatically calculate our copyright year
		DateTimeFormat dtf = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR);
		year.setText(dtf.format(new Date()));

	}

}
