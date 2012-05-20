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
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.ui.client.adapters.HasTextEditor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DateLabel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.tasktop.c2c.server.common.web.client.view.Avatar;
import com.tasktop.c2c.server.common.web.client.widgets.Format;
import com.tasktop.c2c.server.common.web.client.widgets.hyperlink.HyperlinkingLabel;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.domain.DiffEntry;
import com.tasktop.c2c.server.scm.web.ui.client.place.ScmCommitPlace;
import com.tasktop.c2c.server.scm.web.ui.client.place.ScmRepoPlace;
import com.tasktop.c2c.server.tasks.client.widgets.TaskHyperlinkDetector;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class ScmCommitView extends Composite implements Editor<Commit> {

	private static ScmCommitView instance;

	public static ScmCommitView getInstance() {
		if (instance == null) {
			instance = new ScmCommitView();
		}
		return instance;
	}

	interface Binder extends UiBinder<Widget, ScmCommitView> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	interface Driver extends SimpleBeanEditorDriver<Commit, ScmCommitView> {
	}

	private Driver driver = GWT.create(Driver.class);

	private ScmCommitView() {
		initWidget(uiBinder.createAndBindUi(this));
		comment = HasTextEditor.of(commentLabel);
		driver.initialize(this);
		patchPanel.getHeader().getElement().getParentElement().setClassName(""); // prevent style collision
		changesPanel.getHeader().getElement().getParentElement().setClassName(""); // prevent style collision
		commentLabel.addHyperlinkDetector(taskHyperlinkDetector);

	}

	@UiField
	protected Label commitId;
	@UiField
	@Path("author.email")
	protected Label authorEmail;
	@UiField
	protected Image authorImage;
	@UiField(provided = true)
	protected DateLabel date = new DateLabel(Format.getDateTimeFormat());
	@UiField
	protected HyperlinkingLabel commentLabel;
	private TaskHyperlinkDetector taskHyperlinkDetector = new TaskHyperlinkDetector(null);
	protected HasTextEditor comment;
	@UiField
	protected Panel parentsPanel;
	@UiField
	protected Panel filesPanel;
	@UiField
	protected DisclosurePanel changesPanel;
	@UiField
	protected Label diffText;
	@UiField
	protected Anchor repository;
	@UiField
	protected DisclosurePanel patchPanel;
	@UiField
	protected DivElement committerInfoDiv;
	@Path("committer.email")
	@UiField
	protected Label committerEmail;
	@UiField(provided = true)
	protected DateLabel commitDate = new DateLabel(Format.getDateTimeFormat());
	private String projectId;

	/**
	 * @param commit
	 */
	public void setCommit(Commit commit) {
		driver.edit(commit);

		repository.setText(commit.getRepository());
		repository.setHref(ScmRepoPlace.createPlace(projectId, commit.getRepository()).getHref());

		parentsPanel.clear();
		if (commit.getParents().isEmpty()) {
			patchPanel.add(new Label("None"));
		} else {
			boolean needSep = false;
			for (String parentId : commit.getParents()) {
				if (needSep) {
					parentsPanel.add(new Label(","));
				} else {
					needSep = true;
				}
				parentsPanel.add(new Anchor(Commit.minimizeCommitId(parentId), ScmCommitPlace.createPlace(projectId,
						commit.getRepository(), parentId).getHref()));
			}
		}

		filesPanel.clear();
		if (commit.getChanges() != null) {
			for (DiffEntry diffEntry : commit.getChanges()) {
				filesPanel.add(new DiffEntryView(diffEntry));
			}
			patchPanel.setVisible(true);
			changesPanel.setVisible(true);
		} else {
			patchPanel.setVisible(false);
			changesPanel.setVisible(false);
		}

		authorImage.setUrl(Avatar.computeAvatarUrl(commit.getAuthor().getGravatarHash(), Avatar.Size.MEDIUM));

		UIObject.setVisible(committerInfoDiv, commit.getCommitter() != null);

	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
		taskHyperlinkDetector.setProjectIdentity(projectId);
	}

}
