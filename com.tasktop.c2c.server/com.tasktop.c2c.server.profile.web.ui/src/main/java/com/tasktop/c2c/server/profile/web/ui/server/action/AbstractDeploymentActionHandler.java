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
package com.tasktop.c2c.server.profile.web.ui.server.action;

import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.Result;

import org.springframework.beans.factory.annotation.Autowired;

import com.tasktop.c2c.server.deployment.service.DeploymentConfigurationService;
import com.tasktop.c2c.server.profile.service.provider.HudsonServiceProvider;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public abstract class AbstractDeploymentActionHandler<A extends Action<R>, R extends Result> extends
		com.tasktop.c2c.server.common.profile.web.ui.server.AbstractProfileActionHandler<A, R> {

	@Autowired
	protected DeploymentConfigurationService deploymentConfigurationService;

	@Autowired
	protected HudsonServiceProvider hudsonServiceProvider;

	public AbstractDeploymentActionHandler() {
		super();
	}

}
