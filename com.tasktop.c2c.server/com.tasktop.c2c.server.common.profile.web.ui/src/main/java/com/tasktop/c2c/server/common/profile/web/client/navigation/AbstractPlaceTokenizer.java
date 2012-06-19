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
package com.tasktop.c2c.server.common.profile.web.client.navigation;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.tasktop.c2c.server.common.profile.web.client.ProfileGinjector;
import com.tasktop.c2c.server.common.profile.web.client.place.AbstractPlace;
import com.tasktop.c2c.server.common.web.client.navigation.Args;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public abstract class AbstractPlaceTokenizer<P extends AbstractPlace> implements PlaceTokenizer<P> {

	/** Note that this is never used anywhere. */
	public String getToken(P place) {
		return place.getHistoryToken();
	}

	protected Args getPathArgsForUrl(String token) {
		// FIXME could just use the associated pageMapping
		return ProfileGinjector.get.instance().getPageMappingRegistry().getPathArgsForUrl(token);
	}

}
