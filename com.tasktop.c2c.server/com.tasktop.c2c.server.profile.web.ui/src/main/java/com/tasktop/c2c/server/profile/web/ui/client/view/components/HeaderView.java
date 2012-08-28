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
package com.tasktop.c2c.server.profile.web.ui.client.view.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.profile.web.client.place.SignInPlace;

public class HeaderView extends BaseHeaderView implements Header {

	interface HeaderViewUiBinder extends UiBinder<Widget, HeaderView> {
	}

	private static HeaderViewUiBinder uiBinder = GWT.create(HeaderViewUiBinder.class);

	@UiField
	protected Button searchButton;
	@UiField
	protected TextBox search;

	protected UserMenuPopupPanel userMenuPopupPanel = null;

	@UiField(provided = true)
	public Image logoImage = new Image(ProfileGinjector.get.instance().getAppResources().logo());

	private HeaderView() {
		ProfileGinjector.get.instance().getAppResources().appCss().ensureInjected();

		initWidget(uiBinder.createAndBindUi(this));

		signIn.setHref(SignInPlace.createPlace().getHref());

		// By default, hide the project specific stuff.
		setProject(null);

		search.getElement().setAttribute("placeholder", "Search");
		hookDefaultButton(searchButton);

		searchButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				presenter.doSearch(search.getText());
			}
		});

	}

	public void setSearchText(String currentQuery) {
		search.setText(currentQuery);
	}

	private String initialBorderWidthProperty;

	public void showMenu(ClickEvent e) {
		if (userMenuPopupPanel == null) {
			userMenuPopupPanel = createUserMenu();
		}

		if (userMenuPopupPanel.isShowing()) {
			userMenuPopupPanel.hide();
		} else {
			userMenuPopupPanel.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
				@Override
				public void setPosition(int offsetWidth, int offsetHeight) {
					int menuBottom = userMenu.getAbsoluteTop() + userMenu.getOffsetHeight();
					int menuRight = userMenu.getAbsoluteLeft() + userMenu.getOffsetWidth();
					// Due initial popup width detection being off, we must explicitly specify the width
					int popupLeft = menuRight - 102;
					// Store the old border property
					initialBorderWidthProperty = DOM.getStyleAttribute(userMenu.getElement(), "borderWidth");
					// Apply the new border property
					DOM.setStyleAttribute(userMenu.getElement(), "borderWidth", "1px 1px 0px 1px");
					// Set popup position taking into account the now reduce border params of the menu
					userMenuPopupPanel.setPopupPosition(popupLeft, menuBottom - 2);
				}
			});
		}
	}

	protected UserMenuPopupPanel createUserMenu() {
		final UserMenuPopupPanel userMenuPopup = new UserMenuPopupPanel();
		userMenuPopup.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> customPopupPanelCloseEvent) {
				DOM.setStyleAttribute(userMenuPopup.getElement(), "borderWidth", initialBorderWidthProperty);
			}
		});
		userMenuPopup.addAutoHidePartner(userMenuPopup.getElement());
		userMenuPopup.removeStyleName("gwt-PopupPanel");
		userMenuPopup.setStyleName("account-options");
		return userMenuPopup;
	}

}
