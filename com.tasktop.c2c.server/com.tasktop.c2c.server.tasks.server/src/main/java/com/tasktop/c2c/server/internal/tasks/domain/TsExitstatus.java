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
package com.tasktop.c2c.server.internal.tasks.domain;

// Generated May 26, 2010 11:31:55 AM by Hibernate Tools 3.3.0.GA

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TsExitstatus generated by hbm2java
 */
@Entity
@Table(name = "ts_exitstatus")
@SuppressWarnings("serial")
public class TsExitstatus implements java.io.Serializable {

	private Integer jobid;
	private int funcid;
	private Short status;
	private Integer completionTime;
	private Integer deleteAfter;

	public TsExitstatus() {
	}

	@Id
	@Column(name = "jobid", unique = true, nullable = false)
	public Integer getJobid() {
		return this.jobid;
	}

	public void setJobid(Integer jobid) {
		this.jobid = jobid;
	}

	@Column(name = "funcid", nullable = false)
	public int getFuncid() {
		return this.funcid;
	}

	public void setFuncid(int funcid) {
		this.funcid = funcid;
	}

	@Column(name = "status")
	public Short getStatus() {
		return this.status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	@Column(name = "completion_time")
	public Integer getCompletionTime() {
		return this.completionTime;
	}

	public void setCompletionTime(Integer completionTime) {
		this.completionTime = completionTime;
	}

	@Column(name = "delete_after")
	public Integer getDeleteAfter() {
		return this.deleteAfter;
	}

	public void setDeleteAfter(Integer deleteAfter) {
		this.deleteAfter = deleteAfter;
	}

}
