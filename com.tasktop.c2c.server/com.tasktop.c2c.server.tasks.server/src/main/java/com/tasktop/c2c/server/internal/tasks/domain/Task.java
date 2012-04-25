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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.eclipse.persistence.annotations.Convert;

import com.tasktop.c2c.server.tasks.domain.TaskFieldConstants;

/**
 * Generated by hbm2java
 */
@Entity
@Table(name = "bugs", uniqueConstraints = @UniqueConstraint(columnNames = "alias"))
@SuppressWarnings("serial")
public class Task extends AbstractIdentified<Integer> implements Serializable {
	public static final String CUSTOM_TYPE_FIELD_NAME = TaskFieldConstants.TASK_TYPE_FIELD;
	public static final String CUSTOM_ITERATION_FIELD_NAME = TaskFieldConstants.ITERATION_FIELD;
	public static final String CUSTOM_EXTERNAL_TASK_RELATIONS_FIELD_NAME = TaskFieldConstants.EXTERNAL_TASK_RELATIONS_FIELD;

	private Integer id;
	private String version;
	private Profile reporter;
	private Profile assignee;
	private Product product;
	private Component component;
	private String bugFileLoc;
	private String severity;
	private String status;
	private Date creationTs;
	private Date deltaTs;
	private String shortDesc;
	private String opSys;
	private String priority;
	private String repPlatform;
	private String resolution;
	private String targetMilestone;
	private Integer qaContact;
	private String statusWhiteboard;
	private int votes;
	private String keywords;
	private Date lastdiffed;
	private boolean everconfirmed;
	private boolean reporterAccessible;
	private boolean cclistAccessible;
	private BigDecimal estimatedTime;
	private BigDecimal remainingTime;
	private Date deadline;
	private String alias;
	private Set<BugGroupMap> bugGroupMaps = new HashSet<BugGroupMap>(0);
	private Duplicate duplicatesByBugId;
	private List<Dependency> dependenciesesForBlocked = new ArrayList<Dependency>(0);
	private Set<Duplicate> duplicatesesForDupeOf = new HashSet<Duplicate>(0);
	private List<Attachment> attachments = new ArrayList<Attachment>(0);
	private List<Keyword> keywordses = new ArrayList<Keyword>(0);
	private Set<TaskActivity> bugsActivities = new HashSet<TaskActivity>(0);
	private Set<Flags> flagses = new HashSet<Flags>(0);
	private Set<Vote> voteses = new HashSet<Vote>(0);
	private List<Comment> comments = new ArrayList<Comment>(0);
	private List<Cc> ccs = new ArrayList<Cc>(0);
	private List<Dependency> dependenciesesForDependson = new ArrayList<Dependency>(0);

	// Mandatory custom fields
	private String taskType;
	private String iteration;
	private String externalTaskRelations;

	public Task() {
	}

