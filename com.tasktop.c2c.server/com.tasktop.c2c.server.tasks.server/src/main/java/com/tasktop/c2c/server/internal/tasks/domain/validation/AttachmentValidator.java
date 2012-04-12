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
package com.tasktop.c2c.server.internal.tasks.domain.validation;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.tasktop.c2c.server.internal.tasks.service.TaskServiceConfiguration;
import com.tasktop.c2c.server.tasks.domain.Attachment;

@Component
public class AttachmentValidator implements Validator {

	@Autowired
	private TaskServiceConfiguration configuration;

	public boolean supports(Class<?> clazz) {
		return Attachment.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {

		ValidationUtils.rejectIfEmpty(errors, "description", "field.required");
		ValidationUtils.rejectIfEmpty(errors, "filename", "field.required");
		ValidationUtils.rejectIfEmpty(errors, "mimeType", "field.required");

		Attachment attachment = (Attachment) target;

		if (attachment.getAttachmentData() == null || attachment.getAttachmentData().length == 0) {
			errors.rejectValue("attachmentData", "field.required");
		} else {

			int attachementSize = attachment.getAttachmentData().length;
			if (attachementSize > configuration.getMaxAttachmentSize()) {
				errors.rejectValue("attachmentData", "field.tooLarge",
						new Object[] { FileUtils.byteCountToDisplaySize(configuration.getMaxAttachmentSize()) },
						"Field to large");
			}
		}

		if (attachment.getFilename() != null) {
			int filenameSize = attachment.getFilename().length();
			if (filenameSize > configuration.getMaxAttachmentFilenameSize()) {
				errors.rejectValue("filename", "field.tooLarge",
						new Object[] { configuration.getMaxAttachmentFilenameSize() }, "Field to large");
			}
		}
	}

}
