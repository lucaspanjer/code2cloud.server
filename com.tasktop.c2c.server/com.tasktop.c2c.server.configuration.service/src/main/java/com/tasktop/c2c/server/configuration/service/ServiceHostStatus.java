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
package com.tasktop.c2c.server.configuration.service;

import com.tasktop.c2c.server.cloud.domain.ServiceType;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public class ServiceHostStatus {
	public enum State {
		RUNNING, STOPPED
	};

	private State state;
	private ServiceType serviceType;

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}
}
