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
package com.tasktop.c2c.server.internal.tasks.service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.tasktop.c2c.server.common.service.EntityNotFoundException;
import com.tasktop.c2c.server.common.service.wiki.MarkupLanguageUtil;
import com.tasktop.c2c.server.common.service.wiki.WebSafeHtmlDocumentBuilder;
import com.tasktop.c2c.server.tasks.service.TaskService;

/**
 * @author Lucas Panjer (Tasktop Technologies Inc.)
 * 
 */
@Component
public class CommentWikiRenderer {

	Map<String, MarkupLanguage> markupLanguageByName;

	@Qualifier("main")
	@Autowired
	private TaskService service;

	public CommentWikiRenderer() {
		markupLanguageByName = getMarkupMap(MarkupLanguageUtil.createMarkupLanguages());
	}

	private Map<String, MarkupLanguage> getMarkupMap(List<MarkupLanguage> markupLanguages) {
		Map<String, MarkupLanguage> markupLanguageByName = new HashMap<String, MarkupLanguage>();

		for (MarkupLanguage markupLanguage : markupLanguages) {
			markupLanguageByName.put(markupLanguage.getName(), markupLanguage);
		}
		return markupLanguageByName;
	}

	public String render(String markupText) {
		StringWriter writer = new StringWriter();
		String selectedLanguage = null;
		try {
			selectedLanguage = service.retrieveConfigurationProperty(TaskService.MARKUP_LANGUAGE_DB_KEY);
		} catch (EntityNotFoundException e) {
			throw new RuntimeException(e);
		}

		// markup language cannot be shared between threads
		// MarkupLanguage markupLanguage = this.markupLanguage.clone();
		MarkupLanguage markupLanguage = markupLanguageByName.get(selectedLanguage);

		HtmlDocumentBuilder builder = new WebSafeHtmlDocumentBuilder(writer);
		MarkupParser parser = new MarkupParser(markupLanguage);

		parser.setBuilder(builder);
		parser.parse(markupText);

		return writer.toString();
	}
}
