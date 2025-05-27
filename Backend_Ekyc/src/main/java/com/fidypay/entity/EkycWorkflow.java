package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EKYC_WORKFLOW")
public class EkycWorkflow {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EKYC_WORKFLOW_ID")
	private Long ekycWorkflowId;

	@Column(name = "CREATION_DATE")
	private Timestamp creationDate;

	@Column(name = "MERCHANT_ID", nullable = false)
	private Long merchantId;

	@Column(name = "SERVICES", nullable = false)
	private String services;

	@Column(name = "WORKFLOW_NAME", nullable = false)
	private String workflowName;

	@Column(name = "WORKFLOW_UNIQUE_ID", nullable = false)
	private String workflowUniqueId;

	@Column(name = "IS_DELETED", nullable = false)
	private char isDeleted;

	@Column(name = "DAYS", nullable = false)
	private String days;

	@Column(name = "DESCRIPTION", nullable = false)
	private String description;

	@Column(name = "IMAGE_URL", nullable = false)
	private String imageUrl;
	
	@Column(name = "KYC_TYPE", nullable = false)
	private String kycType;

	public Long getEkycWorkflowId() {
		return ekycWorkflowId;
	}

	public void setEkycWorkflowId(Long ekycWorkflowId) {
		this.ekycWorkflowId = ekycWorkflowId;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
		this.services = services;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public String getWorkflowUniqueId() {
		return workflowUniqueId;
	}

	public void setWorkflowUniqueId(String workflowUniqueId) {
		this.workflowUniqueId = workflowUniqueId;
	}

	public char getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getKycType() {
		return kycType;
	}

	public void setKycType(String kycType) {
		this.kycType = kycType;
	}
	
	

}
