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
import com.tasktop.c2c.server.common.profile.web.client.place.AbstractBatchFetchingPlace;
import com.tasktop.c2c.server.common.profile.web.client.place.HeadingPlace;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;

public class HelpPlace extends AbstractBatchFetchingPlace implements HeadingPlace {

	public static PageMapping Help = new PageMapping(new HelpPlace.Tokenizer(), "help");

	private ProfileMessages messages = AppGinjector.get.instance().getProfileMessages();

	private static class Tokenizer extends AbstractPlaceTokenizer<HelpPlace> {

		@Override
		public HelpPlace getPlace(String token) {
			return HelpPlace.createPlace();
		}

	}

	private HelpPlace() {

	}

	public static HelpPlace createPlace() {
		return new HelpPlace();
	}

	@Override
	public String getPrefix() {
		return Help.getUrl();
	}

	@Override
	public String getHeading() {
		return messages.help();
	}

	protected void handleBatchResults() {
		onPlaceDataFetched();
	}

}
