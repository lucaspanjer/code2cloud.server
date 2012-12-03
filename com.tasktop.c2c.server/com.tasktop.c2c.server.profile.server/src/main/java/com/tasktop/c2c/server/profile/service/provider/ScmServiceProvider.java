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
package com.tasktop.c2c.server.profile.service.provider;

import org.springframework.stereotype.Service;

import com.tasktop.c2c.server.scm.service.ScmServiceClient;

@Service("scmServiceProvider")
public class ScmServiceProvider extends AbstractPreAuthServiceProvider<ScmServiceClient> {

	protected ScmServiceProvider() {
		super("/scm/");
	}

	@Override
	protected ScmServiceClient getNewService() {
		// can't be injected, must be new'd
		// TODO can ask the factory for a new bean (declared as prototype).
		return new ScmServiceClient();
	}

	@Override
	protected String computeBaseUrl(String internalBaseUri) {
		return internalBaseUri + "/api";
	}
}
