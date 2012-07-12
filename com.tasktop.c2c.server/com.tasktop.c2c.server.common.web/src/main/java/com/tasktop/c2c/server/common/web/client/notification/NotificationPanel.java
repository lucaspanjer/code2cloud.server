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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.place.shared.PlaceChangeRequestEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tasktop.c2c.server.common.web.client.view.CommonGinjector;

public class NotificationPanel extends Composite implements Notifier {

	interface NotificationPanelUiBinder extends UiBinder<HTMLPanel, NotificationPanel> {
	}

	private static class MessageInfo {
		int numInstances = 1; // number of instances for this particular message;
		Widget panelWidget;
	}

	private static NotificationPanelUiBinder ourUiBinder = GWT.create(NotificationPanelUiBinder.class);
	@UiField
	protected FlowPanel messagesPanel;

	private Map<Message, MessageInfo> messages = new HashMap<Message, MessageInfo>();

	public NotificationPanel() {
		initWidget(ourUiBinder.createAndBindUi(this));

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

		if (messages.containsKey(messageToDisplay)) {
			MessageInfo info = messages.get(messageToDisplay);
			info.numInstances = info.numInstances + 1;
			return;
		}

		NotificationMessage messageWidget = new NotificationMessage(messageToDisplay, this);
		MessageInfo info = new MessageInfo();
		info.panelWidget = messageWidget;
		messages.put(messageToDisplay, info);
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
		MessageInfo info = messages.get(message);
		if (info == null) {
			return;
		}
		info.numInstances = info.numInstances - 1;

		if (info.numInstances == 0) {
			messages.remove(message);
			messagesPanel.remove(info.panelWidget);
		}
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
