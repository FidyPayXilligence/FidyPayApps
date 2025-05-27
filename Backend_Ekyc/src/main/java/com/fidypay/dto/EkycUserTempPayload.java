package com.fidypay.dto;

public class EkycUserTempPayload {
	
	private int sNo;
	
	private Long ekycUserTempId;

	private String creationDate;

	private Long merchantId;

	private String workflowUniqueId;

	private String userName;

	private String userEmail;

	private String userMobile;
	
	private String isVerified;
	
	private String reason;
	
	
	
	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

	public Long getEkycUserTempId() {
		return ekycUserTempId;
	}

	public void setEkycUserTempId(Long ekycUserTempId) {
		this.ekycUserTempId = ekycUserTempId;
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

	public String getWorkflowUniqueId() {
		return workflowUniqueId;
	}

	public void setWorkflowUniqueId(String workflowUniqueId) {
		this.workflowUniqueId = workflowUniqueId;
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

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public String getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(String isVerified) {
		this.isVerified = isVerified;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}


	
	
}
