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

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.tasktop.c2c.server.common.profile.web.client.CommonProfileMessages;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.profile.web.client.place.IPlace;
import com.tasktop.c2c.server.common.profile.web.shared.actions.UpdateOrganizationAction;
import com.tasktop.c2c.server.common.profile.web.shared.actions.UpdateOrganizationResult;
import com.tasktop.c2c.server.common.web.client.notification.Message;
import com.tasktop.c2c.server.common.web.client.presenter.AsyncCallbackSupport;
import com.tasktop.c2c.server.common.web.client.presenter.SplittableActivity;
import com.tasktop.c2c.server.profile.domain.project.Organization;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.place.OrganizationAdminPlace;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.organization.OrganizationAdminEditView;

public class OrganizationAdminPresenter extends AbstractActivity implements OrganizationAdminEditView.Presenter,
		SplittableActivity {

	private Organization organization;
	private OrganizationAdminEditView view = OrganizationAdminEditView.getInstance();
	private CommonProfileMessages commonProfileMessages = AppGinjector.get.instance().getCommonProfileMessages();
	private ProfileMessages profileMessages = AppGinjector.get.instance().getProfileMessages();

	public OrganizationAdminPresenter() {
	}

	public void setPlace(Place p) {
		OrganizationAdminPlace place = (OrganizationAdminPlace) p;
		this.organization = place.getOrganization();
		updateView();
	}

	private void updateView() {
		view.setPresenter(this);
	}

	@Override
	public Organization getOrganization() {
		return organization;
	}

	@Override
	public void onSaveOrganization() {
		AppGinjector.get
				.instance()
				.getDispatchService()
				.execute(
						new UpdateOrganizationAction(organization),
						new AsyncCallbackSupport<UpdateOrganizationResult>(Message
								.createProgressMessage(commonProfileMessages.saving())) {
							@Override
							protected void success(UpdateOrganizationResult result) {
								organization = result.get();
								IPlace after = ProfileGinjector.get.instance().getPlaceProvider().getDefaultPlace();
								after.displayOnArrival(Message.createSuccessMessage(profileMessages
										.organizationUpdated()));
								after.go();
							}
						});
	}

	@Override
	public void onCancelProjectEdit() {
		ProfileGinjector.get.instance().getPlaceProvider().getDefaultPlace().go();
	}

	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(view);
	}

}
