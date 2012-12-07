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
package com.tasktop.c2c.server.common.profile.web.client;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public interface CommonProfileMessages extends Messages {

	@DefaultMessage("Browse Projects")
	String browseProjects();

	@DefaultMessage("Code2Cloud")
	String code2Cloud();

	@DefaultMessage("Discover Projects")
	String discoverProjects();

	@DefaultMessage("Want to participate in a project you see, or create your own public or private project? "
			+ "Code2Cloud is currently in beta and is accepting requests to join. "
			+ "<em>Just send your first name, last name, and email address to <a href=\"mailto:engineering-c2c@tasktop.com\">engineering-c2c@tasktop.com</a>.</em> ")
	SafeHtml participateInstructions();

	@DefaultMessage("Note: Setting a project as \"Public\" makes the project code, wiki docs, tasks and builds publicly available")
	SafeHtml settingsProjectHelp();

	@DefaultMessage("Code2Cloud is an online service for <em>creating, hosting, and deploying software development projects</em>. In just a "
			+ "few clicks, you can create your own project ready to store <em>source code</em> (Git), "
			+ "track <em>tasks and issues</em>, create <em>wiki documentation</em>, "
			+ "set up <em>continuous integration</em> (Hudson), and <em>deploy</em> to a local server "
			+ "or your <em>Cloud Foundry</em> instance. All of these tools come <em>pre-integrated</em> to work together, so you don''t "
			+ "have to build or maintain the tools you''re using to build the world''s next greatest application.")
	SafeHtml welcomeOverviewHtml();
}
