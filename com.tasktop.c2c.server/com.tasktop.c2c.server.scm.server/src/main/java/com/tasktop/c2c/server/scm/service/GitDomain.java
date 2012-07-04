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

import java.util.ArrayList;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

import com.tasktop.c2c.server.common.service.identity.Gravatar;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.domain.Profile;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class GitDomain {

	/**
	 * @param revCommit
	 * @return
	 */
	public static Commit createCommit(RevCommit revCommit) {
		Commit commit = new Commit(revCommit.getName(), fromPersonIdent(revCommit.getAuthorIdent()), revCommit
				.getAuthorIdent().getWhen(), revCommit.getFullMessage());
		commit.setParents(new ArrayList<String>(revCommit.getParentCount()));
		for (ObjectId parentId : revCommit.getParents()) {
			commit.getParents().add(parentId.getName());
		}
		if (revCommit.getCommitterIdent() != null && !revCommit.getAuthorIdent().equals(revCommit.getCommitterIdent())) {
			commit.setCommitter(fromPersonIdent(revCommit.getCommitterIdent()));
			commit.setCommitDate(revCommit.getCommitterIdent().getWhen());
		}

		return commit;
	}

	public static Profile fromPersonIdent(PersonIdent person) {
		Profile result = new Profile();
		result.setEmail(person.getEmailAddress());
		result.setUsername(person.getEmailAddress());
		result.setGravatarHash(Gravatar.computeHash(person.getEmailAddress()));
		int firstSpace = person.getName().indexOf(" ");
		String firstName = firstSpace == -1 ? "" : person.getName().substring(0, firstSpace);
		String lastName = firstSpace == -1 ? person.getName() : person.getName().substring(firstSpace + 1);
		result.setFirstName(firstName);
		result.setLastName(lastName);

		return result;
	}

}
