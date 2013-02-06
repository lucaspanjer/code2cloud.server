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
package com.tasktop.c2c.server.scm.web.ui.client.presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.HasData;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.service.domain.Region;
import com.tasktop.c2c.server.common.web.client.notification.Message;
import com.tasktop.c2c.server.common.web.client.presenter.AbstractPresenter;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.common.web.client.presenter.SplittableActivity;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.web.ui.client.place.ScmRepoPlace;
import com.tasktop.c2c.server.scm.web.ui.client.resources.ScmMessages;
import com.tasktop.c2c.server.scm.web.ui.client.shared.action.GetScmLogAction;
import com.tasktop.c2c.server.scm.web.ui.client.shared.action.GetScmLogResult;
import com.tasktop.c2c.server.scm.web.ui.client.view.ScmRepoView;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class ScmRepoPresenter extends AbstractPresenter implements IScmRepoView.Presenter, SplittableActivity {

	public static final int PAGE_SIZE = 15;

	private ScmRepoView view;

	private String projectId;
	private String repoName;
	private Region region;
	private String currentBranch;
	private ScmMessages scmMessages = GWT.create(ScmMessages.class);

	/**
	 * @param view
	 */
	public ScmRepoPresenter() {
		this(ScmRepoView.getInstance());

	}

	/**
	 * @param instance
	 */
	public ScmRepoPresenter(ScmRepoView view) {
		super(view);
		this.view = view;

		view.setDataProvider(new AbstractDataProvider<Commit>() {

			@Override
			protected void onRangeChanged(HasData<Commit> display) {
				region = new Region(display.getVisibleRange().getStart(), display.getVisibleRange().getLength());
				updateDisplay();

			}
		});
	}

	protected void updateDisplay() {
		if (projectId == null) {
			return; // To protect against the initial add. Maybe this pattern should be reworked?
		}

		final Region thisRegion = region;
		ProfileGinjector.get
				.instance()
				.getDispatchService()
				.execute(new GetScmLogAction(repoName, currentBranch, projectId, region),
						new AsyncCallbackSupport<GetScmLogResult>() {

							@Override
							protected void success(GetScmLogResult result) {
								view.setData(thisRegion, result.get());
							}
						});

	}

	@Override
	protected void bind() {
		// TODO Auto-generated method stub

	}

	public void setPlace(Place aPlace) {
		ScmRepoPlace place = (ScmRepoPlace) aPlace;

		if (isCurrentPlace(place)) {
			return;
		}

		this.projectId = place.getProjectId();
		this.region = new Region(0, PAGE_SIZE);
		this.repoName = place.getRepositoryName();
		if (place.getRepository() == null) {
			ProfileGinjector.get.instance().getNotifier()
					.displayMessage(Message.createErrorMessage(scmMessages.repoNotFound()));
			return;
		}

		if (place.getBranchName() != null && !place.getRepository().getBranches().contains(currentBranch)) {
			ProfileGinjector.get.instance().getNotifier()
					.displayMessage(Message.createErrorMessage(scmMessages.branchNotFound()));
			return;
		}

		String branchName;
		String defaultBranchName = "master";
		if (place.getBranchName() != null) {
			branchName = place.getBranchName();
		} else {
			if (place.getRepository().getBranches().contains(defaultBranchName)) {
				branchName = defaultBranchName;
			} else if (place.getRepository().getBranches().isEmpty()) {
				branchName = null;
			} else {
				branchName = place.getRepository().getBranches().get(0);
			}
		}
		this.currentBranch = branchName;

		view.setPresenter(this);
		view.setProjectId(place.getProjectId());
		view.setRepository(place.getRepository());
		view.setBranch(currentBranch);
		updateDisplay();
	}

	private boolean isCurrentPlace(ScmRepoPlace place) {
		return this.repoName != null && this.repoName.equals(place.getRepositoryName()) && this.projectId != null
				&& this.projectId.equals(place.getProjectId()) && this.currentBranch != null
				&& this.currentBranch.equals(place.getBranchName());
	}

	@Override
	public void branchSelected(String value) {
		this.currentBranch = value;
		String newToken = ScmRepoPlace.createPlace(projectId, repoName, currentBranch).getHistoryToken();
		History.newItem(newToken, false);

		this.region = new Region(0, PAGE_SIZE);
		updateDisplay();

	}

}
