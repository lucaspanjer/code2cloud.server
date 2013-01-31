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
package com.tasktop.c2c.server.common.profile.web.client.util.enums;

import com.google.gwt.i18n.client.Messages;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public interface EnumClientMessageSelector<E extends Enum<E>, M extends Messages> {

	public String getInternationalizedMessage(E enumeration, M messages);
}
