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
package com.tasktop.c2c.server.profile.web.ui.client.view.components.project.admin.team;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.tasktop.c2c.server.profile.web.ui.client.view.components.project.admin.team.IProjectAdminTeamView.Presenter;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class MemberInviteWidget extends Composite implements IMemberAddWidget {

	interface Binder extends UiBinder<Widget, MemberInviteWidget> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	@UiField
	protected TextBox inviteEmail;
	@UiField
	protected Button inviteButton;

	private Presenter presenter;

	public MemberInviteWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		inviteEmail.getElement().setPropertyString("placeholder", "Enter Email Address");
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("inviteButton")
	void onInviteUser(ClickEvent event) {
		presenter.sendInvite(inviteEmail.getText());
	}

	@Override
	public void clearInput() {
		inviteEmail.setText("");
	}
}
