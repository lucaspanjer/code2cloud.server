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
package com.tasktop.c2c.server.profile.web.ui.client.shared.action;

import java.util.List;

import net.customware.gwt.dispatch.shared.AbstractSimpleResult;

import com.tasktop.c2c.server.deployment.domain.DeploymentServiceType;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class GetDeploymentServiceTypesResult extends AbstractSimpleResult<List<DeploymentServiceType>> {

	protected GetDeploymentServiceTypesResult() {
		super();
	}

	public GetDeploymentServiceTypesResult(List<DeploymentServiceType> value) {
		super(value);
	}

}
