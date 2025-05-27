package com.fidypay.dto;

public class EkycWorkflowTableResponse {

	private Long ekycWorkflowId;

	private String creationDate;

	private Long merchantId;

	private String services;

	private String workflowName;

	private String workflowUniqueId;

	private String days;

	private String description;

	private String kycType;
	
	private String isAssigned;

	public Long getEkycWorkflowId() {
		return ekycWorkflowId;
	}

	public void setEkycWorkflowId(Long ekycWorkflowId) {
		this.ekycWorkflowId = ekycWorkflowId;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
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

	public String getKycType() {
		return kycType;
	}

	public void setKycType(String kycType) {
		this.kycType = kycType;
	}

	public String getIsAssigned() {
		return isAssigned;
	}

	public void setIsAssigned(String isAssigned) {
		this.isAssigned = isAssigned;
	}

	
	
	
}
