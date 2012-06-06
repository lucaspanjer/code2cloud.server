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

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.testing.PassthroughRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.tasktop.c2c.server.common.service.domain.Region;
import com.tasktop.c2c.server.common.web.client.view.Avatar;
import com.tasktop.c2c.server.common.web.client.view.NoCellListStyle;
import com.tasktop.c2c.server.common.web.client.widgets.Format;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.domain.ScmRepository;
import com.tasktop.c2c.server.scm.web.ui.client.place.ScmCommitPlace;
import com.tasktop.c2c.server.scm.web.ui.client.presenter.IScmRepoView;
import com.tasktop.c2c.server.scm.web.ui.client.presenter.ScmRepoPresenter;
import com.tasktop.c2c.server.scm.web.ui.client.resources.ScmResources;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class ScmRepoView extends Composite implements Editor<ScmRepository>, IScmRepoView {

	private static ScmRepoView instance;

	public static ScmRepoView getInstance() {
		if (instance == null) {
			instance = new ScmRepoView();
		}
		return instance;
	}

	static {
		ScmResources.get.style().ensureInjected();
	}

	interface Binder extends UiBinder<Widget, ScmRepoView> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	private static final int maxCommentLen = 130;

	private class CommitCell extends AbstractCell<Commit> {

		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context, Commit value, SafeHtmlBuilder sb) {
			String dateString = value.getDate() == null ? "" : Format.stringValueDateTime(value.getDate());
			String avatarUrl = Avatar.computeAvatarUrl(value.getAuthor().getGravatarHash(), Avatar.Size.SMALL);
			String message = trimMessage(value.getComment());
			String commitUrl = ScmCommitPlace.createPlace(projectId, repository.getName(), value.getCommitId())
					.getHref();
			SafeHtml anchorDiv = template.commitAnchor(commitUrl, value.getMinimizedCommitId());
			SafeHtml messageDiv = template.message(message, commitUrl, ScmResources.get.style().commitCellMessage());
			SafeHtml commitInfo = template.commitInfo(messageDiv, value.getAuthor().getEmail(), dateString,
					ScmResources.get.style().commitCellAuthor());
			sb.append(template.profileCell(template.avatar(avatarUrl), commitInfo, anchorDiv, ScmResources.get.style()
					.commitCell()));
			sb.append(template.spacerClear(ScmResources.get.style().spacer()));
		}

		private String trimMessage(String message) {
			message = message.trim();
			int firstNewline = message.indexOf("\n");
			if (firstNewline != -1) {
				message = message.substring(0, firstNewline);
			}
			if (message.length() > maxCommentLen) {
				message = message.substring(0, maxCommentLen - 1);
			}
			return message;
		}
	}

	private static HtmlTemplates template = GWT.create(HtmlTemplates.class);

	static interface HtmlTemplates extends SafeHtmlTemplates {

		@Template("<div><img src=\"{0}\"></img></div>")
		SafeHtml avatar(String avatarUrl);

		@Template("<div class=\"{2}\"><a href=\"{1}\">{0}</a></div>")
		SafeHtml message(String message, String url, String messageClass);

		@Template("<div>{0}<div class=\"{3}\">{1} authored on {2}</div></div>")
		SafeHtml commitInfo(SafeHtml messageDiv, String author, String date, String authorClass);

		@Template("<div class=\"right\"><a href=\"{0}\">{1}</a></div>")
		SafeHtml commitAnchor(String url, String commitHash);

		@Template("<div class=\"{0} clear\"/>")
		SafeHtml spacerClear(String spacerClass);

		@Template("<div class=\"{3}\">{0} {1} {2}</div>")
		SafeHtml profileCell(SafeHtml avatarDiv, SafeHtml infoDiv, SafeHtml anchorDiv, String className);

	}

	private String projectId;
	private ScmRepository repository;

	@UiField(provided = true)
	protected CellList<Commit> commitCellList = new CellList<Commit>(new CommitCell(), new NoCellListStyle());
	@UiField(provided = true)
	protected SimplePager pager = new SimplePager();

	@UiField
	protected Label name;
	@Editor.Ignore
	@UiField(provided = true)
	protected ValueListBox<String> branches = new ValueListBox<String>(PassthroughRenderer.instance());

	private Presenter presenter;

	private ScmRepoView() {
		initWidget(uiBinder.createAndBindUi(this));
		setupList();

		branches.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				presenter.branchSelected(event.getValue());
			}
		});
	}

	private void setupList() {
		commitCellList.setPageSize(ScmRepoPresenter.PAGE_SIZE);
		commitCellList.setSelectionModel(new NoSelectionModel<Commit>());
		commitCellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		pager.setDisplay(commitCellList);
		// pager.setRangeLimited(false);

	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public void setDataProvider(AbstractDataProvider<Commit> dataProvider) {
		dataProvider.addDataDisplay(commitCellList);
	}

	@Override
	public void setData(Region region, List<Commit> list) {
		boolean hasMore = region.getSize() == list.size();
		commitCellList.setRowData(region.getOffset(), list);
		commitCellList.setRowCount(region.getOffset() + list.size(), !hasMore);
		if (commitCellList.getVisibleRange().getStart() != region.getOffset()
				|| commitCellList.getVisibleRange().getLength() != region.getSize()) {
			commitCellList.setVisibleRange(region.getOffset(), region.getSize());
		}
	}

	@Override
	public void setRepository(ScmRepository repository) {
		this.repository = repository;
		this.name.setText(repository.getName());

	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public void setBranch(String branch) {
		branches.setVisible(branch != null);
		this.branches.setValue(branch, false);
		this.branches.setAcceptableValues(repository.getBranches());
	}

}
