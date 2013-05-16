/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies
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

import java.io.Serializable;
import java.util.List;

/**
 * @author clint (Tasktop Technologies Inc.)
 * 
 */
@SuppressWarnings("serial")
public class ProjectTemplateMetadata implements Serializable {

	public static class GitFileReplacement implements Serializable {
		private String patternToReplace;
		private String filePattern;
		private String fileName;
		private ProjectTemplateProperty property;

		public String getPatternToReplace() {
			return patternToReplace;
		}

		public void setPatternToReplace(String patternToReplace) {
			this.patternToReplace = patternToReplace;
		}

		public String getFilePattern() {
			return filePattern;
		}

		public void setFilePattern(String filePattern) {
			this.filePattern = filePattern;
		}

		public ProjectTemplateProperty getProperty() {
			return property;
		}

		public void setProperty(ProjectTemplateProperty property) {
			this.property = property;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
	}

	public static class BuildJobConfigReplacement implements Serializable {
		private String jobName;
		private String patternToReplace;
		private ProjectTemplateProperty property;

		public String getJobName() {
			return jobName;
		}

		public void setJobName(String jobName) {
			this.jobName = jobName;
		}

		public String getPatternToReplace() {
			return patternToReplace;
		}

		public void setPatternToReplace(String patternToReplace) {
			this.patternToReplace = patternToReplace;
		}

		public ProjectTemplateProperty getProperty() {
			return property;
		}

		public void setProperty(ProjectTemplateProperty property) {
			this.property = property;
		}
	}

	private List<GitFileReplacement> fileReplacements;
	private List<BuildJobConfigReplacement> buildJobReplacements;

	public List<GitFileReplacement> getFileReplacements() {
		return fileReplacements;
	}

	public void setFileReplacements(List<GitFileReplacement> fileReplacements) {
		this.fileReplacements = fileReplacements;
	}

	public List<BuildJobConfigReplacement> getBuildJobReplacements() {
		return buildJobReplacements;
	}

	public void setBuildJobReplacements(List<BuildJobConfigReplacement> buildJobReplacements) {
		this.buildJobReplacements = buildJobReplacements;
	}

}
