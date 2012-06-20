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
package com.tasktop.c2c.server.common.profile.web.client.place;

import java.util.List;

import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.profile.web.client.navigation.AbstractPlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMapping;
import com.tasktop.c2c.server.common.profile.web.client.util.WindowTitleBuilder;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetPendingAgreementsAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.GetPendingAgreementsResult;
import com.tasktop.c2c.server.profile.domain.project.Agreement;

/**
 * @author straxus (Tasktop Technologies Inc.)
 * 
 */
public class AgreementsPlace extends LoggedInPlace implements HeadingPlace, WindowTitlePlace {

	public static PageMapping Agreements = new PageMapping(new AgreementsPlace.Tokenizer(), "agreements");

	private static class Tokenizer extends AbstractPlaceTokenizer<AgreementsPlace> {

		@Override
		public AgreementsPlace getPlace(String token) {
			return AgreementsPlace.createPlace(null);
		}

	}

	public static AgreementsPlace createPlace(DefaultPlace postAgreementsPlace) {
		return new AgreementsPlace(postAgreementsPlace);
	}

	private List<Agreement> agreements;
	private DefaultPlace postAgreementsPlace;

	private AgreementsPlace(DefaultPlace postAgreementsPlace) {
		if (postAgreementsPlace == null) {
			this.postAgreementsPlace = ProfileGinjector.get.instance().getPlaceProvider().getDefaultPlace();
		} else {
			this.postAgreementsPlace = postAgreementsPlace;
		}
	}

	public DefaultPlace getPostAgreementsPlace() {
		return postAgreementsPlace;
	}

	@Override
	public String getHeading() {
		return "Legal Agreements";
	}

	public List<Agreement> getAgreements() {
		return agreements;
	}

	@Override
	public String getPrefix() {
		return Agreements.getUrl();
	}

	@Override
	protected void addActions() {
		super.addActions();
		addAction(new GetPendingAgreementsAction());
	}

	@Override
	protected void handleBatchResults() {
		super.handleBatchResults();
		agreements = getResult(GetPendingAgreementsResult.class).get();
		onPlaceDataFetched();
	}

	@Override
	public String getWindowTitle() {
		return WindowTitleBuilder.createWindowTitle("Legal Agreements");
	}
}
