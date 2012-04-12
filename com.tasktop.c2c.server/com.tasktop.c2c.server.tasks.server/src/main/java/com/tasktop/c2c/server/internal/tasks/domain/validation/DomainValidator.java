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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.tasktop.c2c.server.common.service.validation.AbstractDomainValidator;
import com.tasktop.c2c.server.tasks.domain.Attachment;
import com.tasktop.c2c.server.tasks.domain.Comment;
import com.tasktop.c2c.server.tasks.domain.Component;
import com.tasktop.c2c.server.tasks.domain.FieldDescriptor;
import com.tasktop.c2c.server.tasks.domain.Iteration;
import com.tasktop.c2c.server.tasks.domain.Keyword;
import com.tasktop.c2c.server.tasks.domain.Milestone;
import com.tasktop.c2c.server.tasks.domain.Product;
import com.tasktop.c2c.server.tasks.domain.SavedTaskQuery;
import com.tasktop.c2c.server.tasks.domain.Task;
import com.tasktop.c2c.server.tasks.domain.WorkLog;
import com.tasktop.c2c.server.tasks.domain.validation.CommentValidator;
import com.tasktop.c2c.server.tasks.domain.validation.ComponentValidator;
import com.tasktop.c2c.server.tasks.domain.validation.FieldDescriptorValidator;
import com.tasktop.c2c.server.tasks.domain.validation.IterationValidator;
import com.tasktop.c2c.server.tasks.domain.validation.KeywordValidator;
import com.tasktop.c2c.server.tasks.domain.validation.MilestoneValidator;
import com.tasktop.c2c.server.tasks.domain.validation.ProductValidator;
import com.tasktop.c2c.server.tasks.domain.validation.SavedTaskQueryValidator;
import com.tasktop.c2c.server.tasks.domain.validation.TaskValidator;
import com.tasktop.c2c.server.tasks.domain.validation.WorkLogValidator;

@org.springframework.stereotype.Component
public class DomainValidator extends AbstractDomainValidator implements InitializingBean {

	@Autowired
	private AttachmentValidator attachmentValidator;

	@Override
	public void afterPropertiesSet() throws Exception {
		registerValidator(Task.class, new TaskValidator());
		registerValidator(Comment.class, new CommentValidator());
		registerValidator(WorkLog.class, new WorkLogValidator());
		registerValidator(Product.class, new ProductValidator());
		registerValidator(Component.class, new ComponentValidator());
		registerValidator(Milestone.class, new MilestoneValidator());
		registerValidator(Keyword.class, new KeywordValidator());
		registerValidator(SavedTaskQuery.class, new SavedTaskQueryValidator());
		registerValidator(Iteration.class, new IterationValidator());
		registerValidator(FieldDescriptor.class, new FieldDescriptorValidator());
		registerValidator(Attachment.class, attachmentValidator);

	}

}
