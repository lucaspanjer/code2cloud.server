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
package com.tasktop.c2c.server.profile.domain.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author michaelnelson (Tasktop Technologies Inc.)
 * 
 */
public enum SupportedLanguage {
	BRAZILIAN_PORTUGUESE("pt_BR"), CHINESE_SIMPLE("zh_CN"), CHINESE_TRADITIONAL("zh_TW"), ENGLISH("en"), FRENCH("fr"), GERMAN(
			"de"), ITALIAN("it"), JAPANESE("ja"), KOREAN("ko"), SPANISH("es");

	private String code;

	SupportedLanguage(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static Collection<SupportedLanguage> getValues() {
		return Arrays.asList(values());
	}

	public static Collection<String> getCodes() {
		List<String> codeList = new ArrayList<String>();
		for (SupportedLanguage value : values()) {
			codeList.add(value.getCode());
		}
		return codeList;
	}
}
