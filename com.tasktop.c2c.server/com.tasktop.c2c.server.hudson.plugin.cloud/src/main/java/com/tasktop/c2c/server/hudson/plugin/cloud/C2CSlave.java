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

import hudson.model.BuildableItem;
import hudson.model.Queue.Task;
import hudson.model.TaskListener;
import hudson.model.Descriptor.FormException;
import hudson.model.Hudson;
import hudson.model.queue.CauseOfBlockage;
import hudson.slaves.AbstractCloudSlave;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.hudson.security.team.Team;

public class C2CSlave extends AbstractCloudSlave {
	private static final int NUMEXECUTORS = 1;
	private static final Mode MODE = Mode.EXCLUSIVE;
	private static final String LABEL = "Code2Cloud";
	private static final String NODE_DESC = "Code2Cloud Builder";

	private String address;
	private C2CSlaveCloud cloud;
	private String projectId;

	public C2CSlave(String name, String projectId, C2CSlaveCloud cloud, String remoteFilesystemPath)
			throws FormException, IOException {
		super(name, NODE_DESC, remoteFilesystemPath, NUMEXECUTORS, MODE, LABEL, new C2CComputerLauncher(
				cloud.getSshUser(), cloud.getSshKeyFilePath()), new C2CRetentionStrategy(), Collections.EMPTY_LIST);
		this.cloud = cloud;
		this.projectId = projectId;
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

	public String getProjectId() {
		return projectId;
	}

	private static class WrongProjectCauseOfBlockage extends CauseOfBlockage {

		@Override
		public String getShortDescription() {
			return "Wrong project";
		}

	}

	private static WrongProjectCauseOfBlockage WRONG_PROJECT = new WrongProjectCauseOfBlockage();

	// Enforce that this slave can only build for a given project.
	@Override
	public CauseOfBlockage canTake(Task task) {

		if (task instanceof BuildableItem) {
			BuildableItem buildableItem = (BuildableItem) task;
			String jobId = buildableItem.getId();
			Team t = Hudson.getInstance().getTeamManager().findJobOwnerTeam(jobId);
			if (t == null) {
				return WRONG_PROJECT;
			}

			String jobProjectId = Hudson.getInstance().getTeamManager().findJobOwnerTeam(jobId).getName();

			if (this.projectId.equals(jobProjectId)) {
				return null;
			} else {
				return WRONG_PROJECT;
			}
		} else {
			return WRONG_PROJECT;
		}

	}
}
