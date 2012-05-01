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
package com.tasktop.c2c.server.profile.domain.internal;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.tasktop.c2c.server.profile.domain.project.WikiMarkupLanguage;

/**
 * @author Myles Feichtinger (Tasktop Technologies Inc.)
 * 
 */
@Entity
public class ProjectPreferences extends BaseEntity {
	private WikiMarkupLanguage wikiLanguage;

	@Enumerated(EnumType.STRING)
	public WikiMarkupLanguage getWikiLanguage() {
		return wikiLanguage;
	}

	public void setWikiLanguage(WikiMarkupLanguage wikiLanguage) {
		this.wikiLanguage = wikiLanguage;
	}

}
