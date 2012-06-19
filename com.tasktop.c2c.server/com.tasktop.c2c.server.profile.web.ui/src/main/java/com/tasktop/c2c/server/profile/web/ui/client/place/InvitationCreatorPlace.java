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

import com.tasktop.c2c.server.common.profile.web.client.navigation.AbstractPlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.place.HeadingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.LoggedInPlace;

public class InvitationCreatorPlace extends LoggedInPlace implements HeadingPlace {

	@Override
	public String getHeading() {
		return "Invitation Creator";
	}

	public static PageMapping InvitationCreator = new PageMapping(new InvitationCreatorPlace.Tokenizer(),
			"admin/invitationCreator");

	private static class Tokenizer extends AbstractPlaceTokenizer<InvitationCreatorPlace> {

		@Override
		public InvitationCreatorPlace getPlace(String token) {
			return InvitationCreatorPlace.createPlace();
		}

	}

	public static InvitationCreatorPlace createPlace() {
		return new InvitationCreatorPlace();
	}

	private InvitationCreatorPlace() {

	}

	@Override
	public String getPrefix() {
		return InvitationCreator.getUrl();
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		onPlaceDataFetched();
	}

}
