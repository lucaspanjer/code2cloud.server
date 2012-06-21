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
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.domain.DiffEntry;
import com.tasktop.c2c.server.scm.web.ui.client.resources.ScmResources;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class DiffEntryView extends Composite implements Editor<Commit> {

	interface Binder extends UiBinder<Widget, DiffEntryView> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	@UiField
	protected Image operationImage;
	@UiField
	protected Label renameFrom;
	@UiField
	protected Anchor fileName;

	@UiField
	protected Label numAdded;
	@UiField
	protected Label numRemoved;

	private int maxFileNameSize = 100;

	public DiffEntryView(DiffEntry diff) {
		initWidget(uiBinder.createAndBindUi(this));
		renderDiff(diff);
	}

	/**
	 * @param diff
	 */
	private void renderDiff(DiffEntry diff) {
		ImageResource opRes;
		String filename = null;
		renameFrom.setVisible(false);
		switch (diff.getChangeType()) {
		case MODIFY:
		case ADD:
		default:
			opRes = ScmResources.get.changeIcon();
			filename = diff.getNewPath();
			break;
		case DELETE:
			opRes = ScmResources.get.deleteIcon();
			filename = diff.getOldPath();
			break;
		case RENAME:
			renameFrom.setVisible(true);
			renameFrom.setText(trimFilename(diff.getOldPath()) + " renamed to ");
			opRes = ScmResources.get.changeIcon();
			filename = diff.getNewPath();
			break;
		case COPY:
			renameFrom.setVisible(true);
			renameFrom.setText(trimFilename(diff.getOldPath()) + " copied to ");
			opRes = ScmResources.get.changeIcon();
			filename = diff.getNewPath();
		}
		operationImage.setResource(opRes);
		fileName.setText(trimFilename(filename));

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

	private String trimFilename(String theFilename) {
		if (theFilename.length() > maxFileNameSize) {
			int start = theFilename.length() - maxFileNameSize;
			return "..." + theFilename.substring(start);
		}
		return theFilename;
	}

	public Anchor getFileNameAnchor() {
		return fileName;
	}
}
