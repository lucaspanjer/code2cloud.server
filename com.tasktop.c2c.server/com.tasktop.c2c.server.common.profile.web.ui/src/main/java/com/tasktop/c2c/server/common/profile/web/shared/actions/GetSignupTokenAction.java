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
package com.tasktop.c2c.server.common.profile.web.shared.actions;

import net.customware.gwt.dispatch.shared.Action;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class GetSignupTokenAction implements Action<GetSignupTokenResult> {

	private String token;

	public GetSignupTokenAction(String token) {
		this.token = token;
	}

	protected GetSignupTokenAction() {

	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

}
