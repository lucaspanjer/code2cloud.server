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
package com.tasktop.c2c.server.profile.web.ui.client.place;

import java.util.List;

import com.tasktop.c2c.server.common.profile.web.client.navigation.AbstractPlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.place.HeadingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.LoggedInPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.WindowTitlePlace;
import com.tasktop.c2c.server.common.profile.web.client.util.WindowTitleBuilder;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetSshPublicKeysAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetSshPublicKeysResult;
import com.tasktop.c2c.server.profile.domain.project.Profile;
import com.tasktop.c2c.server.profile.domain.project.SshPublicKey;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;

public class UserAccountPlace extends LoggedInPlace implements HeadingPlace, WindowTitlePlace {

	public static PageMapping Account = new PageMapping(new UserAccountPlace.Tokenizer(), "account");

	@Override
	public String getHeading() {
		return "Account Settings";
	}

	public static class Tokenizer extends AbstractPlaceTokenizer<UserAccountPlace> {

		@Override
		public UserAccountPlace getPlace(String token) {
			return UserAccountPlace.createPlace();
		}

	}

	private Profile profile;
	private List<SshPublicKey> sshPublicKeys;

	public static UserAccountPlace createPlace() {
		return new UserAccountPlace();
	}

	private UserAccountPlace() {
	}

	public Profile getProfile() {
		return profile;
	}

	public List<SshPublicKey> getSshPublicKeys() {
		return sshPublicKeys;
	}

	@Override
	public String getPrefix() {
		return Account.getUrl();
	}

	@Override
	protected void addActions() {
		super.addActions();
		addAction(new GetSshPublicKeysAction());
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		profile = AppGinjector.get.instance().getAppState().getCredentials().getProfile();
		sshPublicKeys = getResult(GetSshPublicKeysResult.class).get();
		onPlaceDataFetched();
	}

	@Override
	public String getWindowTitle() {
		return WindowTitleBuilder.createWindowTitle("Account Settings");
	}
}
