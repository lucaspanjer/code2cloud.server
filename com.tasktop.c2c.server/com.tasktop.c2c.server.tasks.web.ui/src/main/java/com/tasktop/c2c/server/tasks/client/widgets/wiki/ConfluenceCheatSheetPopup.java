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
package com.tasktop.c2c.server.tasks.client.widgets.wiki;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class ConfluenceCheatSheetPopup extends DecoratedPopupPanel {

	private static ConfluenceCheatSheetPopup instance;

	public static ConfluenceCheatSheetPopup getInstance() {
		if (instance == null) {
			instance = new ConfluenceCheatSheetPopup();
		}
		return instance;
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	interface Binder extends UiBinder<Widget, ConfluenceCheatSheetPopup> {
	}

	private ConfluenceCheatSheetPopup() {
		super(true);
		setWidget(uiBinder.createAndBindUi(this));
	}

}
