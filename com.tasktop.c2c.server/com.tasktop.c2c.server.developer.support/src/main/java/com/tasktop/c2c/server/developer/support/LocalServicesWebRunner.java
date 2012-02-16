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
package com.tasktop.c2c.server.developer.support;

import org.eclipse.jetty.ajp.Ajp13SocketConnector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * @author clint.morgan@tasktop.com (Tasktop Technologies Inc.)
 * 
 */
public class LocalServicesWebRunner {
	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		WebAppContext serviceContext = new WebAppContext();
		serviceContext.setResourceBase("../com.tasktop.c2c.server.services.web/src/main/webapp");
		serviceContext.setContextPath("/services");
		serviceContext.setParentLoaderPriority(true);

		WebAppContext taskContext = new WebAppContext();
		taskContext.setResourceBase("../com.tasktop.c2c.server.tasks.web/src/main/webapp");
		taskContext.setContextPath("/tasks");
		taskContext.setParentLoaderPriority(true);

		WebAppContext wikiContext = new WebAppContext();
		wikiContext.setResourceBase("../com.tasktop.c2c.server.wiki.web/src/main/webapp");
		wikiContext.setContextPath("/wiki");
		wikiContext.setParentLoaderPriority(true);

		// TODO hudson config.

		ContextHandlerCollection handlers = new ContextHandlerCollection();
		handlers.setHandlers(new Handler[] { serviceContext, taskContext, wikiContext });
		server.setHandler(handlers);

		Ajp13SocketConnector ajpCon = new Ajp13SocketConnector();
		ajpCon.setPort(8009);
		server.addConnector(ajpCon);

		server.start();
		server.join();
	}
}
