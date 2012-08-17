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
package com.tasktop.c2c.server.profile.web.ui.client.view.components;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.tasktop.c2c.server.profile.domain.build.HudsonStatus;
import com.tasktop.c2c.server.profile.domain.build.JobSummary;

public class HudsonStatusView extends Composite {
	interface Binder extends UiBinder<Widget, HudsonStatusView> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	private int maxJobs = 15;

	public HudsonStatusView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Panel jobSummaryPanel;

	public void setStatus(HudsonStatus status) {
		jobSummaryPanel.clear();
		if (status == null) {
			return;
		}
		List<JobSummary> jobs = status.getJobs();
		if (jobs.size() > maxJobs) {
			Collections.sort(jobs, new Comparator<JobSummary>() {

				@Override
				public int compare(JobSummary j1, JobSummary j2) {
					if (hasLastBuildTs(j1)) {
						if (hasLastBuildTs(j2)) {
							return j2.getLastBuild().getTimestamp().compareTo(j1.getLastBuild().getTimestamp());
						} else {
							return -1;
						}
					} else {
						if (hasLastBuildTs(j2)) {
							return 1;
						} else {
							return 0;
						}
					}
				}

				private boolean hasLastBuildTs(JobSummary js) {
					return js.getLastBuild() != null && js.getLastBuild().getTimestamp() != null;
				}
			});
			jobs = jobs.subList(0, maxJobs);
		}
		setJobs(jobs);
	}

	private void setJobs(List<JobSummary> jobs) {
		for (JobSummary job : jobs) {
			jobSummaryPanel.add(new HudsonJobRow(job));
		}

	}

}
