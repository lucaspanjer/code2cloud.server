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
package com.tasktop.c2c.server.profile.web.ui.client.view.deployment;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.tasktop.c2c.server.deployment.domain.DeploymentConfiguration;
import com.tasktop.c2c.server.deployment.domain.DeploymentServiceType;
import com.tasktop.c2c.server.profile.web.ui.client.view.deployment.IDeploymentsView.Presenter;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public interface INewDeploymentView extends IsWidget {

	void setPresenter(Presenter presenter);

	/**
	 * 
	 */
	void clear();

	/**
	 * @param serviceTypes
	 */
	void setServiceTypes(List<DeploymentServiceType> serviceTypes);

	/**
	 * @return
	 */
	DeploymentConfiguration getValue();

	/**
	 * @param valid
	 */
	void setCredentialsValid(Boolean valid);

}
