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
package com.tasktop.c2c.server.scm.web.ui.client.presenter;

import java.util.List;

import com.google.gwt.view.client.AbstractDataProvider;
import com.tasktop.c2c.server.common.service.domain.Region;
import com.tasktop.c2c.server.scm.domain.Commit;
import com.tasktop.c2c.server.scm.domain.ScmRepository;

/**
 * @author cmorgan (Tasktop Technologies Inc.)
 * 
 */
public interface IScmRepoView {

	public static interface Presenter {

		/**
		 * @param value
		 */
		void branchSelected(String value);

	}

	/**
	 * @param list
	 */
	void setData(Region region, List<Commit> list);

	/**
	 * @param repository
	 */
	void setRepository(ScmRepository repository);

	/**
	 * @param dataProvider
	 */
	void setDataProvider(AbstractDataProvider<Commit> dataProvider);

	/**
	 * @param projectId
	 */
	void setProjectId(String projectId);

	void setPresenter(Presenter presenter);

}
