package com.fidypay.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;

public class ENachTxnInfoRequest {

	private String mandateId;
	private String customerBankAccountNumber;
	private String mobileNumber;
	private String serviceProviderUtilityCode;
	@Value("10")
	private Integer pageSize;
	@Value("0")
	private Integer pageNo;

	public String getMandateId() {
		return mandateId;
	}

	public void setMandateId(String mandateId) {
		this.mandateId = mandateId;
	}

	public String getCustomerBankAccountNumber() {
		return customerBankAccountNumber;
	}

	public void setCustomerBankAccountNumber(String customerBankAccountNumber) {
		this.customerBankAccountNumber = customerBankAccountNumber;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getServiceProviderUtilityCode() {
		return serviceProviderUtilityCode;
	}

	public void setServiceProviderUtilityCode(String serviceProviderUtilityCode) {
		this.serviceProviderUtilityCode = serviceProviderUtilityCode;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

}
