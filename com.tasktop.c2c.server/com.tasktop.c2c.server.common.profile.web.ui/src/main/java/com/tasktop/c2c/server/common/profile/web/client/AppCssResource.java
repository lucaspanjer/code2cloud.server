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
package com.tasktop.c2c.server.common.profile.web.client;

import com.google.gwt.resources.client.CssResource;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public interface AppCssResource extends CssResource {

	String header();

	String headerWrapper();

	String wrapper();

	String headerNav();

	String search();

	@ClassName("search-button")
	String searchButton();

	String container();

	String footerStretch();

	String footer();

	String logo();

	String button();

	String disabled();

	String special();

	String selected();

	String spacer();

	String small();

	String push();

	/* BEGIN styles integrated from main-sb.css */

	String projectFilter();

	String first();

	String last();

	String inlineTabs();

	/* END styles integrated from main-sb.css */
}
