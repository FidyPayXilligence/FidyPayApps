package com.fidypay.dto;

public class EkycUserRequestsResponse {

	private Long ekycUserId;

	private Long ekycWorkflowId;

	private String creationDate;

	private Long merchantId;

	private String userName;

	private String userEmail;

	private String mobile;

	private char isVerified;

	private String userUniqueId;
	
	private String workflowName;

	private String servicesJson;
	
	private String serviceCount;
	
	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public Long getEkycUserId() {
		return ekycUserId;
	}

	public void setEkycUserId(Long ekycUserId) {
		this.ekycUserId = ekycUserId;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public char getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(char isVerified) {
		this.isVerified = isVerified;
	}
//
//	public char getIsDeleted() {
//		return isDeleted;
//	}
//
//	public void setIsDeleted(char isDeleted) {
//		this.isDeleted = isDeleted;
//	}

	public String getUserUniqueId() {
		return userUniqueId;
	}

	public void setUserUniqueId(String userUniqueId) {
		this.userUniqueId = userUniqueId;
	}

	public String getServicesJson() {
		return servicesJson;
	}

	public void setServicesJson(String servicesJson) {
		this.servicesJson = servicesJson;
	}

	public String getServiceCount() {
		return serviceCount;
	}

	public void setServiceCount(String serviceCount) {
		this.serviceCount = serviceCount;
	}
	
	

}