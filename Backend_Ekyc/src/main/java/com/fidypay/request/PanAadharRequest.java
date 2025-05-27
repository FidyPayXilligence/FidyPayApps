package com.fidypay.request;

import javax.validation.constraints.Pattern;

public class PanAadharRequest {

	@Pattern(regexp = "^{1,30}$,", message = "Invalid merchantTrxnRefId, Special character not allowed")
	private String uId;
	private String panNumber;

	public String getuId() {
		return uId;
	}
	public void setuId(String uId) {
		this.uId = uId;
	}
	public String getPanNumber() {
		return panNumber;
	}
	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	
	
	
}
