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
import com.tasktop.c2c.server.scm.web.ui.client.place.ScmCommitPlace;
import com.tasktop.c2c.server.scm.web.ui.client.view.ScmCommitView;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class ScmCommitPresenter extends AbstractPresenter {

	private ScmCommitView view;

	/**
	 * @param view
	 */
	public ScmCommitPresenter() {
		this(ScmCommitView.getInstance());

	}

	/**
	 * @param instance
	 */
	public ScmCommitPresenter(ScmCommitView view) {
		super(view);
		this.view = view;
	}

	@Override
	protected void bind() {
		// TODO Auto-generated method stub

	}

	public void setPlace(Place aPlace) {
		ScmCommitPlace place = (ScmCommitPlace) aPlace;
		view.setProjectId(place.getProjectId());
		view.setCommit(place.getCommit());
	}

}
