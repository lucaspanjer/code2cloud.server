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
/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tasktop.c2c.server.common.profile.web.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * A cell which takes delegate to handle rendering and "action" events. Currently action events just correspond to click
 * or enter-press events.
 * 
 * @author clint.morgan@tasktop.com (Tasktop Technologies Inc.)
 * 
 * @param <C>
 *            type that this cell represents
 */
public class DelegateCell<C> extends AbstractCell<C> {

	/**
	 * The delegate that will handle events from the cell.
	 * 
	 * @param <T>
	 *            the type that this delegate acts on
	 */
	public static interface ActionDelegate<T> {
		/**
		 * Perform an action on the cell.
		 * 
		 * @param context
		 *            of the cell
		 */
		void execute(Context context);
	}

	/**
	 * The delegate that will render the cell.
	 */
	public static interface RenderDelegate<C> {
		/**
		 * Generate the html for the cell.
		 * 
		 * @return html
		 */
		SafeHtml render(Context context, C value, SafeHtmlBuilder sb);
	}

	private final RenderDelegate<C> renderDelegate;
	private final ActionDelegate<C> actionDelegate;

	/**
	 * Construct a new {@link DelegateCell}.
	 * 
	 * @param templateDelegate
	 *            the html to render
	 * @param actionDelegate
	 *            the delegate that will handle events
	 */
	public DelegateCell(RenderDelegate<C> templateDelegate, ActionDelegate<C> actionDelegate) {
		super("click", "keydown");
		this.actionDelegate = actionDelegate;
		this.renderDelegate = templateDelegate;
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, C value, NativeEvent event, ValueUpdater<C> valueUpdater) {
		if (parent == null) {
			return;
		}
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		if ("click".equals(event.getType())) {
			EventTarget eventTarget = event.getEventTarget();
			if (!Element.is(eventTarget)) {
				return;
			}
			if (parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget))) {
				// Ignore clicks that occur outside of the main element.
				onEnterKeyDown(context, parent, value, event, valueUpdater);
			}
		}
	}

	@Override
	public void render(Context context, C value, SafeHtmlBuilder sb) {
		sb.append(renderDelegate.render(context, value, sb));
	}

	@Override
	protected void onEnterKeyDown(Context context, Element parent, C value, NativeEvent event,
			ValueUpdater<C> valueUpdater) {
		actionDelegate.execute(context);
	}
}
