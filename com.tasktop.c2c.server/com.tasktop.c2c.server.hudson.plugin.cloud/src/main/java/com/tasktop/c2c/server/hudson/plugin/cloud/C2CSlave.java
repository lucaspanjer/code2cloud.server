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

import hudson.model.Descriptor.FormException;
import hudson.model.TaskListener;
import hudson.slaves.AbstractCloudSlave;

import java.io.IOException;
import java.util.Collections;

public class C2CSlave extends AbstractCloudSlave {
	private static final int NUMEXECUTORS = 1;
	private static final Mode MODE = Mode.NORMAL;
	private static final String LABEL = "Code2Cloud";
	private static final String NODE_DESC = "Code2Cloud builder";

	private String address;
	private C2CSlaveCloud cloud;

	public C2CSlave(String name, C2CSlaveCloud cloud, String remoteFilesystemPath) throws FormException, IOException {
		super(name, NODE_DESC, remoteFilesystemPath, NUMEXECUTORS, MODE, LABEL, new C2CComputerLauncher(
				cloud.getSshUser(), cloud.getSshKeyFilePath()), new C2CRetentionStrategy(), Collections.EMPTY_LIST);
		this.cloud = cloud;
	}

	@Override
	public C2CSlaveComputer createComputer() {
		return new C2CSlaveComputer(this);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	protected void _terminate(TaskListener listener) throws IOException, InterruptedException {
		cloud.returnSlave(this, listener);
	}
}
