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
package com.tasktop.c2c.server.configuration.service;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public class FileUtils {

	/**
	 * Return the size of a file. If this file is a directory, return the size of all files contained in the directory,
	 * counting each file only once (in the case of symlinks).
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static long size(File file) throws IOException {
		if (file.isDirectory()) {
			return directorySize(file);
		}
		return file.length();
	}

	public static long directorySize(File file) throws IOException {
		long count = 0;

		Queue<File> toVisit = new LinkedList<File>();
		Set<String> visitedCanonicalPaths = new HashSet<String>();

		toVisit.add(file);

		while (!toVisit.isEmpty()) {
			File current = toVisit.poll();

			if (visitedCanonicalPaths.contains(current.getCanonicalPath())) {
				continue;
			}

			if (current.isDirectory()) {
				for (File subDir : current.listFiles()) {
					if (!visitedCanonicalPaths.contains(subDir)) {
						toVisit.add(subDir);
					}
				}
			} else {
				count = count + current.length();
			}
			visitedCanonicalPaths.add(current.getCanonicalPath());
		}
		return count;
	}
}
