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
package com.tasktop.c2c.server.event.service;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tasktop.c2c.server.auth.service.AuthUtils;
import com.tasktop.c2c.server.common.service.domain.Role;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;
import com.tasktop.c2c.server.event.domain.Event;

@Service("eventService")
public class EventServiceImpl implements EventService {

	private Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

	private List<EventListener> eventListeners;

	private ExecutorService executorService = Executors.newCachedThreadPool();

	@Secured(Role.System)
	@Override
	public void publishEvent(Event event) {
		executorService.submit(new EventCallable(event));
	}

	@Autowired
	public void setEventListeners(List<EventListener> eventListeners) {
		this.eventListeners = eventListeners;
	}

	private class EventCallable implements Callable<Object> {

		private Event event;

		public EventCallable(Event event) {
			this.event = event;
		}

		@Override
		public Object call() throws Exception {

			TenancyUtil.setProjectTenancyContext(event.getProjectId());

			AuthUtils.assumeSystemIdentity(event.getProjectId());

			try {
				for (EventListener listener : eventListeners) {
					try {
						listener.onEvent(event);
					} catch (Throwable t) {
						LOGGER.warn(
								String.format("Listener [%s] threw exception on event [%s]", listener.toString(),
										event.toString()), t);
						// continue;
					}
				}
			} finally {
				TenancyUtil.clearContext();
				SecurityContextHolder.clearContext();
			}
			return null;
		}

	}

}
