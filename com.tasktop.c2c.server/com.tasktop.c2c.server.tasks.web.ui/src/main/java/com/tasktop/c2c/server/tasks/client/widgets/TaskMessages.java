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
package com.tasktop.c2c.server.tasks.client.widgets;

import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.LocalizableResource.GenerateKeys;
import com.google.gwt.i18n.client.Messages;

@GenerateKeys("com.google.gwt.i18n.server.keygen.MethodNameKeyGenerator")
@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
public interface TaskMessages extends Messages {

	@DefaultMessage("Attachments ({0})")
	String attachmentsHeader(int attachmentCount);

	@DefaultMessage("There are unsaved changes. Are you sure you want to navigate away? Press OK to navigate away and lose unsaved changes, or Cancel to stay on the current page.")
	String dirtyNavigateWarning();
}
