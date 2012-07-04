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
package com.tasktop.c2c.server.common.web.client.notification;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.place.shared.PlaceChangeRequestEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.tasktop.c2c.server.common.web.client.view.CommonGinjector;

public class NotificationPanel extends Composite implements Notifier {

	interface NotificationPanelUiBinder extends UiBinder<HTMLPanel, NotificationPanel> {
	}

	private static NotificationPanelUiBinder ourUiBinder = GWT.create(NotificationPanelUiBinder.class);
	@UiField
	protected FlowPanel messagesPanel;

	private List<Message> messages = new ArrayList<Message>();

	public NotificationPanel() {
		initWidget(ourUiBinder.createAndBindUi(this));

		// CommonGinjector.get.instance().getEventBus().addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler()
		// {
		// public void onPlaceChange(PlaceChangeEvent event) {
		// hide();
		// }
		// });

		CommonGinjector.get.instance().getEventBus()
				.addHandler(PlaceChangeRequestEvent.TYPE, new PlaceChangeRequestEvent.Handler() {

					@Override
					public void onPlaceChangeRequest(PlaceChangeRequestEvent event) {
						clearMessages();

					}
				});

		setVisible(false);

	}

	private void show() {
		setVisible(true);
	}

	@Override
	public void displayMessage(final Message messageToDisplay) {
		if (messageToDisplay == null) {
			clearMessages();
			return;
		}
		show();

		if (messages.contains(messageToDisplay)) {
			return;
		}

		NotificationMessage messageWidget = new NotificationMessage(messageToDisplay, this);
		messages.add(messageToDisplay);
		messagesPanel.add(messageWidget);

		if (messageToDisplay.getDisplayFor() > 0 && !messageToDisplay.isScheduledForRemoval()) {
			Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
				@Override
				public boolean execute() {
					messageToDisplay.setScheduledForRemoval(true);
					removeMessage(messageToDisplay);
					return false;
				}
			}, messageToDisplay.getDisplayFor() * 1000);
		}
	}

	@Override
	public void removeMessage(Message message) {
		int index = messages.indexOf(message);
		if (index == -1) {
			return;
		}
		messages.remove(index);
		messagesPanel.remove(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tasktop.c2c.server.common.web.client.notification.Notifier#clearMessages()
	 */
	@Override
	public void clearMessages() {
		setVisible(false);
		messages.clear();
		messagesPanel.clear();
	}

}
