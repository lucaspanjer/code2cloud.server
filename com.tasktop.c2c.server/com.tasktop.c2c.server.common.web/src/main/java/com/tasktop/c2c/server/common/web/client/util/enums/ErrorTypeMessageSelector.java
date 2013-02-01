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
package com.tasktop.c2c.server.common.web.client.util.enums;

import com.tasktop.c2c.server.common.web.client.CommonMessages;
import com.tasktop.c2c.server.common.web.client.view.errors.ErrorType;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public class ErrorTypeMessageSelector implements EnumClientMessageSelector<ErrorType, CommonMessages> {

	@Override
	public String getInternationalizedMessage(ErrorType errorType, CommonMessages commonMessages) {
		switch (errorType) {
		case ERROR404:
			return commonMessages.errorFileNotFound();
		case ERROR500:
			return commonMessages.errorApplicationError();
		case ERROR503:
			return commonMessages.errorPerformingMaintenance();
		default:
			return "";
		}
	}

}
