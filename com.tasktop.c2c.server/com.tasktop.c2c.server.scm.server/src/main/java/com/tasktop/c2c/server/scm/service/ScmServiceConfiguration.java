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
package com.tasktop.c2c.server.scm.service;

import com.tasktop.c2c.server.common.service.BaseProfileConfiguration;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class ScmServiceConfiguration extends BaseProfileConfiguration {

	private int sshPort = 22;

	public int getPublicSshPort() {
		return sshPort;
	}

	public void setPublicSshPort(int sshPort) {
		this.sshPort = sshPort;
	}

}
