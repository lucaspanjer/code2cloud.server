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
package com.tasktop.c2c.server.internal.profile.service;

import java.util.List;

import com.tasktop.c2c.server.profile.domain.internal.ServiceHost;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
public interface ServiceHostBalancingStrategy {
	void balance(ServiceHost ontoHost, List<ServiceHost> otherHosts);
}
