package com.fidypay.request;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Value;

public class EkycRequests {

	@NotEmpty(message = "userName can not be empty.")
	private String userName;

	@NotEmpty(message = "userEmail can not be empty.")
	private String userEmail;

	@NotEmpty(message = "userMobile can not be empty.")
	private String userMobile;

	@NotEmpty(message = "workflowName can not be empty.")
	private String workflowName;

	@Value("10")
	private Integer pageSize;
	@Value("0")
	private Integer pageNo;

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

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

}
