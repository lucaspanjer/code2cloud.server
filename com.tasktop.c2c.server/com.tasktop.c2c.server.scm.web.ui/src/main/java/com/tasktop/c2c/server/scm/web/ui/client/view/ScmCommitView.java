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
import com.tasktop.c2c.server.common.profile.web.client.CommonProfileMessages;
import com.tasktop.c2c.server.common.web.client.navigation.Navigation;
import com.tasktop.c2c.server.common.web.client.view.Avatar;
import com.tasktop.c2c.server.common.web.client.widgets.Format;
import com.tasktop.c2c.server.common.web.client.widgets.hyperlink.HyperlinkingLabel;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.domain.DiffEntry;
import com.tasktop.c2c.server.scm.domain.DiffEntry.Hunk;
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
			instance = GWT.create(ScmCommitView.class);
		}
		return instance;
	}

	interface Binder extends UiBinder<Widget, ScmCommitView> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	interface Driver extends SimpleBeanEditorDriver<Commit, ScmCommitView> {
	}

	private Driver driver = GWT.create(Driver.class);

	protected ScmCommitView() {
		bindUI();
		initializeAfterBind();
	}

	protected void bindUI() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	protected void initializeAfterBind() {
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
	public Label commitId;
	@UiField
	@Path("author.email")
	public Label authorEmail;
	@UiField
	public Image authorImage;
	@UiField(provided = true)
	public DateLabel date = new DateLabel(Format.getDateTimeFormat());
	@UiField
	public HyperlinkingLabel commentLabel;
	private TaskHyperlinkDetector taskHyperlinkDetector = new TaskHyperlinkDetector(null);
	protected HasTextEditor comment;
	@UiField
	public Panel parentsPanel;
	@UiField
	public Panel filesPanel;
	@UiField
	public DisclosurePanel changesPanel;
	@UiField
	@Editor.Ignore
	public HTML diffHtml;
	@UiField
	public Anchor repository;
	@UiField
	public DisclosurePanel patchPanel;
	@UiField
	public DivElement committerInfoDiv;
	@UiField
	@Ignore
	public Label committedByLabel;
	protected String projectId;

	private final int maxPrettifiableLines = 1000;
	private int totalDiffLines;
	private CommonProfileMessages commonProfileMessages = GWT.create(CommonProfileMessages.class);
	private ScmMessages scmMessages = GWT.create(ScmMessages.class);

	/**
	 * @param commit
	 */
	public void setCommit(Commit commit) {

		driver.edit(commit);

		repository.setText(commonProfileMessages.parentheses(commit.getRepository()));
		repository.setHref(ScmRepoPlace.createPlace(projectId, commit.getRepository()).getHref());

		parentsPanel.clear();
		if (commit.getParents().isEmpty()) {
			parentsPanel.add(new Label("None"));
		} else {
			boolean needSep = false;
			for (String parentId : commit.getParents()) {
				if (needSep) {
					parentsPanel.add(new Label(commonProfileMessages.comma()));
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
		if (commit.getCommitter() != null) {
			committedByLabel.setText(scmMessages.committedByOn(commit.getCommitter().getEmail(), Format
					.getDateTimeFormat().format(commit.getCommitDate())));
		}
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

		@Template("<pre class=\"prettyprint {0}\">@@ -{1},{2} +{3},{4} @@</pre>")
		SafeHtml hunk(String style, int aS, int aE, int bS, int bE);

		@Template("<pre class=\"prettyprint {0}\">Binary files differ</pre>")
		SafeHtml binary(String style);

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
				fileName = scmMessages.renamedTo(diff.getOldPath(), diff.getNewPath());
				break;
			case COPY:
				fileName = scmMessages.copiedTo(diff.getOldPath(), diff.getNewPath());
				break;
			default:
				fileName = diff.getNewPath();
				break;
			}

			htmlBuilder.append(template.addFileChange(ScmResources.get.style().contentFileHeader(), elId, fileName));

			if (diff.isBinary()) {
				htmlBuilder.append(template.binary(""));
			} else {
				for (Hunk hunk : diff.getHunks()) {
					htmlBuilder.append(template.hunk("", hunk.getAStartLine(), hunk.getAEndLine(),
							hunk.getBStartLine(), hunk.getBEndLine()));

					String style = null;
					char prefix = ' ';
					StringBuilder content = null;
					Hunk.LineChange.Type lt = null;

					for (Hunk.LineChange lc : hunk.getLineChanges()) {

						if (lc.getType() != lt) {

							if (content != null) {
								htmlBuilder.append(template.content(style, content.toString()));
							}

							content = new StringBuilder();
							lt = lc.getType();
							switch (lc.getType()) {
							case ADDED:
								style = ScmResources.get.style().contentAdded();
								prefix = '+';
								break;
							case REMOVED:
								prefix = '-';
								style = ScmResources.get.style().contentRemoved();
								break;
							default:
								prefix = ' ';
								style = "";
							}
						}

						content.append(prefix + lc.getText() + "\n");
					}

					htmlBuilder.append(template.content(style, content.toString()));

				}
			}

		}
		return htmlBuilder.toSafeHtml();
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
		taskHyperlinkDetector.setProjectIdentity(projectId);
	}

}
