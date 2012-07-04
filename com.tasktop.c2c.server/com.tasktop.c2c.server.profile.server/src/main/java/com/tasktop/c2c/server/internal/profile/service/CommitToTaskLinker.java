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
package com.tasktop.c2c.server.internal.profile.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tasktop.c2c.server.common.service.ConcurrentUpdateException;
import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.tasks.domain.Task;
import com.tasktop.c2c.server.tasks.service.TaskService;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class CommitToTaskLinker {

	public static String INT_IDENTIFIER_PATTERN = "(?:(?:[Tt][Aa][Ss][Kk])|(?:[Ff][Ee][Aa][Tt][Uu][Rr][Ee])|(?:[Dd][Ee][Ff][Ee][Cc][Tt])|(?:[Bb][Uu][Gg]))[ \t]*#?[ \t]*(\\d+)";

	// private static Pattern idPattern = Pattern.compile(INT_IDENTIFIER_PATTERN);

	public static String taskUrlPattern(String projectId) {
		return "https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*/" + projectId + "/task/(\\d+)"; // FIXME
	}

	private final TaskService taskService;
	private final String projectId;

	private final Map<Integer, List<Commit>> commitsBytaskId = new HashMap<Integer, List<Commit>>();

	public CommitToTaskLinker(String projectId, TaskService taskService) {
		this.taskService = taskService;
		this.projectId = projectId;
	}

	public void process(List<Commit> commits) {
		for (Commit c : commits) {
			processSingle(c);
		}

		for (Entry<Integer, List<Commit>> entry : commitsBytaskId.entrySet()) {
			verifyTaskHasCommit(entry.getKey(), entry.getValue());
		}
	}

	public void processSingle(Commit commit) {
		// We are currently only detecting on the url pattern
		List<Pattern> taskIdPatterns = Arrays.asList(Pattern.compile(taskUrlPattern(projectId)));

		for (Pattern p : taskIdPatterns) {
			Matcher m = p.matcher(commit.getComment());

			while (m.find()) {
				String id = m.group(1);

				try {
					Integer taskId = Integer.parseInt(id);
					List<Commit> cl = commitsBytaskId.get(taskId);
					if (cl == null) {
						cl = new ArrayList<Commit>();
						commitsBytaskId.put(taskId, cl);
					}
					cl.add(commit);
				} catch (NumberFormatException e) {
					// continue
				}
			}
		}
	}

	public void verifyTaskHasCommit(Integer taskId, List<Commit> commits) {

		Task task;
		try {
			task = taskService.retrieveTask(taskId);
		} catch (EntityNotFoundException e) {
			return;
		}

		boolean modifiedTask = false;

		for (Commit commit : commits) {
			boolean found = false;
			for (String existingCommit : task.getCommits()) {
				if (existingCommit.contains(commit.getCommitId())) {
					found = true;
					continue;
				}
			}
			if (!found) {
				task.getCommits().add(commit.getUrl());
				modifiedTask = true;
			}
		}

		if (modifiedTask) {
			try {
				taskService.updateTask(task);
			} catch (ValidationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConcurrentUpdateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
