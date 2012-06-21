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
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.tasktop.c2c.server.scm.domain.DiffEntry;
import com.tasktop.c2c.server.scm.domain.DiffEntry.ChangeType;
import com.tasktop.c2c.server.scm.domain.DiffEntry.Content;
import com.tasktop.c2c.server.scm.domain.DiffEntry.Content.Type;

/**
 * Maps from a standard git patch to a List of DiffEntries
 * 
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class PatchParser {

	private String mainDiffLinePrefix = "diff";
	private List<String> linePrefixesToIgnore = Arrays.asList("index", "new", "deleted");
	private String devNull = "/dev/null";
	private String contextPrefix = " ";
	private String addLinePrefix = "+";
	private String removeLinePrefix = "-";
	private String lineCtxPrefix = "@@";
	private String binaryPrefix = "Binary files differ";

	public List<DiffEntry> parsePatch(String patchText) {
		List<DiffEntry> result = new ArrayList<DiffEntry>();
		Scanner scanner = new Scanner(patchText);

		String line = scanner.nextLine();

		while (scanner.hasNextLine()) {
			if (line.startsWith(mainDiffLinePrefix)) {
				line = parseDiffEntry(scanner, result);
			} else { // Should happen?
				line = scanner.nextLine();
			}
		}

		return result;
	}

	private String parseDiffEntry(Scanner scanner, List<DiffEntry> diffs) {
		DiffEntry diffEntry = new DiffEntry();
		diffs.add(diffEntry);

		String line;
		do {
			line = scanner.nextLine();
		} while (scanner.hasNext() && shouldIgnore(line));

		diffEntry.setOldPath(getOldPath(line));
		line = scanner.nextLine();
		diffEntry.setNewPath(getNewPath(line));

		if (diffEntry.getOldPath() != null && diffEntry.getOldPath().equals(diffEntry.getNewPath())) {
			diffEntry.setChangeType(ChangeType.MODIFY);
		} else if (diffEntry.getOldPath() == null && diffEntry.getNewPath() != null) {
			diffEntry.setChangeType(ChangeType.ADD);
		} else if (diffEntry.getNewPath() == null && diffEntry.getOldPath() != null) {
			diffEntry.setChangeType(ChangeType.DELETE);
		} else {
			diffEntry.setChangeType(ChangeType.RENAME); // Or copy??
		}

		diffEntry.setContent(new ArrayList<DiffEntry.Content>());

		StringBuilder content = new StringBuilder();
		Content.Type contentType = null;
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			if (line.startsWith(mainDiffLinePrefix)) {
				break;
			}

			Content.Type thisLineType = null;
			if (line.startsWith(contextPrefix) || line.startsWith(lineCtxPrefix)) {
				thisLineType = Type.CONTEXT;
			} else if (line.startsWith(addLinePrefix)) {
				thisLineType = Type.ADDED;
				diffEntry.setLinesAdded(diffEntry.getLinesAdded() + 1);
			} else if (line.startsWith(removeLinePrefix)) {
				thisLineType = Type.REMOVED;
				diffEntry.setLinesRemoved(diffEntry.getLinesRemoved() + 1);
			} else if (line.startsWith(binaryPrefix)) {
				thisLineType = Type.BINARY; // Should be the only type/content in the file
			}
			if (thisLineType == null) {
				continue; // ??
			}
			if (contentType == null) {
				contentType = thisLineType;
				content.append(line);
			} else if (contentType.equals(thisLineType)) {
				content.append("\n").append(line);
			} else {
				diffEntry.getContent().add(new Content(contentType, content.toString()));
				contentType = thisLineType;
				content = new StringBuilder(line);
			}
			line = null;
		}
		if (contentType != null) {
			diffEntry.getContent().add(new Content(contentType, content.toString()));
		}
		return line;
	}

	private String getOldPath(String line) {
		if (!line.startsWith("--- ")) {
			throw new IllegalStateException();
		}
		return getPath(line);
	}

	private String getPath(String line) {
		String path = line.substring(4);
		if (path.equals(devNull)) {
			return null;
		}
		return path.substring(2);
	}

	private String getNewPath(String line) {
		if (!line.startsWith("+++ ")) {
			throw new IllegalStateException();
		}
		return getPath(line);
	}

	private boolean shouldIgnore(String line) {
		for (String prefix : linePrefixesToIgnore) {
			if (line.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}
}
