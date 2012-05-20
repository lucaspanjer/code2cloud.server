/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 ******************************************************************************/
package com.tasktop.c2c.server.scm.web.ui.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.domain.DiffEntry;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class DiffEntryView extends Composite implements Editor<Commit> {

	interface Binder extends UiBinder<Widget, DiffEntryView> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	@UiField
	protected Label operationName;
	@UiField
	protected Label fileName;

	@UiField
	protected Label numAdded;
	@UiField
	protected Label numRemoved;

	public DiffEntryView(DiffEntry diff) {
		initWidget(uiBinder.createAndBindUi(this));
		renderDiff(diff);
	}

	/**
	 * @param diff
	 */
	private void renderDiff(DiffEntry diff) {
		String opText = null;
		String filename = null;
		switch (diff.getChangeType()) {
		case ADD:
			opText = "Added";
			filename = diff.getNewPath();
			break;
		case MODIFY:
			opText = "Modified";
			filename = diff.getNewPath();
			break;
		case DELETE:
			opText = "Removed";
			filename = diff.getOldPath();
			break;
		case COPY:
		case RENAME:
			opText = "Moved";
			filename = diff.getNewPath();
			break;
		}
		operationName.setText(opText);
		fileName.setText(filename);

		if (diff.getLinesAdded() == 0) {
			numAdded.setVisible(false);
		} else {
			numAdded.setText("+" + diff.getLinesAdded());
		}

		if (diff.getLinesRemoved() == 0) {

		} else {
			numRemoved.setText("-" + diff.getLinesRemoved());
		}

	}
}