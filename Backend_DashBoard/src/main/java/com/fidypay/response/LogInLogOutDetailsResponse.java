package com.fidypay.response;

public class LogInLogOutDetailsResponse {

	private Long logInDetailsId;

	private Long merchantId;

	private String logInTime;

	private String logOutTime;

	private String date;
	
	private String description;
	 private String loginUniqueId;

	public Long getLogInDetailsId() {
		return logInDetailsId;
	}

	public void setLogInDetailsId(Long logInDetailsId) {
		this.logInDetailsId = logInDetailsId;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public String getLogInTime() {
		return logInTime;
	}

	public void setLogInTime(String logInTime) {
		this.logInTime = logInTime;
	}

	public String getLogOutTime() {
		return logOutTime;
	}

	public void setLogOutTime(String logOutTime) {
		this.logOutTime = logOutTime;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLoginUniqueId() {
		return loginUniqueId;
	}

	public void setLoginUniqueId(String loginUniqueId) {
		this.loginUniqueId = loginUniqueId;
	}

	

}
