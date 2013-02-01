/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 ******************************************************************************/
package com.tasktop.c2c.server.wiki.web.ui.client.util.enums;

import com.tasktop.c2c.server.common.web.client.util.enums.EnumClientMessageSelector;
import com.tasktop.c2c.server.wiki.domain.WikiActivity.Type;
import com.tasktop.c2c.server.wiki.web.ui.client.WikiMessages;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public class WikiActivityTypeMessageSelector implements EnumClientMessageSelector<Type, WikiMessages> {

	@Override
	public String getInternationalizedMessage(Type type, WikiMessages wikiMessages) {
		switch (type) {
		case CREATED:
			return wikiMessages.createdPage();
		case DELETED:
			return wikiMessages.deletedPage();
		case UPDATED:
			return wikiMessages.updatedPage();
		default:
			return "";
		}
	}

}
