/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies
 * Copyright (c) 2010, 2011 SpringSource, a division of VMware
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 ******************************************************************************/
package com.tasktop.c2c.server.profile.web.ui.client.presenter.components;

import java.util.List;

import com.google.gwt.place.shared.Place;
import com.tasktop.c2c.server.common.profile.web.shared.actions.ToggleProfileDisableResult;
import com.tasktop.c2c.server.common.profile.web.shared.actions.ToggleProfileDisabledAction;
import com.tasktop.c2c.server.common.web.client.notification.Message;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.profile.domain.project.Profile;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.place.AdminProfilePlace;
import com.tasktop.c2c.server.profile.web.ui.client.presenter.AbstractProfilePresenter;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.AdminProfileView;

public class AdminProfilePresenter extends AbstractProfilePresenter {

	private final AdminProfileView view;
	private List<Profile> profiles;
	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	public AdminProfilePresenter(AdminProfileView view) {
		super(view);
		this.view = view;
	}

	public AdminProfilePresenter() {
		this(AdminProfileView.getInstance());
	}

	public void setPlace(Place aPlace) {
		AdminProfilePlace place = (AdminProfilePlace) aPlace;
		this.profiles = place.getProfiles();
		view.setProfileList(profiles);
		view.setPresenter(this);
	}

	@Override
	protected void bind() {

	}

	public void toggleAccountEnabled(final Profile profile) {
		if (getAppState().getCredentials().getProfile().equals(profile)) {
			getNotifier().displayMessage(Message.createErrorMessage(profileMessages.cannotDisableOwnAccount()));
			return;
		}
		final boolean disable = !profile.getAccountDisabled();
		getDispatchService().execute(new ToggleProfileDisabledAction(profile, disable),
				new AsyncCallbackSupport<ToggleProfileDisableResult>() {

					@Override
					protected void success(ToggleProfileDisableResult result) {
						int idx = profiles.indexOf(profile);
						profiles.remove(profile);
						profiles.add(idx, result.get());
						view.setProfileList(profiles);
						String msg;
						if (disable) {
							msg = profileMessages.profileDisabled();
						} else {
							msg = profileMessages.profileEnabled();
						}
						getNotifier().displayMessage(Message.createSuccessMessage(msg));
					}
				});
	}
}
