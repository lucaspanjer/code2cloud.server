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
package com.tasktop.c2c.server.wiki.web.ui.client;

import com.tasktop.c2c.server.common.profile.web.client.navigation.PageMappings;
import com.tasktop.c2c.server.wiki.web.ui.client.place.ProjectWikiEditPagePlace;
import com.tasktop.c2c.server.wiki.web.ui.client.place.ProjectWikiHomePlace;
import com.tasktop.c2c.server.wiki.web.ui.client.place.ProjectWikiViewPagePlace;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class WikiPageMappings extends PageMappings {

	public WikiPageMappings() {
		super(ProjectWikiEditPagePlace.ProjectWikiEditPage, ProjectWikiViewPagePlace.ProjectWikiViewPage,
				ProjectWikiHomePlace.ProjectWiki);
	}

}
