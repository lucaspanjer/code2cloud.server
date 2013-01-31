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

import com.tasktop.c2c.server.common.profile.web.client.util.enums.EnumClientMessageSelector;
import com.tasktop.c2c.server.wiki.domain.Page.GroupAccess;
import com.tasktop.c2c.server.wiki.web.ui.client.WikiMessages;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public class GroupAccessMessageSelector implements EnumClientMessageSelector<GroupAccess, WikiMessages> {

	@Override
	public String getInternationalizedMessage(GroupAccess enumeration, WikiMessages messages) {
		switch (enumeration) {
		case ALL:
			return messages.allowAllUsers();
		case MEMBER_AND_OWNERS:
			return messages.membersAndOwners();
		case OWNERS:
			return messages.owners();
		default:
			return "";
		}
	}
}
