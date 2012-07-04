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
package com.tasktop.c2c.server.common.web.client.notification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.tasktop.c2c.server.common.web.client.notification.Message.Action;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class NotificationMessage extends Composite {

	interface NotificationPanelUiBinder extends UiBinder<HTMLPanel, NotificationMessage> {
	}

	private static NotificationPanelUiBinder ourUiBinder = GWT.create(NotificationPanelUiBinder.class);
	@UiField
	protected SimplePanel contentPanel;
	@UiField
	protected Anchor actionButton;
	@UiField
	protected HTMLPanel mole;

	public NotificationMessage(final Message messageToDisplay, final NotificationPanel notificationPanel) {
		initWidget(ourUiBinder.createAndBindUi(this));
		actionButton.setText("Close");
		actionButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				notificationPanel.removeMessage(messageToDisplay);
			}
		});

		if (messageToDisplay.getMessageType() == Message.MessageType.PROGRESS) {
			displayProgressMessage(messageToDisplay.getMainMessage());
		}
		if (messageToDisplay.getMessageType() == Message.MessageType.ERROR) {
			displayErrorMessage(messageToDisplay.getMainMessage());
		}

		if (messageToDisplay.getMessageType() == Message.MessageType.SUCCESS) {
			Panel panel = new FlowPanel();
			panel.add(new Label(messageToDisplay.getMainMessage()));
			for (final Action action : messageToDisplay.getActions()) {
				Anchor anchor = new Anchor(action.getMessage());
				anchor.setHref(null); // for style underline
				anchor.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						action.getHandler().run();
					}
				});
				anchor.addStyleName("mole-action");
				panel.add(anchor);
			}

			displaySuccessMessage(panel);
		}

	}

	private void displaySuccessMessage(Widget message) {
		contentPanel.setWidget(message);
		actionButton.setVisible(true);
		mole.removeStyleName("error-mole");
		mole.addStyleName("success-mole");
	}

	private void displayProgressMessage(String message) {
		contentPanel.setWidget(new Label(message));
		actionButton.setVisible(false);
		mole.removeStyleName("success-mole");
		mole.removeStyleName("error-mole");
	}

	private void displayErrorMessage(String message) {
		contentPanel.setWidget(new Label(message));
		actionButton.setVisible(true);
		mole.removeStyleName("success-mole");
		mole.addStyleName("error-mole");
	}

}
