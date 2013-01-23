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
package com.tasktop.c2c.server.tasks.client.widgets.wiki;

import com.google.gwt.i18n.client.Messages;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public interface WikiMarkupMessages extends Messages {

	@DefaultMessage("Acronyms")
	String acronyms();

	@DefaultMessage("Alignment and Padding")
	String alignmentAndPadding();

	@DefaultMessage("Creates anchor ''anchorname''")
	String anchorDescription();

	@DefaultMessage("Links to anchor ''anchorname''")
	String anchorLinkDescription();

	@DefaultMessage("anchorname")
	String anchorName();

	@DefaultMessage("Anchors")
	String anchors();

	@DefaultMessage("Attributes")
	String attributes();

	@DefaultMessage("Block code")
	String blockCode();

	@DefaultMessage("Block Modifiers")
	String blockModifiers();

	@DefaultMessage("Block quote")
	String blockQuote();

	@DefaultMessage("Block quote (multiple paragraphs)")
	String blockQuoteMultiple();

	@DefaultMessage("Block quote (single paragraph)")
	String blockQuoteSingle();

	@DefaultMessage("bold")
	String bold();

	@DefaultMessage("Bulleted list")
	String bulletedList();

	@DefaultMessage("Bulleted Lists")
	String bulletedLists();

	@DefaultMessage("Caution")
	String caution();

	@DefaultMessage("citation")
	String citation();

	@DefaultMessage("code")
	String code();

	@DefaultMessage("col")
	String col();

	@DefaultMessage("deleted text")
	String deletedText();

	@DefaultMessage("— symbol (emdash)")
	String emDashSymbol();

	@DefaultMessage("emphasis")
	String emphasis();

	@DefaultMessage("(empty line)")
	String emptyLine();

	@DefaultMessage("produces a new paragraph")
	String emptyLineDescription();

	@DefaultMessage("– symbol (endash)")
	String enDashSymbol();

	@DefaultMessage("Eg:")
	String exampleAbbrev();

	@DefaultMessage("eg:")
	String exampleAbbrevLc();

	@DefaultMessage("Extended Blocks")
	String extendedBlocks();

	@DefaultMessage("Footnote")
	String footnote();

	@DefaultMessage("Footnotes")
	String footnotes();

	@DefaultMessage("footnote text")
	String footnoteText();

	@DefaultMessage("Generated Content")
	String generatedContent();

	@DefaultMessage("Generates a glossary based on acronyms in the document.")
	String glossaryDetails();

	@DefaultMessage("header text")
	String headerText();

	@DefaultMessage("Heading")
	String heading();

	@DefaultMessage("heading")
	String headingLc();

	@DefaultMessage("Headings")
	String headings();

	@DefaultMessage("Heading (where n is some digit, for example h1)")
	String headingDescription();

	@DefaultMessage("A help tip")
	String helpTip();

	@DefaultMessage("horizontal rule")
	String horizontalRule();

	@DefaultMessage("Images")
	String images();

	@DefaultMessage("Informational note")
	String informationalNote();

	@DefaultMessage("inserted text")
	String insertedText();

	@DefaultMessage("italic")
	String italic();

	@DefaultMessage("language")
	String language();

	@DefaultMessage("Links")
	String links();

	@DefaultMessage("Lists")
	String lists();

	@DefaultMessage("Markup Cheat Sheet")
	String markupCheatSheet();

	@DefaultMessage("monospace")
	String monospace();

	@DefaultMessage("Note")
	String note();

	@DefaultMessage("Numeric list")
	String numericList();

	@DefaultMessage("one")
	String one();

	@DefaultMessage("or")
	String or();

	@DefaultMessage("pad left")
	String padLeft();

	@DefaultMessage("pad left and right")
	String padLeftRight();

	@DefaultMessage("Paragraph (optional)")
	String paragraphOptional();

	@DefaultMessage("Phrase Modifiers")
	String phraseModifiers();

	@DefaultMessage("Pre-formatted")
	String preFormatted();

	@DefaultMessage("Pre-formatted block")
	String preFormattedBlock();

	@DefaultMessage("Punctuation")
	String punctuation();

	@DefaultMessage("quotes")
	String quotes();

	@DefaultMessage("reference")
	String reference();

	@DefaultMessage("A simple note")
	String simpleNote();

	@DefaultMessage("span")
	String span();

	@DefaultMessage("strong")
	String strong();

	@DefaultMessage("subscript")
	String subscript();

	@DefaultMessage("superscript")
	String superscript();

	@DefaultMessage("Tables")
	String tables();

	@DefaultMessage("center text")
	String textAlignCenter();

	@DefaultMessage("left text alignment")
	String textAlignLeft();

	@DefaultMessage("justify text")
	String textAlignJustify();

	@DefaultMessage("right text alignment")
	String textAlignRight();

	@DefaultMessage("Text Breaks")
	String textBreaks();

	@DefaultMessage("Tip")
	String tip();

	@DefaultMessage("tip")
	String tipLc();

	@DefaultMessage("Title")
	String title();

	@DefaultMessage("title")
	String titleLc();

	@DefaultMessage("Generates a table of contents.")
	String tocDetails();

	@DefaultMessage("two")
	String two();

	@DefaultMessage("Useful Information")
	String usefulInformation();

	@DefaultMessage("A warning note")
	String warningNote();
}
