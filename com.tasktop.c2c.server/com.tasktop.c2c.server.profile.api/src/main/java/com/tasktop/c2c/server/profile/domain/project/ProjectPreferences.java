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
package com.tasktop.c2c.server.profile.domain.project;

import com.tasktop.c2c.server.common.service.domain.AbstractEntity;

/**
 * @author Myles Feichtinger (Tasktop Technologies Inc.)
 * 
 */
@SuppressWarnings("serial")
public class ProjectPreferences extends AbstractEntity {
	private WikiMarkupLanguage wikiLanguage;

	/**
	 * @return the wikiLanguage
	 */
	public WikiMarkupLanguage getWikiLanguage() {
		return wikiLanguage;
	}

	/**
	 * @param wikiLanguage
	 *            the wikiLanguage to set
	 */
	public void setWikiLanguage(WikiMarkupLanguage wikiLanguage) {
		this.wikiLanguage = wikiLanguage;
	}

}
