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
import com.tasktop.c2c.server.common.web.client.presenter.AbstractPresenter;
import com.tasktop.c2c.server.common.web.client.presenter.SplittableActivity;
import com.tasktop.c2c.server.scm.web.ui.client.place.ScmPlace;
import com.tasktop.c2c.server.scm.web.ui.client.view.ScmRepoListView;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class ScmRepoListPresenter extends AbstractPresenter implements SplittableActivity {

	public static final int PAGE_SIZE = 20;

	private ScmRepoListView view;

	/**
	 * @param view
	 */
	public ScmRepoListPresenter() {
		this(ScmRepoListView.getInstance());

	}

	/**
	 * @param instance
	 */
	public ScmRepoListPresenter(ScmRepoListView view) {
		super(view);
		this.view = view;

	}

	@Override
	protected void bind() {
		// TODO Auto-generated method stub

	}

	public void setPlace(Place aPlace) {
		ScmPlace place = (ScmPlace) aPlace;
		view.setProjectId(place.getProjectId());
		view.setRepositories(place.getRepositories());
	}

}
