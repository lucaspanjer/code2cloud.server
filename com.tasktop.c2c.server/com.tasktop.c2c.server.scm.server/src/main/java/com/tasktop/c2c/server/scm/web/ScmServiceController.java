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
import javax.servlet.http.HttpServletRequest;

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
import com.tasktop.c2c.server.scm.domain.Blame;
import com.tasktop.c2c.server.scm.domain.Blob;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.domain.Item;
import com.tasktop.c2c.server.scm.domain.Profile;
import com.tasktop.c2c.server.scm.domain.ScmRepository;
import com.tasktop.c2c.server.scm.domain.ScmSummary;
import com.tasktop.c2c.server.scm.domain.Trees;
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
	public Commit getCommit(@PathVariable("repoName") String repoName, @PathVariable("commitId") String commitId,
			@RequestParam(required = false, value = "context") Integer context) throws EntityNotFoundException {
		return scmService.getCommit(repoName, commitId, context);
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

	@RequestMapping(value = ScmServiceClient.GET_LOG_FOR_PATH_URL + "/**", method = RequestMethod.GET)
	public List<Commit> getLogForPath(@PathVariable("repo") String repoName, @PathVariable("revision") String revision,
	/* @PathVariable("filePath") String filePath, */
	@RequestParam(required = false, value = ScmServiceClient.PAGESIZE_URL_PARAM) Integer pageSize,
			@RequestParam(required = false, value = ScmServiceClient.OFFSET_URL_PARAM) Integer offset,
			HttpServletRequest request) throws EntityNotFoundException {

		return this.getLog(repoName, revision, getFilePath(request, repoName, "commits", revision),
				createRegion(offset, pageSize));
	}

	@Override
	public List<Commit> getLog(String repoName, String revision, String path, Region region)
			throws EntityNotFoundException {
		return scmService.getLog(repoName, revision, path, region);
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

	@RequestMapping(value = ScmServiceClient.CREATE_BRANCH_URL, method = RequestMethod.POST)
	public Map<String, String> createBranchReq(@PathVariable("repoName") String repoName,
			@PathVariable("branchName") String branchName) throws EntityNotFoundException {

		return Collections.singletonMap("branch", createBranch(repoName, branchName));
	}

	@Override
	public String createBranch(String repoName, String branchName) throws EntityNotFoundException {
		return scmService.createBranch(repoName, branchName);
	}

	@Override
	@RequestMapping(value = ScmServiceClient.DELETE_BRANCH_URL, method = RequestMethod.POST)
	public void deleteBranch(@PathVariable("repoName") String repoName, @PathVariable("branchName") String branchName)
			throws EntityNotFoundException {

		scmService.deleteBranch(repoName, branchName);
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

	@RequestMapping(value = ScmServiceClient.GET_TREES_URL + "/**", method = RequestMethod.GET)
	public Trees getTreesRequest(@PathVariable("repo") String repoName, @PathVariable("revision") String revision,
	/* @PathVariable("filePath") String filePath, */
	@RequestParam(required = false, value = "history", defaultValue = "true") Boolean history,
			@RequestParam(required = false, value = "recursion", defaultValue = "0") Integer recursion,
			HttpServletRequest request) throws EntityNotFoundException {

		return getTrees(repoName, revision, getFilePath(request, repoName, "trees", revision), history, recursion);
	}

	@Override
	public Trees getTrees(String repoName, String revision, String path, boolean history, int recursion)
			throws EntityNotFoundException {
		return scmService.getTrees(repoName, revision, path, history, recursion);
	}

	@RequestMapping(value = ScmServiceClient.GET_BLOB_URL + "/**", method = RequestMethod.GET)
	public Blob getBlobRequest(@PathVariable("repo") String repoName, @PathVariable("revision") String revision,
	/* @PathVariable("filePath") String filePath, */
	HttpServletRequest request) throws EntityNotFoundException {

		return getBlob(repoName, revision, getFilePath(request, repoName, "blob", revision));
	}

	@Override
	public Blob getBlob(String repoName, String revision, String path) throws EntityNotFoundException {
		return scmService.getBlob(repoName, revision, path);
	}

	@RequestMapping(value = ScmServiceClient.GET_BLAME_URL + "/**", method = RequestMethod.GET)
	public Blame getBlameRequest(@PathVariable("repo") String repoName, @PathVariable("revision") String revision,
	/* @PathVariable("filePath") String filePath, */
	HttpServletRequest request) throws EntityNotFoundException {
		return getBlame(repoName, revision, getFilePath(request, repoName, "blame", revision));
	}

	@Override
	public Blame getBlame(String repoName, String revision, String path) throws EntityNotFoundException {
		return scmService.getBlame(repoName, revision, path);
	}

	@RequestMapping(value = ScmServiceClient.GET_ITEM_URL + "/**", method = RequestMethod.GET)
	public Item getItemRequest(@PathVariable("repo") String repoName, @PathVariable("revision") String revision,
	/* @PathVariable("filePath") String filePath, */
	HttpServletRequest request) throws EntityNotFoundException {
		return getItem(repoName, revision, getFilePath(request, repoName, "item", revision));
	}

	@Override
	public Item getItem(String repoName, String revision, String path) throws EntityNotFoundException {
		return scmService.getItem(repoName, revision, path);
	}

	@RequestMapping(value = ScmServiceClient.GET_MERGE_BASE_URL + "/**", method = RequestMethod.GET)
	public Commit getMergeBaseRequest(@PathVariable("repo") String repoName, @PathVariable("revA") String revA,
			@PathVariable("revB") String revB, HttpServletRequest request) throws EntityNotFoundException {
		return getMergeBase(repoName, revA, revB);
	}

	@Override
	public Commit getMergeBase(String repoName, String revA, String revB) throws EntityNotFoundException {
		return scmService.getMergeBase(repoName, revA, revB);
	}

	/*
	 * This is extremely ugly and fragile. Using some decent REST server/client framework such as Jersey would solve it
	 * ... And it would also make the *ServiceCient.ServiceCallResult pattern go.
	 */
	private String getFilePath(HttpServletRequest request, String repoName, String type, String revision) {
		String filePath = request.getPathInfo();
		// api/repository/{repo}/trees/{revision}/ HUH
		return filePath.substring("api/repository/".length() + repoName.length() + type.length() + 2 + // e.g. "/tree/"
				revision.length() + 1);
	}

}
