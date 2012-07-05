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
package com.tasktop.c2c.server.hudson.plugin.cloud;

import hudson.slaves.AbstractCloudComputer;
import hudson.slaves.CloudRetentionStrategy;

public class C2CRetentionStrategy extends CloudRetentionStrategy {

	private final long initTime = System.currentTimeMillis();

	public C2CRetentionStrategy() {
		super(1);
	}

	/** Workaround for task 4457. Sometimes we immediately release. */
	public synchronized long check(AbstractCloudComputer c) {
		if (System.currentTimeMillis() - initTime < 60000) {
			return 1;
		}
		return super.check(c);
	}

}
