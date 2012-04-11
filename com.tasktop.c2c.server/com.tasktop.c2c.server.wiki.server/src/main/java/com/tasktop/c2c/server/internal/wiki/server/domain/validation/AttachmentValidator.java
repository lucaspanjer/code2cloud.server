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
package com.tasktop.c2c.server.internal.wiki.server.domain.validation;

import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.tasktop.c2c.server.internal.wiki.server.WikiServiceConfiguration;
import com.tasktop.c2c.server.internal.wiki.server.domain.MediaTypes;
import com.tasktop.c2c.server.wiki.domain.Attachment;

@Component
public class AttachmentValidator implements Validator {

	private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_ -]+.[a-zA-Z]{2,7}");

	@Autowired
	private MediaTypes mediaTypes;

	@Autowired
	private WikiServiceConfiguration configuration;

	@Override
	public boolean supports(Class<?> clazz) {
		return Attachment.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Attachment attachment = (Attachment) target;

		ValidationUtils.rejectIfEmpty(errors, "page", "field.required");
		ValidationUtils.rejectIfEmpty(errors, "name", "field.required");
		if (attachment.getName() != null && !NAME_PATTERN.matcher(attachment.getName()).matches()) {
			errors.rejectValue("name", "invalidValue", new Object[] { attachment.getName() }, "invalid name");
		}
		if (attachment.getMimeType() != null) {
			MediaType mediaType = null;
			try {
				mediaType = MediaType.parseMediaType(attachment.getMimeType());
			} catch (IllegalArgumentException e) {
				errors.rejectValue("mimeType", "invalidFormat");
			}
			if (mediaType != null) {
				if (mediaType.isWildcardType() || mediaType.isWildcardSubtype()) {
					errors.rejectValue("mimeType", "mediaTypeWildcardNotAllowed");
				} else if (!mediaTypes.isSupported(mediaType)) {
					errors.rejectValue("mimeType", "mediaTypeNotPermissible",
							new Object[] { attachment.getMimeType() }, "bad mime type");
				}
			}
		}
		if (attachment.getContent() == null || attachment.getContent().length == 0) {
			errors.rejectValue("content", "field.required");
		}
		int attachementSize = attachment.getContent().length;
		if (attachementSize > configuration.getMaxAttachmentSize()) {
			errors.rejectValue("content", "field.tooLarge",
					new Object[] { FileUtils.byteCountToDisplaySize(configuration.getMaxAttachmentSize()) },
					"Field to large");
		}
	}

	public void setMediaTypes(MediaTypes mediaTypes) {
		this.mediaTypes = mediaTypes;
	}
}