	@ActivityIgnored
	@Id
	@Column(name = "bug_id", unique = true, nullable = false)
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "version", nullable = false, length = 64)
	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Column(name = "cf_tasktype", nullable = false, length = 64)
	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	@Column(name = "cf_task_relations", nullable = true)
	public String getExternalTaskRelations() {
		return externalTaskRelations;
	}

	public void setExternalTaskRelations(String externalTaskRelations) {
		this.externalTaskRelations = externalTaskRelations;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter", nullable = false)
	public Profile getReporter() {
		return this.reporter;
	}

	public void setReporter(Profile reporter) {
		this.reporter = reporter;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assigned_to", nullable = true)
	public Profile getAssignee() {
		return this.assignee;
	}

	public void setAssignee(Profile assignee) {
		this.assignee = assignee;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "component_id", nullable = false)
	public Component getComponent() {
		return this.component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	@ActivityIgnored
	@Column(name = "bug_file_loc", length = 16777215)
	public String getBugFileLoc() {
		return this.bugFileLoc;
	}

	public void setBugFileLoc(String bugFileLoc) {
		this.bugFileLoc = bugFileLoc;
	}

	@Column(name = "bug_severity", nullable = false, length = 64)
	public String getSeverity() {
		return this.severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	@Column(name = "bug_status", nullable = false, length = 64)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@ActivityIgnored
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creation_ts", length = 19)
	public Date getCreationTs() {
		return this.creationTs;
	}

	public void setCreationTs(Date creationTs) {
		this.creationTs = creationTs;
	}

	@ActivityIgnored
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "delta_ts", nullable = false, length = 19)
	public Date getDeltaTs() {
		return this.deltaTs;
	}

	public void setDeltaTs(Date deltaTs) {
		this.deltaTs = deltaTs;
	}

	@Column(name = "short_desc", nullable = false)
	public String getShortDesc() {
		return this.shortDesc;
	}

	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}

	@ActivityIgnored
	@Column(name = "op_sys", nullable = false, length = 64)
	public String getOpSys() {
		return this.opSys;
	}

	public void setOpSys(String opSys) {
		this.opSys = opSys;
	}

	@Column(name = "priority", nullable = false, length = 64)
	public String getPriority() {
		return this.priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	@ActivityIgnored
	@Column(name = "rep_platform", nullable = false, length = 64)
	public String getRepPlatform() {
		return this.repPlatform;
	}

	public void setRepPlatform(String repPlatform) {
		this.repPlatform = repPlatform;
	}

	@Column(name = "resolution", nullable = false, length = 64)
	public String getResolution() {
		return this.resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	@Column(name = "target_milestone", nullable = false, length = 20)
	public String getTargetMilestone() {
		return this.targetMilestone;
	}

	public void setTargetMilestone(String targetMilestone) {
		this.targetMilestone = targetMilestone;
	}

	@ActivityIgnored
	@Column(name = "qa_contact")
	public Integer getQaContact() {
		return this.qaContact;
	}

	public void setQaContact(Integer qaContact) {
		this.qaContact = qaContact;
	}

	@ActivityIgnored
	@Column(name = "status_whiteboard", nullable = false, length = 16777215)
	public String getStatusWhiteboard() {
		return this.statusWhiteboard;
	}

	public void setStatusWhiteboard(String statusWhiteboard) {
		this.statusWhiteboard = statusWhiteboard;
	}

	@Column(name = "votes", nullable = false)
	public int getVotes() {
		return this.votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	@Column(name = "keywords", nullable = false, length = 16777215)
	public String getKeywords() {
		return this.keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "lastdiffed", length = 19)
	public Date getLastdiffed() {
		return this.lastdiffed;
	}

	public void setLastdiffed(Date lastdiffed) {
		this.lastdiffed = lastdiffed;
	}

	@ActivityIgnored
	@Column(name = "everconfirmed", nullable = false)
	@Convert("booleanToByte")
	public boolean getEverconfirmed() {
		return this.everconfirmed;
	}

	public void setEverconfirmed(boolean everconfirmed) {
		this.everconfirmed = everconfirmed;
	}

	@ActivityIgnored
	@Column(name = "reporter_accessible", nullable = false)
	@Convert("booleanToByte")
	public boolean getReporterAccessible() {
		return this.reporterAccessible;
	}

	public void setReporterAccessible(boolean reporterAccessible) {
		this.reporterAccessible = reporterAccessible;
	}

	@ActivityIgnored
	@Column(name = "cclist_accessible", nullable = false)
	@Convert("booleanToByte")
	public boolean getCclistAccessible() {
		return this.cclistAccessible;
	}

	public void setCclistAccessible(boolean cclistAccessible) {
		this.cclistAccessible = cclistAccessible;
	}

	@Column(name = "estimated_time", nullable = true, precision = 7)
	public BigDecimal getEstimatedTime() {
		return this.estimatedTime;
	}

	public void setEstimatedTime(BigDecimal estimatedTime) {
		this.estimatedTime = estimatedTime;
	}

	@Column(name = "remaining_time", nullable = false, precision = 7)
	public BigDecimal getRemainingTime() {
		return this.remainingTime;
	}

	public void setRemainingTime(BigDecimal remainingTime) {
		this.remainingTime = remainingTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "deadline", length = 19)
	public Date getDeadline() {
		return this.deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	@Column(name = "alias", unique = true, length = 20)
	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bugs")
	public Set<BugGroupMap> getBugGroupMaps() {
		return this.bugGroupMaps;
	}

	public void setBugGroupMaps(Set<BugGroupMap> bugGroupMaps) {
		this.bugGroupMaps = bugGroupMaps;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "bugsByBugId", cascade = CascadeType.ALL, orphanRemoval = true)
	public Duplicate getDuplicatesByBugId() {
		return this.duplicatesByBugId;
	}

	public void setDuplicatesByBugId(Duplicate duplicatesByBugId) {
		this.duplicatesByBugId = duplicatesByBugId;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bugsByBlocked", orphanRemoval = true)
	@OrderBy("id.dependson")
	public List<Dependency> getDependenciesesForBlocked() {
		return this.dependenciesesForBlocked;
	}

	public void setDependenciesesForBlocked(List<Dependency> dependenciesesForBlocked) {
		this.dependenciesesForBlocked = dependenciesesForBlocked;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bugsByDupeOf")
	public Set<Duplicate> getDuplicatesesForDupeOf() {
		return this.duplicatesesForDupeOf;
	}

	public void setDuplicatesesForDupeOf(Set<Duplicate> duplicatesesForDupeOf) {
		this.duplicatesesForDupeOf = duplicatesesForDupeOf;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bugs")
	@OrderBy("id")
	public List<Attachment> getAttachments() {
		return this.attachments;
	}

	public void setAttachments(List<Attachment> attachmentses) {
		this.attachments = attachmentses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bugs", cascade = CascadeType.PERSIST)
	@OrderBy("id")
	public List<Keyword> getKeywordses() {
		return this.keywordses;
	}

	public void setKeywordses(List<Keyword> keywordses) {
		this.keywordses = keywordses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bugs")
	public Set<TaskActivity> getBugsActivities() {
		return this.bugsActivities;
	}

	public void setBugsActivities(Set<TaskActivity> bugsActivities) {
		this.bugsActivities = bugsActivities;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bugs")
	public Set<Flags> getFlagses() {
		return this.flagses;
	}

	public void setFlagses(Set<Flags> flagses) {
		this.flagses = flagses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bugs")
	public Set<Vote> getVoteses() {
		return this.voteses;
	}

	public void setVoteses(Set<Vote> voteses) {
		this.voteses = voteses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "task", cascade = CascadeType.PERSIST)
	@OrderBy("creationTs")
	public List<Comment> getComments() {
		return this.comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bugs", orphanRemoval = true)
	public List<Cc> getCcs() {
		return this.ccs;
	}

	public void setCcs(List<Cc> ccs) {
		this.ccs = ccs;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bugsByDependson", orphanRemoval = true)
	@OrderBy("id.blocked")
	public List<Dependency> getDependenciesesForDependson() {
		return this.dependenciesesForDependson;
	}

	public void setDependenciesesForDependson(List<Dependency> dependenciesesForDependson) {
		this.dependenciesesForDependson = dependenciesesForDependson;
	}

	@PrePersist
	public void prePersist() {
		fillCreationTs();
		fillDeltaTs();
	}

	// Note that we call this manually so that it will be hit in tests which wrap multiple service calls in a single
	// transaction
	public void preUpdate() {
		fillDeltaTs();
	}

	private void fillCreationTs() {
		if (creationTs == null) {
			creationTs = new Date();
		}
	}

	private void fillDeltaTs() {
		setDeltaTs(new Date(System.currentTimeMillis()));
	}

	/**
	 * Add the given profile to the {@link #getCcs() ccs}. If an existing Cc exists for the given profile, a new Cc is
	 * not created. If a new Cc is created, it is not {@link EntityManager#persist(Object) persisted} before returning.
	 * 
	 * @param profile
	 *            the profile for which a CC is needed
	 * @return the new or existing Cc for the given profile
	 */
	public Cc addCc(Profile profile) {
		for (Cc cc : getCcs()) {
			if (cc.getProfiles().equals(profile)) {
				return cc;
			}
		}
		Cc cc = new Cc();
		cc.setProfiles(profile);
		cc.setBugs(this);
		cc.setId(new CcId(getId(), profile.getId()));
		getCcs().add(cc);
		profile.getCcs().add(cc);
		return cc;
	}

	/**
	 * add a task as blocked by this task
	 * 
	 * @param blockedTask
	 * @return
	 */
	public Dependency addBlocked(Task blockedTask) {
		if (blockedTask == null || blockedTask == this) {
			throw new IllegalArgumentException();
		}
		if (getId() == null) {
			throw new IllegalStateException();
		}
		Dependency dependency = new Dependency();
		dependency.setId(new DependencyId(blockedTask.getId(), getId()));
		dependency.setBugsByDependson(this);
		dependency.setBugsByBlocked(blockedTask);

		getDependenciesesForDependson().add(dependency);

		return dependency;
	}
}
