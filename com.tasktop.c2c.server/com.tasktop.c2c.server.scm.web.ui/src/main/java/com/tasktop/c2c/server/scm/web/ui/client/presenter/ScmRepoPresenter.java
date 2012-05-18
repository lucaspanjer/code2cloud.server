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

import com.google.gwt.place.shared.Place;
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
								view.setData(region, result.get());
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
					.displayMessage(Message.createErrorMessage("Repository not found"));
			return;
		}
		if (place.getRepository().getBranches().contains("master")) {
			this.currentBranch = "master";
		} else if (!place.getRepository().getBranches().isEmpty()) {
			this.currentBranch = place.getRepository().getBranches().get(0);
		} else {
			this.currentBranch = null;
		}

		view.setPresenter(this);
		view.setProjectId(place.getProjectId());
		view.setRepository(place.getRepository());
		view.setBranch(currentBranch);
		updateDisplay();
	}

	private boolean isCurrentPlace(ScmRepoPlace place) {
		return this.repoName != null && this.repoName.equals(place.getRepositoryName()) && this.projectId != null
				&& this.projectId.equals(place.getProjectId());
	}

	@Override
	public void branchSelected(String value) {
		this.currentBranch = value;
		this.region = new Region(0, PAGE_SIZE);
		updateDisplay();

	}

}
