package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MERCHANT_USER")
public class MerchantUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MERCHANT_USER_ID")
	private long merchantUserId;

	@Column(name = "MERCHANT_ID")
	private long merchantId;

	@Column(name = "DATE")
	private Timestamp date;

	@Column(name = "MERCHANT_USER_EMAIL")
	private String merchantUserEmail;

	@Column(name = "MERCHANT_USER_MOBILE_NO")
	private String merchantUserMobileNo;

	@Column(name = "MERCHANT_USER_NAME")
	private String merchantUserName;

	@Column(name = "MERCHANT_USER_PASSWORD")
	private String merchantUserPassword;

	@Column(name = "IS_ACTIVE")
	private char isActive;

	@Column(name = "MERCHANT_USER_TYPE")
	private String merchantUserType;

	@Column(name = "MERCHANT_BUSINESS_NAME")
	private String merchantBusinessName;

	@Column(name = "MERCHANT_USER_KEY")
	private String merchantUserKey;

	@Column(name = "LOGIN_COUNT")
	private long loginCount;

	@Column(name = "UPDATE_PASSWORD_DATE")
	private Timestamp updatePasswordDate;

	public long getMerchantUserId() {
		return merchantUserId;
	}

	public void setMerchantUserId(long merchantUserId) {
		this.merchantUserId = merchantUserId;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getMerchantUserEmail() {
		return merchantUserEmail;
	}

	public void setMerchantUserEmail(String merchantUserEmail) {
		this.merchantUserEmail = merchantUserEmail;
	}

	public String getMerchantUserMobileNo() {
		return merchantUserMobileNo;
	}

	public void setMerchantUserMobileNo(String merchantUserMobileNo) {
		this.merchantUserMobileNo = merchantUserMobileNo;
	}

	public String getMerchantUserName() {
		return merchantUserName;
	}

	public void setMerchantUserName(String merchantUserName) {
		this.merchantUserName = merchantUserName;
	}

	public String getMerchantUserPassword() {
		return merchantUserPassword;
	}

	public void setMerchantUserPassword(String merchantUserPassword) {
		this.merchantUserPassword = merchantUserPassword;
	}

	public char getIsActive() {
		return isActive;
	}

	public void setIsActive(char isActive) {
		this.isActive = isActive;
	}

	public String getMerchantUserType() {
		return merchantUserType;
	}

	public void setMerchantUserType(String merchantUserType) {
		this.merchantUserType = merchantUserType;
	}

	public String getMerchantBusinessName() {
		return merchantBusinessName;
	}

	public void setMerchantBusinessName(String merchantBusinessName) {
		this.merchantBusinessName = merchantBusinessName;
	}

	public String getMerchantUserKey() {
		return merchantUserKey;
	}

	public void setMerchantUserKey(String merchantUserKey) {
		this.merchantUserKey = merchantUserKey;
	}

	public long getLoginCount() {
		return loginCount;
	}

	public void setLoginCount(long loginCount) {
		this.loginCount = loginCount;
	}

	public Timestamp getUpdatePasswordDate() {
		return updatePasswordDate;
	}

	public void setUpdatePasswordDate(Timestamp updatePasswordDate) {
		this.updatePasswordDate = updatePasswordDate;
	}

}
