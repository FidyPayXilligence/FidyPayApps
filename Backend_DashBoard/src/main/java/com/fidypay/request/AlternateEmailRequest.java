package com.fidypay.request;

public class AlternateEmailRequest {
	
	private String callBackUrl;
	private String redirectURL;
	private String domesticCallBackUrl;
	private String pgMerchantId;
	private String MCC;
	
	
	public String getCallBackUrl() {
		return callBackUrl;
	}
	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}
	public String getRedirectURL() {
		return redirectURL;
	}
	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}
	public String getDomesticCallBackUrl() {
		return domesticCallBackUrl;
	}
	public void setDomesticCallBackUrl(String domesticCallBackUrl) {
		this.domesticCallBackUrl = domesticCallBackUrl;
	}
	public String getPgMerchantId() {
		return pgMerchantId;
	}
	public void setPgMerchantId(String pgMerchantId) {
		this.pgMerchantId = pgMerchantId;
	}
	public String getMCC() {
		return MCC;
	}
	public void setMCC(String mCC) {
		MCC = mCC;
	}
	
	
	
}
