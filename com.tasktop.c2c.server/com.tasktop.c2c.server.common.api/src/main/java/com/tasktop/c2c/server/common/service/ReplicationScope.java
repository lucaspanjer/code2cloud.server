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
package com.tasktop.c2c.server.common.service;

/**
 * Since we replicate to services that may not contain the entity being replicated, these constants help services
 * determine how to handle replication.
 */
public enum ReplicationScope {
	CREATE_OR_UPDATE, UPDATE_IF_EXISTS
}
