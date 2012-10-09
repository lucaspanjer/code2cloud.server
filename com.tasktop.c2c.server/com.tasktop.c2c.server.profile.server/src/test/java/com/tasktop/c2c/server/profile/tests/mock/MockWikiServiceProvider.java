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
package com.tasktop.c2c.server.profile.tests.mock;

import com.tasktop.c2c.server.profile.service.provider.WikiServiceProvider;
import com.tasktop.c2c.server.wiki.service.WikiService;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public class MockWikiServiceProvider extends WikiServiceProvider {

	private static WikiService wikiService;

	public static void setWikiService(WikiService wikiService) {
		MockWikiServiceProvider.wikiService = wikiService;
	}

	@Override
	public WikiService getWikiService(String projectIdentifier) {
		return wikiService;
	}
}
