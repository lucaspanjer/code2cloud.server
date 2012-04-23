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
package com.tasktop.c2c.server.common.web.client.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author straxus (Tasktop Technologies Inc.)
 * 
 */
public class StringUtils {

	private StringUtils() {
		// No instantiation of this class
	}

	public static final boolean hasText(String str) {
		// If our string is not just whitespace, return true.
		return (str != null && str.trim().length() > 0);
	}

	public static String concatenate(Collection<?> coll) {
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = coll.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext()) {
				sb.append(' ');
			}
		}
		return sb.toString();
	}

	public static String chopLongLinesAtSpaces(int maxLineLenght, String text) {
		StringBuilder result = new StringBuilder();

		int currentIndex = 0;

		while (currentIndex < text.length()) {
			int newLineIdx = text.indexOf("\n", currentIndex);
			String line;

			// Get the next line
			if (newLineIdx == -1) {
				line = text.substring(currentIndex);
				currentIndex = text.length();
			} else {
				line = text.substring(currentIndex, newLineIdx + 1);
				currentIndex = newLineIdx + 1;
			}

			// Chop it up
			while (shouldChop(maxLineLenght, line)) {
				int spaceIdx = line.substring(0, maxLineLenght).lastIndexOf(" ");
				if (spaceIdx != -1) {
					result.append(line.substring(0, spaceIdx)).append("\n");
					line = line.substring(spaceIdx + 1);
				} else {
					result.append(line.substring(0, maxLineLenght)).append("\n");
					line = line.substring(maxLineLenght);
				}

			}
			result.append(line);

		}

		return result.toString();
	}

	private static boolean shouldChop(int maxLineLenght, String line) {
		return line.length() > maxLineLenght && !(line.length() == maxLineLenght + 1 && line.endsWith("\n"));
	}

}
