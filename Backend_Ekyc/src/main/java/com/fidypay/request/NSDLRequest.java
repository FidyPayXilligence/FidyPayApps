package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class NSDLRequest {

	@NotBlank(message = "bankAccountNumber can not be blank")
	@Size(min = 8,max = 30, message = "bankAccountNumber size must be 8 to 30")
	@Pattern(regexp = "^[a-zA-Z0-9]+$" , message = "please pass valid bankAccountNumber")
	private String bankAccountNumber;

	
	@NotBlank(message = "mobileNo can not be blank")
	@Pattern(regexp = "\\d{10}", message = "please pass valid mobileNo")
	private String mobileNo;

	@NotBlank(message = "bankIFSCCode can not be blank")
	private String bankIFSCCode;

	@NotBlank(message = "bankAccountType can not be blank")
	@Size(min = 1,max = 20, message = "bankAccountType size must be 1 to 20")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "please pass valid bankAccountType")
	private String bankAccountType;

	@NotBlank(message = "merchantName can not be blank")
	private String merchantName;
	
	@NotBlank(message = "merchantTrxnRefId can not be blank")
	@Size(min = 1, max = 30, message = "merchantTrxnRefId size must be 1 to 30")
	@Pattern(regexp = "^[a-zA-Z0-9]+$" , message = "please pass valid merchantTrxnRefId")
	private String merchantTrxnRefId;

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getBankIFSCCode() {
		return bankIFSCCode;
	}

	public void setBankIFSCCode(String bankIFSCCode) {
		this.bankIFSCCode = bankIFSCCode;
	}

	public String getBankAccountType() {
		return bankAccountType;
	}

	public void setBankAccountType(String bankAccountType) {
		this.bankAccountType = bankAccountType;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	
	public String getMerchantTrxnRefId() {
		return merchantTrxnRefId;
	}

	public void setMerchantTrxnRefId(String merchantTrxnRefId) {
		this.merchantTrxnRefId = merchantTrxnRefId;
	}

}
