/*******************************************************************************
 * Copyright (c) 2010, 2012 Oracle and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Oracle and/or its affiliates.
 ******************************************************************************/
package com.tasktop.c2c.server.scm.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.SkipRevFilter;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.domain.Region;
import com.tasktop.c2c.server.scm.domain.Blame;
import com.tasktop.c2c.server.scm.domain.Blob;
import com.tasktop.c2c.server.scm.domain.Commit;
//import com.tasktop.c2c.server.scm.domain.DiffEntry.Content;
import com.tasktop.c2c.server.scm.domain.DiffEntry;
import com.tasktop.c2c.server.scm.domain.DiffEntry.Hunk;
import com.tasktop.c2c.server.scm.domain.Item;
import com.tasktop.c2c.server.scm.domain.Trees;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Stack;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.util.io.NullOutputStream;

class GitBrowseUtil {

	// XXX implement recursion at least the -1
	public static Trees getTrees(Repository r, String revision, String path, boolean history, int recursion)
			throws IOException, GitAPIException, EntityNotFoundException {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}

		ObjectId revId = r.resolve(revision);

		if (revId == null) {
			throw new EntityNotFoundException("Revision: " + revision + " not found.");
		}

		TreeWalk treeWalk = new TreeWalk(r);
		treeWalk.addTree(new RevWalk(r).parseTree(revId));

		if (!moveToPath(treeWalk, path)) {
			throw new EntityNotFoundException("Revision: " + revision + ", path: " + path + " not found.");
		}
		Trees trees = new Trees(treeWalk.getObjectId(0).getName());

		Git git = history ? new Git(r) : null;

		while (treeWalk.next()) {
			ObjectLoader loader = r.open(treeWalk.getObjectId(0));
			Trees.Tree t = new Trees.Tree(treeWalk.getObjectId(0).getName(), // sha
					treeWalk.getPathString(), // path
					treeWalk.getNameString(), // name
					getType(loader.getType()), // type
					treeWalk.getRawMode(0), // mode
					loader.getSize() // size
			);
			trees.getTree().add(t);

			if (recursion == -2 && t.getType() == Item.Type.TREE) {
				t.setEmpty(getEmptyPath(r, treeWalk.getObjectId(0)));
			}

			if (history) {
				addHistory(git, t, revId, /* path + ( path.length() == 0 ? "" : "/") + */t.getPath());
			}

		}

