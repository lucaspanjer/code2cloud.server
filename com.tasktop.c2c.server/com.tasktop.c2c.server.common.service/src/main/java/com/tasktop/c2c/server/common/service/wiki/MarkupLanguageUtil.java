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
package com.tasktop.c2c.server.common.service.wiki;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.mylyn.wikitext.confluence.core.ConfluenceLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguageConfiguration;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.ImpliedHyperlinkReplacementToken;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import org.eclipse.mylyn.wikitext.tracwiki.core.TracWikiLanguage;
import org.eclipse.mylyn.wikitext.twiki.core.TWikiLanguage;

import com.tasktop.c2c.server.common.service.BaseProfileConfiguration;

/**
 * @author straxus (Tasktop Technologies Inc.)
 * 
 */
public class MarkupLanguageUtil {

	private MarkupLanguageUtil() {
		// No instantiation of this class.
	}

	public static MarkupLanguage createDefaultMarkupLanguage() {
		// Create and wire up our markup language.
		MarkupLanguage markupLanguage = new TextileLanguage();
		markupLanguage.configure(getMarkupLanguageConfiguration());

		return markupLanguage;
	}

	public static List<MarkupLanguage> createMarkupLanguages() {
		// Create and wire up our markup languages.
		List<MarkupLanguage> languageList = new LinkedList<MarkupLanguage>();
		languageList.add(new TextileLanguage());
		languageList.add(new ConfluenceLanguage());
		languageList.add(new MediaWikiLanguage());
		languageList.add(new TracWikiLanguage());
		languageList.add(new TWikiLanguage());

		for (MarkupLanguage language : languageList) {
			language.configure(getMarkupLanguageConfiguration());
		}

		return languageList;
	}

	private static MarkupLanguageConfiguration getMarkupLanguageConfiguration() {
		MarkupLanguageConfiguration configuration = new MarkupLanguageConfiguration();
		configuration.setEscapingHtmlAndXml(true);

		// Be relatively liberal with our email detection regex.
		LinkReplacementToken emailReplacer = new LinkReplacementToken(
				"([^@\\s]+@(?:[^\\.\\@\\s]+\\.)+\\p{Alpha}{2,6})", "mailto:%s");

		// Wire up our task detection.
		TenantAwareLinkReplacementToken taskLinkReplacer = new TenantAwareLinkReplacementToken(
				"(?:(?:(?:[Dd][Uu][Pp][Ll][Ii][Cc][Aa][Tt][Ee] [Oo][Ff])|(?:[Tt][Aa][Ss][Kk])|(?:[Ff][Ee][Aa][Tt][Uu][Rr][Ee])|(?:[Dd][Ee][Ff][Ee][Cc][Tt])|(?:[Bb][Uu][Gg]))[ \t]*#?[ \t]*(\\d+))",
				BaseProfileConfiguration.PROJECTS_WEB_PATH + "/%1$s/task/%2$s");

		configuration.getPhraseModifiers().add(emailReplacer);
		configuration.getPhraseModifiers().add(taskLinkReplacer);
		configuration.getPhraseModifiers().add(new ImpliedHyperlinkReplacementToken());
		return configuration;
	}
}
