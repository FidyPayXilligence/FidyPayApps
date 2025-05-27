package com.fidypay.dto;

public class BillerDetailPayload {
	
	
	private String billersId;
	private String billersName;
	private String paramName;
	private String category;
	
	public String getBillersId() {
		return billersId;
	}
	public void setBillersId(String billersId) {
		this.billersId = billersId;
	}
	public String getBillersName() {
		return billersName;
	}
	public void setBillersName(String billersName) {
		this.billersName = billersName;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	
	
}
