package com.fidypay.request;

public class SubmerchantDocsRequest {

	private String panCardAuthorizerKey;
	private String isVerPanCardAuthorizer;
	private String isVerAadhaarCardAuthorizer;
	private String aadhaarCardAuthorizerKey;
	private String isVerGST;
	private String gstKey;
	private String gstNumber;
	private String isVerCancelCheque;
	private String cancelChecqueKey;
	private String flag;


	public String getPanCardAuthorizerKey() {
		return panCardAuthorizerKey;
	}

	public void setPanCardAuthorizerKey(String panCardAuthorizerKey) {
		this.panCardAuthorizerKey = panCardAuthorizerKey;
	}

	public String getIsVerPanCardAuthorizer() {
		return isVerPanCardAuthorizer;
	}

	public void setIsVerPanCardAuthorizer(String isVerPanCardAuthorizer) {
		this.isVerPanCardAuthorizer = isVerPanCardAuthorizer;
	}

	public String getIsVerAadhaarCardAuthorizer() {
		return isVerAadhaarCardAuthorizer;
	}

	public void setIsVerAadhaarCardAuthorizer(String isVerAadhaarCardAuthorizer) {
		this.isVerAadhaarCardAuthorizer = isVerAadhaarCardAuthorizer;
	}

	public String getAadhaarCardAuthorizerKey() {
		return aadhaarCardAuthorizerKey;
	}

	public void setAadhaarCardAuthorizerKey(String aadhaarCardAuthorizerKey) {
		this.aadhaarCardAuthorizerKey = aadhaarCardAuthorizerKey;
	}

	public String getIsVerGST() {
		return isVerGST;
	}

	public void setIsVerGST(String isVerGST) {
		this.isVerGST = isVerGST;
	}

	public String getGstKey() {
		return gstKey;
	}

	public void setGstKey(String gstKey) {
		this.gstKey = gstKey;
	}

	public String getGstNumber() {
		return gstNumber;
	}

	public void setGstNumber(String gstNumber) {
		this.gstNumber = gstNumber;
	}


	public String getIsVerCancelCheque() {
		return isVerCancelCheque;
	}

	public void setIsVerCancelCheque(String isVerCancelCheque) {
		this.isVerCancelCheque = isVerCancelCheque;
	}

	public String getCancelChecqueKey() {
		return cancelChecqueKey;
	}

	public void setCancelChecqueKey(String cancelChecqueKey) {
		this.cancelChecqueKey = cancelChecqueKey;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	

}
