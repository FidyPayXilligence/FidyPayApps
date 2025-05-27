package com.fidypay.dto;

public class FetchBillDTO {

	private String paramName;
	private String paramValue;
	private String customerMobile;
	private String billerId;
	private String category;

//	public FetchBillDTO() {
//
//	}

//	public FetchBillDTO(String paramName, String paramValue, String customerMobile, String billerId, String category) {
//
//		this.paramName = paramName;
//		this.paramValue = paramValue;
//		this.customerMobile = customerMobile;
//		this.billerId = billerId;
//		this.category = category;
//	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public String getCustomerMobile() {
		return customerMobile;
	}

	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}

	public String getBillerId() {
		return billerId;
	}

	public void setBillerId(String billerId) {
		this.billerId = billerId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
