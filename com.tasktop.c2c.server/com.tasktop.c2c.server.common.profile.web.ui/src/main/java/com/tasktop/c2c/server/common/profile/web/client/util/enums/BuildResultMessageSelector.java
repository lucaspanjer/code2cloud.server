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

import com.tasktop.c2c.server.common.profile.web.client.CommonProfileMessages;
import com.tasktop.c2c.server.common.web.client.util.enums.EnumClientMessageSelector;
import com.tasktop.c2c.server.profile.domain.build.BuildDetails.BuildResult;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public class BuildResultMessageSelector implements EnumClientMessageSelector<BuildResult, CommonProfileMessages> {

	@Override
	public String getInternationalizedMessage(BuildResult buildResult, CommonProfileMessages commonProfileMessages) {
		switch (buildResult) {
		case ABORTED:
			return commonProfileMessages.aborted();
		case FAILURE:
			return commonProfileMessages.failure();
		case NOT_BUILT:
			return commonProfileMessages.success();
		case SUCCESS:
			return commonProfileMessages.success();
		case UNSTABLE:
			return commonProfileMessages.unstable();
		default:
			return "";
		}
	}

}
