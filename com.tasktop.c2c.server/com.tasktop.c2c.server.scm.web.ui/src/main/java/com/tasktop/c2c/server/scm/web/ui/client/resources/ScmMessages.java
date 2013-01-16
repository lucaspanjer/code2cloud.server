/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 ******************************************************************************/
package com.tasktop.c2c.server.scm.web.ui.client.resources;

import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.LocalizableResource.GenerateKeys;
import com.google.gwt.i18n.client.Messages;

@GenerateKeys("com.google.gwt.i18n.server.keygen.MethodNameKeyGenerator")
@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
public interface ScmMessages extends Messages {

	@DefaultMessage("Author")
	String author();

	@DefaultMessage("Changes")
	String changes();

	@DefaultMessage("Commit")
	String commit();

	@DefaultMessage("Committed by {0} on {1}")
	String committedByOn(String committerEmail, String committedDate);

	@DefaultMessage("Commit \"{0}\" not found.")
	String commitNotFound(String commitId);

	@DefaultMessage("Commit {0} of {1} - {2} - {3}")
	String commitTitle(String commitId, String repositoryName, String projectName, String productName);

	@DefaultMessage("{0} copied to {1}")
	String copiedTo(String oldPath, String newPath);

	@DefaultMessage("Date")
	String date();

	@DefaultMessage("Files")
	String files();

	@DefaultMessage("Parent")
	String parent();

	@DefaultMessage("Patch Text")
	String patchText();

	@DefaultMessage("{0} renamed to {1}")
	String renamedTo(String oldPath, String newPath);

	@DefaultMessage("Repository not found")
	String repoNotFound();

	@DefaultMessage("Commits of {0} - {1} - {2}")
	String repoTitle(String repositoryName, String projectName, String productName);

	@DefaultMessage("Source repositories - {0} - {1}")
	String scmTitle(String projectName, String productName);

	@DefaultMessage("Source")
	String source();
}
