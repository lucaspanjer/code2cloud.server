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
package com.tasktop.c2c.server.wiki.web.ui.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public interface WikiMessages extends Messages {

	@DefaultMessage("Access Controls")
	String accessControls();

	@DefaultMessage("Allow all users")
	String allowAllUsers();

	@DefaultMessage("All Pages")
	String allPages();

	@DefaultMessage("Attach File")
	String attachFile();

	@DefaultMessage("Attachments")
	String attachments();

	@DefaultMessage("Attachment \"{0}\" uploaded")
	String attachmentUploaded(String attachmentFileName);

	@DefaultMessage("Change")
	String change();

	@DefaultMessage("Changed: {0} by {1}")
	String changedBy(String modifiedDate, String author);

	@DefaultMessage("Change wiki markup to current language preference ({0})")
	String changeWikiMarkup(String markupLanguage);

	@DefaultMessage("Created: {0} by {1}")
	String createdBy(String createdDate, String author);

	@DefaultMessage("Created Page")
	String createdPage();

	@DefaultMessage("Creating Page...")
	String creatingPage();

	@DefaultMessage("Deleted Page")
	String deletedPage();

	@DefaultMessage("Edit Page")
	String editPage();

	@DefaultMessage("Edit Wiki - {0} - {1} - {2}")
	String editWikiWindowTitle(String pagePath, String projectName, String productName);

	@DefaultMessage("Go to page")
	String goToPage();

	@DefaultMessage("Wiki - {0} - {1}")
	String homeWindowTitle(String projectName, String productName);

	@DefaultMessage("Members + Owners")
	String membersAndOwners();

	@DefaultMessage("New Page")
	String newPage();

	@DefaultMessage("New Wiki - {0} - {1}")
	String newWikiWindowTitle(String projectName, String productName);

	@DefaultMessage("No outline available")
	String outlineNotAvailable();

	@DefaultMessage("Owners")
	String owners();

	@DefaultMessage("Page Created.")
	String pageCreated();

	@DefaultMessage("Page Deleted.")
	String pageDeleted();

	@DefaultMessage("This page has been deleted.")
	String pageDeletedMessage();

	@DefaultMessage("Page \"{0}\" not found.")
	String pageNotFound(String pagePath);

	@DefaultMessage("Page Restored.")
	String pageRestored();

	@DefaultMessage("Page Saved.")
	String pageSaved();

	@DefaultMessage("Page Updated.")
	String pageUpdated();

	@DefaultMessage("Restore")
	String restore();

	@DefaultMessage("Saving Page...")
	String savingPage();

	@DefaultMessage("Search Wiki...")
	String searchWiki();

	@DefaultMessage("Text (Markup: {0})")
	String textWithMarkupLanguage(String markupLanguage);

	@DefaultMessage("Title")
	String title();

	@DefaultMessage("There are unsaved changes. Are you sure you want to navigate away? "
			+ "Press OK to navigate away and lose unsaved changes, or Cancel to stay on the current page.")
	String unsavedChangesMessage();

	@DefaultMessage("Updated Page")
	String updatedPage();

	@DefaultMessage("Updating Page...")
	String updatingPage();

	@DefaultMessage("Wiki - {0} - {1} - {2}")
	String viewWikiWindowTitle(String pagePath, String projectName, String productName);

	@DefaultMessage("Wiki Help")
	String wikiHelp();
}
