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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.PostReceiveHook;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.ReceivePack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.common.service.Security;
import com.tasktop.c2c.server.common.service.web.TenancyUtil;
import com.tasktop.c2c.server.event.domain.PushEvent;
import com.tasktop.c2c.server.event.service.EventService;
import com.tasktop.c2c.server.scm.domain.Commit;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
@Component
public class EventGeneratingPostRecieveHook implements PostReceiveHook {

	@Autowired
	EventService eventService;

	@Autowired
	private ScmServiceConfiguration configuration;

	@Override
	public void onPostReceive(ReceivePack rp, Collection<ReceiveCommand> commands) {
		for (ReceiveCommand command : commands) {
			switch (command.getType()) {
			case CREATE:
			case UPDATE:
				sendCommits(rp.getRepository(), command.getRefName(), command.getOldId(), command.getNewId());
				break;
			// TODO handle other cases
			}
		}

	}

	private void sendCommits(Repository repo, String refName, ObjectId oldId, ObjectId newId) {
		try {
			sendCommitsInternal(repo, refName, oldId, newId);
		} catch (Throwable t) {
			// TODO Auto-generated catch block
			t.printStackTrace();
		}
	}

	private void sendCommitsInternal(Repository repo, String refName, ObjectId oldId, ObjectId newId)
			throws IOException {

		Ref ref = repo.getRef(refName);

		if (!newId.equals(ref.getObjectId())) {
			throw new IllegalStateException();
		}

		RevWalk revWal = new RevWalk(repo);
		revWal.markStart(revWal.parseCommit(ref.getObjectId()));
		if (!oldId.equals(ObjectId.zeroId())) {
			revWal.markUninteresting(revWal.parseCommit(oldId));
		}

		List<Commit> eventCommits = new ArrayList<Commit>();

		for (RevCommit revCommit : revWal) {
			Commit commit = GitDomain.createCommit(revCommit);
			commit.setRepository(repo.getDirectory().getName());
			commit.setUrl(configuration.getWebUrlForCommit(TenancyUtil.getCurrentTenantProjectIdentifer(), commit));

			eventCommits.add(commit);
		}

		PushEvent event = new PushEvent();
		event.setUserId(Security.getCurrentUser());
		event.setCommits(eventCommits);
		event.setProjectId(TenancyUtil.getCurrentTenantProjectIdentifer());
		event.setTimestamp(new Date());
		event.setRefName(refName);
		eventService.publishEvent(event);

	}

}
