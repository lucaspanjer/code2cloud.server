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
package com.tasktop.c2c.server.scm.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.ValidationException;
import com.tasktop.c2c.server.common.service.doc.Documentation;
import com.tasktop.c2c.server.common.service.doc.Title;
import com.tasktop.c2c.server.common.service.domain.Region;
import com.tasktop.c2c.server.common.service.web.AbstractBuildInfoRestService;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.domain.Profile;
import com.tasktop.c2c.server.scm.domain.ScmRepository;
import com.tasktop.c2c.server.scm.domain.ScmSummary;
import com.tasktop.c2c.server.scm.service.ScmService;
import com.tasktop.c2c.server.scm.service.ScmServiceClient;
import com.tasktop.c2c.server.scm.service.ScmServiceClient.CommitsForAuthor;

@Title("SCM Service")
@Documentation("A SCM service for managing the SCM repositories Code2Cloud project.\n"
		+ "The SCM service methods are available by appending the URI to the base URL\n"
		+ "https://{hostname}/s/{projectIdentifier}/scm/api + URI, for example: https://example.com/s/code2cloud/scm/api/summary")
@Controller
public class ScmServiceController extends AbstractBuildInfoRestService implements ScmService {

	@Resource(name = "scmService")
	private ScmService scmService;

	private Region createRegion(Integer offset, Integer pageSize) {
		Region retRegion = null;
		if (offset != null && pageSize != null) {
			retRegion = new Region(offset, pageSize);
		} else {
			retRegion = new Region(0, 25);
		}

		return retRegion;
	}

	@RequestMapping(value = ScmServiceClient.GET_LOG_URL, method = RequestMethod.GET)
	public List<Commit> getLog(
			@RequestParam(required = false, value = ScmServiceClient.PAGESIZE_URL_PARAM) Integer pageSize,
			@RequestParam(required = false, value = ScmServiceClient.OFFSET_URL_PARAM) Integer offset) {
		return this.getLog(createRegion(offset, pageSize));
	}

	@Override
	public List<Commit> getLog(Region region) {
		return scmService.getLog(region);
	}

	private int getNumDays(int numDays) {
		// If we have zero or less days, choose a sensible default
		if (numDays < 1) {
			return 60;
		}

		return numDays;
	}

	@Override
	@RequestMapping(value = ScmServiceClient.GET_SCM_SUMMARY_URL, method = RequestMethod.GET)
	public List<ScmSummary> getScmSummary(
			@RequestParam(value = ScmServiceClient.NUMDAYS_URL_PARAM, defaultValue = "-1") int numDays) {
		return scmService.getScmSummary(getNumDays(numDays));
	}

	@RequestMapping(value = ScmServiceClient.GET_NUM_COMMITS_BY_AUTHOR_URL, method = RequestMethod.GET)
	public List<CommitsForAuthor> getCommitsByAuthor(
			@RequestParam(value = ScmServiceClient.NUMDAYS_URL_PARAM, defaultValue = "-1") int numDays) {
		List<CommitsForAuthor> result = new ArrayList<CommitsForAuthor>();
		for (Entry<Profile, Integer> entry : this.getNumCommitsByAuthor(getNumDays(numDays)).entrySet()) {
			CommitsForAuthor c = new CommitsForAuthor();
			c.setAuthor(entry.getKey());
			c.setCount(entry.getValue());
			result.add(c);
		}
		return result;
	}

	@Override
	public Map<Profile, Integer> getNumCommitsByAuthor(int numDays) {
		return scmService.getNumCommitsByAuthor(numDays);
	}

	@Override
	@RequestMapping(value = ScmServiceClient.CREATE_SCM_REPOSITORY_URL, method = RequestMethod.POST)
	public ScmRepository createScmRepository(@RequestBody ScmRepository newRepo) throws EntityNotFoundException,
			ValidationException {
		return scmService.createScmRepository(newRepo);
	}

	@Override
	@RequestMapping(value = ScmServiceClient.DELETE_SCM_REPOSITORY_URL, method = RequestMethod.POST)
	public void deleteScmRepository(@RequestBody ScmRepository repo) throws EntityNotFoundException {
		scmService.deleteScmRepository(repo);
	}

	@Override
	@RequestMapping(value = ScmServiceClient.GET_SCM_REPOSITORIES_URL, method = RequestMethod.GET)
	public List<ScmRepository> getScmRepositories() throws EntityNotFoundException {
		return scmService.getScmRepositories();
	}

	@Override
	@RequestMapping(value = ScmServiceClient.GET_COMMIT_URL, method = RequestMethod.GET)
	public Commit getCommit(@PathVariable("repoName") String repoName, @PathVariable("commitId") String commitId)
			throws EntityNotFoundException {
		return scmService.getCommit(repoName, commitId);
	}

	@RequestMapping(value = ScmServiceClient.GET_LOG_FOR_REPO_URL, method = RequestMethod.GET)
	public List<Commit> getLog(@PathVariable("repo") String repoName,
			@RequestParam(required = false, value = ScmServiceClient.PAGESIZE_URL_PARAM) Integer pageSize,
			@RequestParam(required = false, value = ScmServiceClient.OFFSET_URL_PARAM) Integer offset)
			throws EntityNotFoundException {
		return this.getLog(repoName, createRegion(offset, pageSize));
	}

	@Override
	public List<Commit> getLog(String repoName, Region region) throws EntityNotFoundException {
		return scmService.getLog(repoName, region);
	}

	@RequestMapping(value = ScmServiceClient.GET_LOG_FOR_BRANCH_URL, method = RequestMethod.GET)
	public List<Commit> getLogForBranch(
			@PathVariable("repo") String repoName,
			@RequestParam(required = false, value = ScmServiceClient.BRANCH_PARAM, defaultValue = "master") String branchName,
			@RequestParam(required = false, value = ScmServiceClient.PAGESIZE_URL_PARAM) Integer pageSize,
			@RequestParam(required = false, value = ScmServiceClient.OFFSET_URL_PARAM) Integer offset)
			throws EntityNotFoundException {
		return this.getLogForBranch(repoName, branchName, createRegion(offset, pageSize));
	}

	@Override
	public List<Commit> getLogForBranch(String repoName, String branchName, Region region)
			throws EntityNotFoundException {
		return scmService.getLogForBranch(repoName, branchName, region);
	}

	@RequestMapping(value = ScmServiceClient.PUBILC_SSH_KEY_URL, method = RequestMethod.GET)
	public Map<String, String> getPublicSshKeyReq() {
		return Collections.singletonMap("string", scmService.getPublicSshKey());
	}

	@Override
	public String getPublicSshKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@RequestMapping(value = "/version", method = RequestMethod.GET)
	public Map<String, String> getServiceVersion() {
		return Collections.singletonMap("version", ScmService.VERSION);
	}
}
