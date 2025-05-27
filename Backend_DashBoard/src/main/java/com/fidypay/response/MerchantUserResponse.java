package com.fidypay.response;

public class MerchantUserResponse {
	private int sNo;
	private long merchantUserId;
	private long merchantId;
	private String merchantUserEmail;
	private String merchantUserMobileNo;
	private String merchantUserName;
	private char isActive;
	private String merchantUserType;
	private String merchantBusiness;
	private String merchantUserKey;

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

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

	public String getMerchantBusiness() {
		return merchantBusiness;
	}

	public void setMerchantBusiness(String merchantBusiness) {
		this.merchantBusiness = merchantBusiness;
	}

	public String getMerchantUserKey() {
		return merchantUserKey;
	}

	public void setMerchantUserKey(String merchantUserKey) {
		this.merchantUserKey = merchantUserKey;
	}

}
