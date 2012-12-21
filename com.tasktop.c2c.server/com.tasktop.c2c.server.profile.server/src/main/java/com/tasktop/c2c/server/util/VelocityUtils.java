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
package com.tasktop.c2c.server.util;

import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public class VelocityUtils {

	/**
	 * Merge model text into a Velocity template for a given language code. If no template file is found for the
	 * language code, fall back to the default language (English).
	 * 
	 * @param velocityEngine
	 * @param templateFilename
	 * @param model
	 * @param currentUserLanguage
	 *            The ISO language code (and optional country code to make it more specific) that is the current user's
	 *            preferred language. Examples: "en", "pt_BR".
	 * @return
	 */
	public static String getLocalizedTemplateText(VelocityEngine velocityEngine, String templateFilename,
			Map<String, Object> model, String currentUserLanguage) {
		if ("en".equals(currentUserLanguage)) {
			return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateFilename, model);
		} else {
			int suffixStart = templateFilename.lastIndexOf(".");
			if (suffixStart == -1)
				suffixStart = templateFilename.length();
			StringBuilder sb = new StringBuilder(templateFilename.substring(0, suffixStart));
			sb.append("_").append(currentUserLanguage);
			sb.append(templateFilename.substring(suffixStart));
			String bodyText;
			try {
				bodyText = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, sb.toString(), model);
			} catch (VelocityException e) {
				// If the localized template file doesn't exist, we fall back to using the non-localized template file
				bodyText = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateFilename, model);
			}
			return bodyText;
		}
	}
}
