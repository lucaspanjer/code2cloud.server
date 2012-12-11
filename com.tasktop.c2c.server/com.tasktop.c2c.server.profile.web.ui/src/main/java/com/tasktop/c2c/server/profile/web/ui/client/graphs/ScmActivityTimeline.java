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
package com.tasktop.c2c.server.profile.web.ui.client.graphs;

import java.util.List;

import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.visualizations.AreaChart;
import com.tasktop.c2c.server.profile.web.ui.client.gin.AppGinjector;
import com.tasktop.c2c.server.profile.web.ui.client.resources.ProfileMessages;
import com.tasktop.c2c.server.scm.domain.ScmSummary;

public class ScmActivityTimeline extends AreaChart {

	private ProfileMessages messages = AppGinjector.get.instance().getProfileMessages();

	public void draw(List<ScmSummary> summaries) {
		AbstractDataTable data = createData(summaries);
		super.draw(data, createOptions());
	}

	private Options createOptions() {
		Options options = Options.create();
		options.setWidth(DashboardChartConstants.TIMELINE_WIDTH);
		options.setHeight(DashboardChartConstants.TIMELINE_HEIGHT);
		options.setTitle(messages.commits());
		options.setLegend(LegendPosition.NONE);
		options.setPointSize(DashboardChartConstants.POINT_SIZE);
		options.set("hAxis.fontSize", DashboardChartConstants.H_AXIS_FONT_SIZE);
		options.setTitleFontSize(DashboardChartConstants.TITLE_FONT_SIZE);
		return options;
	}

	private AbstractDataTable createData(List<ScmSummary> summaries) {
		DataTable data = DataTable.create();

		data.addColumn(ColumnType.DATE, messages.day());

		data.addColumn(ColumnType.NUMBER, messages.commits());

		data.addRows(summaries.size());
		int row = 0;
		for (ScmSummary summary : summaries) {
			int col = 0;
			data.setValue(row, col++, summary.getDate());
			data.setValue(row, col++, summary.getAmount());
			row++;
		}
		return data;
	}
}
