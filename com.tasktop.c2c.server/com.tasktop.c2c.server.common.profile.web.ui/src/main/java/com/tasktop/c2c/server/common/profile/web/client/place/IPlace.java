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
package com.tasktop.c2c.server.common.profile.web.client.place;

import com.tasktop.c2c.server.common.web.client.notification.Message;

/**
 * Interface for place objects. This is intetended to work with the gwt Place abstract class.
 * 
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public interface IPlace {

	String TOKEN_SEPERATOR = "-";

	/**
	 * Get the token part of the url. This is the part that is used to specify the location on a physical page, similar
	 * to the anchor
	 * 
	 * @return token or null
	 */
	String getToken();

	/**
	 * Get the prefix part of the url. This is the part of the url after the #.
	 * 
	 * @return prefix
	 */
	String getPrefix();

	/**
	 * Get the full history token consisting of the prefix optionally followed by the token. Note that this does *not*
	 * contain the initial hash ("#")
	 * 
	 * @return history token
	 */
	String getHistoryToken();

	IPlace displayOnArrival(Message displayOnArrival);

	void go();

	boolean isReadyToGo();

	/**
	 * Get the full href for use in a url.
	 * 
	 * @return
	 */
	String getHref();
}
