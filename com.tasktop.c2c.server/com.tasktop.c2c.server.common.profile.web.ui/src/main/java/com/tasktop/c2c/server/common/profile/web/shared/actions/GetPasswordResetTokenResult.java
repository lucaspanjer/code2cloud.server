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
package com.tasktop.c2c.server.common.profile.web.shared.actions;

import net.customware.gwt.dispatch.shared.AbstractSimpleResult;

import com.tasktop.c2c.server.profile.domain.project.PasswordResetToken;

/**
 * @author Myles Feichtinger (Tasktop Technologies Inc.)
 * 
 */
public class GetPasswordResetTokenResult extends AbstractSimpleResult<PasswordResetToken> {

	protected GetPasswordResetTokenResult() {
		super();
	}

	public GetPasswordResetTokenResult(PasswordResetToken value) {
		super(value);
	}

}
