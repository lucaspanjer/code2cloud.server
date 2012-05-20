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
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.tasktop.c2c.server.common.web.client.view.NoCellListStyle;
import com.tasktop.c2c.server.scm.domain.ScmRepository;
import com.tasktop.c2c.server.scm.web.ui.client.place.ScmRepoPlace;
import com.tasktop.c2c.server.scm.web.ui.client.resources.ScmResources;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class ScmRepoListView extends Composite {

	private static ScmRepoListView instance;

	public static ScmRepoListView getInstance() {
		if (instance == null) {
			instance = new ScmRepoListView();
		}
		return instance;
	}

	static {
		ScmResources.get.style().ensureInjected();
	}

	interface Binder extends UiBinder<Widget, ScmRepoListView> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	private class RepoCell extends AbstractCell<ScmRepository> {

		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context, ScmRepository value, SafeHtmlBuilder sb) {
			String repoUrl = ScmRepoPlace.createPlace(projectId, value.getName()).getHref();
			String altUrl = value.getAlternateUrl() == null ? "" : value.getAlternateUrl();
			sb.append(template.repoAnchor(repoUrl, value.getName(), value.getUrl(), altUrl, ScmResources.get.style()
					.repoCellName()));
			sb.append(template.viewCommits(repoUrl));
			sb.append(template.spacerClear(ScmResources.get.style().spacer()));
		}
	}

	private static HtmlTemplates template = GWT.create(HtmlTemplates.class);

	static interface HtmlTemplates extends SafeHtmlTemplates {
		@Template("<div class=\"left\"><div class=\"{4}\"><a href=\"{0}\">{1}</a></div><div>{2}</div><div>{3}</div></div>")
		SafeHtml repoAnchor(String url, String name, String url1Div, String url2Div, String nameClass);

		@Template("<div class=\"right\"><a href=\"{0}\">View Commits</a></div>")
		SafeHtml viewCommits(String url);

		@Template("<div class=\"{0} clear\"/>")
		SafeHtml spacerClear(String spacerClass);

	}

	@UiField(provided = true)
	protected CellList<ScmRepository> repoCellList = new CellList<ScmRepository>(new RepoCell(), new NoCellListStyle());
	@UiField(provided = true)
	protected SimplePager pager = new SimplePager();
	private ListDataProvider<ScmRepository> dataProvider = new ListDataProvider<ScmRepository>();

	private ScmRepoListView() {
		initWidget(uiBinder.createAndBindUi(this));
		setupList();

	}

	private String projectId;

	private static final int PAGE_SIZE = 20;

	private void setupList() {
		repoCellList.setPageSize(PAGE_SIZE);
		repoCellList.setSelectionModel(new NoSelectionModel<ScmRepository>());
		repoCellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		pager.setDisplay(repoCellList);
		dataProvider.addDataDisplay(repoCellList);

	}

	public void setRepositories(List<ScmRepository> repos) {
		dataProvider.setList(repos);
		pager.setVisible(repos.size() > PAGE_SIZE);
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

}
