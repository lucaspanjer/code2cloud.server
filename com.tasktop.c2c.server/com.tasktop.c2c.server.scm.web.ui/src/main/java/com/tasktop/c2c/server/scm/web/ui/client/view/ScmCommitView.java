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

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.ui.client.adapters.HasTextEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DateLabel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.google_code_prettify.GoogleCodePrettifyUtil;
import com.tasktop.c2c.server.common.web.client.navigation.Navigation;
import com.tasktop.c2c.server.common.web.client.view.Avatar;
import com.tasktop.c2c.server.common.web.client.widgets.Format;
import com.tasktop.c2c.server.common.web.client.widgets.hyperlink.HyperlinkingLabel;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.domain.DiffEntry;
import com.tasktop.c2c.server.scm.domain.DiffEntry.Content;
import com.tasktop.c2c.server.scm.web.ui.client.place.ScmCommitPlace;
import com.tasktop.c2c.server.scm.web.ui.client.place.ScmRepoPlace;
import com.tasktop.c2c.server.scm.web.ui.client.resources.ScmMessages;
import com.tasktop.c2c.server.scm.web.ui.client.resources.ScmResources;
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
		ScmResources.get.style().ensureInjected();

		patchPanel.getContent().removeStyleName("content");
		changesPanel.getContent().removeStyleName("content");

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
	@Editor.Ignore
	protected HTML diffHtml;
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

	private final int maxPrettifiableLines = 1000;
	private int totalDiffLines;
	private ScmMessages scmMessages = GWT.create(ScmMessages.class);

	/**
	 * @param commit
	 */
	public void setCommit(Commit commit) {

		driver.edit(commit);

		repository.setText(commit.getRepository());
		repository.setHref(ScmRepoPlace.createPlace(projectId, commit.getRepository()).getHref());

		parentsPanel.clear();
		if (commit.getParents().isEmpty()) {
			parentsPanel.add(new Label("None"));
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
			int i = 0;
			for (DiffEntry diffEntry : commit.getChanges()) {
				DiffEntryView overviewFileDiff = new DiffEntryView(diffEntry);
				filesPanel.add(overviewFileDiff);

				final int index = i++;
				overviewFileDiff.getFileNameAnchor().addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {

						if (!patchPanel.isOpen()) {
							patchPanel.setOpen(true);
						}
						Element toScrollTo = Navigation.findElementById(diffHtml.getElement(), computeElementId(index));
						if (toScrollTo != null) {
							Window.scrollTo(0, toScrollTo.getAbsoluteTop());
						}

					}
				});
			}
			patchPanel.setVisible(true);
			changesPanel.setVisible(true);
			diffHtml.setHTML(buildHtml(commit.getChanges()));
		} else {
			patchPanel.setVisible(false);
			changesPanel.setVisible(false);
			diffHtml.setHTML("");
		}

		authorImage.setUrl(Avatar.computeAvatarUrl(commit.getAuthor().getGravatarHash(), Avatar.Size.MEDIUM));

		UIObject.setVisible(committerInfoDiv, commit.getCommitter() != null);
		if (totalDiffLines < maxPrettifiableLines) {
			GoogleCodePrettifyUtil.run();
		}
	}

	private String computeElementId(int index) {
		return "diff-file-" + index;
	}

	static interface Template extends SafeHtmlTemplates {
		@Template("<div class=\"{0}\" id=\"{1}\">{2}</div>")
		SafeHtml addFileChange(String style, String id, String filename);

		@Template("<pre class=\"prettyprint {0}\">{1}</pre>")
		SafeHtml content(String style, String content);

	}

	private static Template template = GWT.create(Template.class);

	private SafeHtml buildHtml(List<DiffEntry> changes) {
		SafeHtmlBuilder htmlBuilder = new SafeHtmlBuilder();
		String fileName;
		int i = 0;
		totalDiffLines = 0;
		for (DiffEntry diff : changes) {
			totalDiffLines = totalDiffLines + diff.getLinesAdded() + diff.getLinesRemoved();
			String elId = computeElementId(i++);
			switch (diff.getChangeType()) {
			case ADD:
			case MODIFY:
				fileName = diff.getNewPath();
				break;
			case DELETE:
				fileName = diff.getOldPath();
				break;
			case RENAME:
				fileName = scmMessages.renamedTo(diff.getOldPath(), diff.getNewPath()); // FIXME use diff template
				break;
			case COPY:
				fileName = scmMessages.copiedTo(diff.getOldPath(), diff.getNewPath()); // FIXME use diff template
				break;
			default:
				fileName = diff.getNewPath();
				break;
			}

			htmlBuilder.append(template.addFileChange(ScmResources.get.style().contentFileHeader(), elId, fileName));

			for (Content content : diff.getContent()) {
				String style = "";
				switch (content.getType()) {
				case ADDED:
					style = ScmResources.get.style().contentAdded();
					break;
				case REMOVED:
					style = ScmResources.get.style().contentRemoved();
					break;
				}

				htmlBuilder.append(template.content(style, content.getContent()));
			}

		}
		return htmlBuilder.toSafeHtml();
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
		taskHyperlinkDetector.setProjectIdentity(projectId);
	}

}
