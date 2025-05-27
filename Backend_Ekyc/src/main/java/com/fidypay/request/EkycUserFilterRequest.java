package com.fidypay.request;

public class EkycUserFilterRequest {
	
	
	private String userMobile;
	private String userEmail;
	private String userName;
	private Long ekycWorkflowId;

	
	public String getUserMobile() {
		return userMobile;
	}
	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Long getEkycWorkflowId() {
		return ekycWorkflowId;
	}
	public void setEkycWorkflowId(Long ekycWorkflowId) {
		this.ekycWorkflowId = ekycWorkflowId;
	}
	
	

}
