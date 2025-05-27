package com.fidypay.response;

public class SubMerchantTempResponse {

	private long subMerchantTempId;
	private int sNo;
	private String date;
	private String mobile;
	private String email;
	private String subMerchantName;
	private String mccCode;
	private String reason;
	private String businessType;
	private String token;
	private Character isActive;
	private String jsonResponse;
	private long merchantId;
	private String merchantSubMerchantRequest;
	private String merchantSubMerchantResponse;
	private Character isSubMerchant;
	private Character isOnboarding;
	private String logo;

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public long getSubMerchantTempId() {
		return subMerchantTempId;
	}

	public void setSubMerchantTempId(long subMerchantTempId) {
		this.subMerchantTempId = subMerchantTempId;
	}

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
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

}
