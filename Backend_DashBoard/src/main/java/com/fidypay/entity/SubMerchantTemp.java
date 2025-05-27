package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SUB_MERCHANT_TEMP")
public class SubMerchantTemp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SUB_MERCHANT_TEMP_ID", nullable = false)
	private long subMerchantTempId;

	@Column(name = "MERCHANT_ID")
	private long merchantId;

	@Column(name = "DATE", nullable = false)
	private Timestamp date;

	@Column(name = "SUB_MERCHANT_NAME", nullable = false)
	private String subMerchantName;

	@Column(name = "MCC_CODE", nullable = false)
	private String mccCode;

	@Column(name = "MOBILE", nullable = false)
	private String mobile;

	@Column(name = "EMAIL", nullable = false)
	private String email;

	@Column(name = "SUB_MERCHANT_KEY", nullable = false)
	private String subMerchantKey;

	@Column(name = "IS_ACTIVE", nullable = false)
	private Character isActive;

	@Column(name = "JSON_RESPONSE", nullable = false)
	private String jsonResponse;

	@Column(name = "MERCHANT_SUBMERCHANT_REQUEST", nullable = false)
	private String merchantSubMerchantRequest;

	@Column(name = "MERCHANT_SUBMERCHANT_RESPONSE", nullable = false)
	private String merchantSubMerchantResponse;

	@Column(name = "IS_SUBMERCHANT", nullable = false)
	private Character isSubMerchant;

	@Column(name = "IS_ONBOARDING", nullable = false)
	private Character isOnboarding;

	@Column(name = "IS_MERCHANT", nullable = false)
	private Character isMerchant;

	@Column(name = "REASON", nullable = false)
	private String reason;

	@Column(name = "BUSINESS_TYPE", nullable = false)
	private String businessType;

	@Column(name = "BANK_ID", nullable = false)
	private String bankID;

	@Column(name = "VPA_CALL_BACK", nullable = false)
	private String vpaCallBack;

	@Column(name = "MERCHANT_USER_ID")
	private long merchantUserId;

	public long getMerchantUserId() {
		return merchantUserId;
	}

	public void setMerchantUserId(long merchantUserId) {
		this.merchantUserId = merchantUserId;
	}

	public String getVpaCallBack() {
		return vpaCallBack;
	}

	public void setVpaCallBack(String vpaCallBack) {
		this.vpaCallBack = vpaCallBack;
	}

	public Character getIsMerchant() {
		return isMerchant;
	}

	public void setIsMerchant(Character isMerchant) {
		this.isMerchant = isMerchant;
	}

	public String getBankID() {
		return bankID;
	}

	public void setBankID(String bankID) {
		this.bankID = bankID;
	}

	public long getSubMerchantTempId() {
		return subMerchantTempId;
	}

	public void setSubMerchantTempId(long subMerchantTempId) {
		this.subMerchantTempId = subMerchantTempId;
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

	public String getSubMerchantName() {
		return subMerchantName;
	}

	public void setSubMerchantName(String subMerchantName) {
		this.subMerchantName = subMerchantName;
	}

	public String getMccCode() {
		return mccCode;
	}

	public void setMccCode(String mccCode) {
		this.mccCode = mccCode;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSubMerchantKey() {
		return subMerchantKey;
	}

	public void setSubMerchantKey(String subMerchantKey) {
		this.subMerchantKey = subMerchantKey;
	}

	public Character getIsActive() {
		return isActive;
	}

	public void setIsActive(Character isActive) {
		this.isActive = isActive;
	}

	public String getJsonResponse() {
		return jsonResponse;
	}

	public void setJsonResponse(String jsonResponse) {
		this.jsonResponse = jsonResponse;
	}

	public String getMerchantSubMerchantRequest() {
		return merchantSubMerchantRequest;
	}

	public void setMerchantSubMerchantRequest(String merchantSubMerchantRequest) {
		this.merchantSubMerchantRequest = merchantSubMerchantRequest;
	}

	public String getMerchantSubMerchantResponse() {
		return merchantSubMerchantResponse;
	}

	public void setMerchantSubMerchantResponse(String merchantSubMerchantResponse) {
		this.merchantSubMerchantResponse = merchantSubMerchantResponse;
	}

	public Character getIsSubMerchant() {
		return isSubMerchant;
	}

	public void setIsSubMerchant(Character isSubMerchant) {
		this.isSubMerchant = isSubMerchant;
	}

	public Character getIsOnboarding() {
		return isOnboarding;
	}

	public void setIsOnboarding(Character isOnboarding) {
		this.isOnboarding = isOnboarding;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

}
