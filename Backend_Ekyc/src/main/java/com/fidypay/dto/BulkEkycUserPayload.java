package com.fidypay.dto;

public class BulkEkycUserPayload {
	
	 private String Sno;

	private String workflowUniqueId;

	private String userName;

	private String userEmail;

	private String userMobile;
	
	private String reason;

	public String getSno() {
		return Sno;
	}

	public void setSno(String sno) {
		Sno = sno;
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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}


	
	
	
}