		return trees;
	}

	public static Blob getBlob(Repository r, String revision, String path) throws IOException, EntityNotFoundException {

		if (path.startsWith("/")) {
			path = path.substring(1);
		}

		String id = resolve(r, r.resolve(revision), path);
		if (id == null) {
			throw new EntityNotFoundException();
		}
		ObjectId objectId = ObjectId.fromString(id);

		ObjectLoader loader = r.getObjectDatabase().open(objectId, Constants.OBJ_BLOB);

		Blob b = new Blob(id);

		if (loader.isLarge()) {
			b.setLarge(true);
			InputStream is = null;
			IOException ioex = null;
			try {
				is = loader.openStream();
				b.setBinary(RawText.isBinary(is));
			} catch (IOException ex) {
				ioex = ex;
			} finally {
				if (is != null) {
					is.close();
				}
				if (ioex != null) {
					throw ioex;
				}
			}

		} else {
			byte[] raw = loader.getBytes();
			b.setBinary(RawText.isBinary(raw));
			RawText rt = new RawText(raw);

			List<String> lines = new ArrayList<String>(rt.size());
			for (int i = 0; i < rt.size(); i++) {
				lines.add(rt.getString(i));
			}

			b.setLines(lines);
		}

		return b;
	}

	public static Blame getBlame(Repository r, String revision, String path) throws IOException {

		if (path.startsWith("/")) {
			path = path.substring(1);
		}

		Git git = new Git(r);

		ObjectId revId = r.resolve(revision);

		BlameCommand bc = git.blame();
		bc.setStartCommit(revId);
		bc.setFilePath(path);
		BlameResult br = bc.call();

		Blame blame = new Blame();
		blame.path = path;
		blame.revision = revision;
		blame.commits = new ArrayList<Commit>();
		blame.lines = new ArrayList<Blame.Line>();

		Map<String, Integer> sha2idx = new HashMap<String, Integer>();

		RawText resultContents = br.getResultContents();

		for (int i = 0; i < br.getResultContents().size(); i++) {

			RevCommit sourceCommit = br.getSourceCommit(i); // XXX should it really be the source commit
			String sha = sourceCommit.getName();
			Integer cIdx = sha2idx.get(sha);
			if (cIdx == null) {
				cIdx = blame.commits.size();
				Commit commit = GitDomain.createCommit(sourceCommit);
				blame.commits.add(commit);
				sha2idx.put(sha, cIdx);
			}

			Blame.Line bl = new Blame.Line();
			bl.commit = cIdx;
			bl.text = resultContents.getString(i);
			blame.lines.add(bl);
		}

		return blame;
	}

	public static List<Commit> getLog(Repository r, String revision, String path, Region region) throws IOException {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}

		List<Commit> commits = new ArrayList<Commit>();

		ObjectId revId = r.resolve(revision);
		RevWalk walk = new RevWalk(r);

		walk.markStart(walk.parseCommit(revId));

		if (path != null && path.trim().length() != 0) {
			walk.setTreeFilter(AndTreeFilter.create(PathFilterGroup.createFromStrings(path), TreeFilter.ANY_DIFF));
		}

		if (region != null) {
			// if (region.getOffset() > 0 && region.getSize() > 0)
			// walk.setRevFilter(AndRevFilter.create(SkipRevFilter.create(region.getOffset()),
			// MaxCountRevFilter.create(region.getSize())));
			/* else */if (region.getOffset() > 0) {
				walk.setRevFilter(SkipRevFilter.create(region.getOffset()));
			}
			// else if (region.getSize() > 0) {
			// walk.setRevFilter(MaxCountRevFilter.create(region.getSize()));
			// }
		}

		int max = region != null && region.getSize() > 0 ? region.getSize() : -1;

		for (RevCommit revCommit : walk) {
			Commit commit = GitDomain.createCommit(revCommit);
			commit.setRepository(r.getDirectory().getName());
			commits.add(commit);
			if (max != -1) {
				if (max == 0) {
					break;
				}
				max--;
			}
		}

		return commits;
	}

	public static Item getItem(Repository r, String revision, String path) throws IOException, EntityNotFoundException {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}

		String id = resolve(r, r.resolve(revision), path);
		if (id == null) {
			throw new EntityNotFoundException();
		}
		ObjectId objectId = ObjectId.fromString(id);

		ObjectLoader loader = r.open(objectId);

		return new Item(id, getType(loader.getType()));

	}

	// Private methods ---------------------------------------------------------

	private static boolean moveToPath(TreeWalk treeWalk, String path) throws MissingObjectException, IOException {

		if (path == null || path.isEmpty() || "/".equals(path)) {
			return true;
		}

		if (path.startsWith("/")) {
			path = path.substring(1);
		}

		treeWalk.setFilter(PathFilter.create(path));

		while (treeWalk.next()) {
			if (path.equals(treeWalk.getPathString())) {
				treeWalk.enterSubtree();
				return true;
			}
			treeWalk.enterSubtree();
		}

		return false;
	}

	public static String resolve(Repository r, ObjectId revision, String path) throws MissingObjectException,
			IncorrectObjectTypeException, IOException {

		if (revision == null) {
			return null;
		}

		RevCommit commit = new RevWalk(r).parseCommit(revision);

		if ("".equals(path)) {
			return commit.getTree().getName();
		}

		TreeWalk walk = TreeWalk.forPath(r, path, commit.getTree()); // new TreeWalk(r);
		// walk.reset();
		// walk.addTree(commit.getTree());
		// walk.setFilter(PathFilter.create(path));
		// walk.setRecursive(false);
		// while (walk.next()) {
		if (walk != null && path.equals(walk.getPathString())) {
			// WorkingTreeOptions opt = r.getConfig().get(WorkingTreeOptions.KEY);
			return walk.getObjectId(0).getName();
		}
		// }
		return null;

	}

	private static Item.Type getType(int type) {
		switch (type) {
		case Constants.OBJ_BLOB:
			return Item.Type.BLOB;
		case Constants.OBJ_TREE:
			return Item.Type.TREE;
		case Constants.OBJ_COMMIT:
			return Item.Type.COMMIT;
			// XXX add more
		default:
			return null;
		}
	}

	private static String getEmptyPath(Repository r, ObjectId id) throws IOException {
		TreeWalk treeWalk = new TreeWalk(r);
		treeWalk.addTree(new RevWalk(r).parseTree(id));

		String path = null;

		// Find tree of interrest it has to be alone there
		ObjectId iId = null;
		String iPath = null;
		while (treeWalk.next()) {
			if (iId == null) {
				iId = treeWalk.getObjectId(0);
				iPath = treeWalk.getPathString();
			} else {
				iId = null;
				break;
			}
		}

		if (iId != null) {
			ObjectLoader loader = r.open(iId);
			if (loader.getType() == Constants.OBJ_TREE) { // It is alone there and it is a folder
				path = "/" + iPath;
				String sep = getEmptyPath(r, iId);
				if (sep != null) {
					path = path + sep;
				}
			}
		}

		return path;

	}

	private static void addHistory(Git git, Trees.Tree tree, ObjectId revision, String path)
			throws MissingObjectException, IncorrectObjectTypeException, GitAPIException {
		LogCommand logCommand = git.log();
		logCommand.add(revision);

		logCommand.addPath(path);

		for (RevCommit revCommit : logCommand.call()) {
			Commit commit = GitDomain.createCommit(revCommit);
			tree.setLatestCommit(commit);
			break;
		}

	}

	public static DiffEntry getDiffEntry(org.eclipse.jgit.diff.DiffEntry de, Repository repo, Integer context)
			throws IOException {
		return new StructuredDiffEntryFormatter(de, repo, context).getDiffEntry();
	}

	private static class StructuredDiffEntryFormatter extends DiffFormatter {

		private final org.eclipse.jgit.diff.DiffEntry de;

		private Stack<Hunk> hunks = new Stack<Hunk>();

		private boolean binary = false;
		private int linesAdded = 0;
		private int linesRemoved = 0;

		public StructuredDiffEntryFormatter(org.eclipse.jgit.diff.DiffEntry de, Repository repo, Integer context) {
			super(NullOutputStream.INSTANCE);
			this.de = de;
			setRepository(repo);
			if (context != null && context >= 0) {
				setContext(context);
			}
		}

		public DiffEntry getDiffEntry() throws IOException {

			format(de);

			DiffEntry result = new DiffEntry();

			result.setChangeType(convertChangeType(de.getChangeType()));
			result.setNewPath(de.getNewPath());
			result.setOldPath(de.getOldPath());
			result.setHunks(hunks);
			result.setLinesAdded(linesAdded);
			result.setLinesRemoved(linesRemoved);
			result.setBinary(binary);

			return result;
		}

		@Override
		public void format(FileHeader head, RawText a, RawText b) throws IOException {
			binary = head.getPatchType() != FileHeader.PatchType.UNIFIED;
			super.format(head, a, b); // To change body of generated methods, choose Tools | Templates.
		}

		@Override
		protected void writeHunkHeader(int aStartLine, int aEndLine, int bStartLine, int bEndLine) throws IOException {
			hunks.push(new Hunk(aStartLine, aEndLine, bStartLine, bEndLine));
		}

		@Override
		protected void writeContextLine(RawText text, int line) throws IOException {
			hunks.peek().getLineChanges().add(new Hunk.LineChange(Hunk.LineChange.Type.CONTEXT, text.getString(line)));
		}

		@Override
		protected void writeAddedLine(RawText text, int line) throws IOException {
			linesAdded++;
			hunks.peek().getLineChanges().add(new Hunk.LineChange(Hunk.LineChange.Type.ADDED, text.getString(line)));
		}

		@Override
		protected void writeRemovedLine(RawText text, int line) throws IOException {
			linesRemoved++;
			hunks.peek().getLineChanges().add(new Hunk.LineChange(Hunk.LineChange.Type.REMOVED, text.getString(line)));
		}

		private static DiffEntry.ChangeType convertChangeType(org.eclipse.jgit.diff.DiffEntry.ChangeType ct) {
			switch (ct) {
			case ADD:
				return DiffEntry.ChangeType.ADD;
			case COPY:
				return DiffEntry.ChangeType.COPY;
			case DELETE:
				return DiffEntry.ChangeType.DELETE;
			case MODIFY:
				return DiffEntry.ChangeType.MODIFY;
			case RENAME:
				return DiffEntry.ChangeType.RENAME;
			default:
				return null;
			}
		}

	}

}