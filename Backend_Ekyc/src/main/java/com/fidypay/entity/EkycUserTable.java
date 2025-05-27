package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EKYC_USER")
public class EkycUserTable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EKYC_USER_ID")
	private Long ekycUserId;

	@Column(name = "CREATION_DATE", nullable = false)
	private Timestamp creationDate;

	@Column(name = "MERCHANT_ID", nullable = false)
	private Long merchantId;

	@Column(name = "USER_UNIQUE_ID", nullable = false)
	private String userUniqueId;

	@Column(name = "EKYC_WORKFLOW_ID", nullable = false)
	private Long ekycWorkflowId;

	@Column(name = "USER_NAME", nullable = false)
	private String userName;

	@Column(name = "USER_EMAIL", nullable = false)
	private String userEmail;

	@Column(name = "USER_MOBILE", nullable = false)
	private String userMobile;

	@Column(name = "IS_VERIFIED", nullable = false)
	private char isVerified;

	@Column(name = "IS_DELETED", nullable = false)
	private char isDeleted;
	
	@Column(name = "ACTIVATION_DATE")
	private Timestamp activationDate;

	@Column(name = "WORKFLOW_NAME", nullable = false)
	private String workflowName;
	
	@Column(name = "SERVICES_JSON")
	private String servicesJson;

	
	@Column(name = "SERVICE_COUNT")
	private char serviceCount;
	
	
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

	public Long getEkycWorkflowId() {
		return ekycWorkflowId;
	}

	public void setEkycWorkflowId(Long ekycWorkflowId) {
		this.ekycWorkflowId = ekycWorkflowId;
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

	public char getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(char isVerified) {
		this.isVerified = isVerified;
	}

	public char getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}

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

	public Timestamp getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Timestamp activationDate) {
		this.activationDate = activationDate;
	}

	public char getServiceCount() {
		return serviceCount;
	}

	public void setServiceCount(char serviceCount) {
		this.serviceCount = serviceCount;
	}

	
	
	
	
}
