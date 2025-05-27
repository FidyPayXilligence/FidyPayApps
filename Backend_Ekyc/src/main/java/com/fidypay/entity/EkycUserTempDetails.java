package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EKYC_USER_TEMP_DETAILS")
public class EkycUserTempDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EKYC_USER_TEMP_ID")
	private Long ekycUserTempId;

	@Column(name = "CREATION_DATE", nullable = false)
	private Timestamp creationDate;

	@Column(name = "MERCHANT_ID", nullable = false)
	private Long merchantId;

	@Column(name = "WORKFLOW_UNIQUE_ID", nullable = false)
	private String workflowUniqueId;

	@Column(name = "USER_NAME", nullable = false)
	private String userName;

	@Column(name = "USER_EMAIL", nullable = false)
	private String userEmail;

	@Column(name = "USER_MOBILE", nullable = false)
	private String userMobile;
	
	@Column(name = "SERVICE_COUNT")
	private Character serviceCount;
	
	@Column(name = "IS_VERIFIED")
	private Character isVerified;
	
	@Column(name = "REASON", nullable = false)
	private String reason;

	public Long getEkycUserTempId() {
		return ekycUserTempId;
	}

	public void setEkycUserTempId(Long ekycUserTempId) {
		this.ekycUserTempId = ekycUserTempId;
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

	public Character getServiceCount() {
		return serviceCount;
	}

	public void setServiceCount(Character serviceCount) {
		this.serviceCount = serviceCount;
	}

	public Character getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Character isVerified) {
		this.isVerified = isVerified;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	
	
	
}
