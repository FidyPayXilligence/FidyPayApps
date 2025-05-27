package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AccountVerificationRequest {

	@NotBlank(message = "beneficiaryAccNo can not be blank")
	@Size(min = 9, max = 30, message = "beneficiaryAccNo size must be 9 to 30")
//	@Pattern(regexp =  "^((?=[A-Za-z0-9@])(?![_\\\\-]).)*$", message = "pass valid beneficiaryAccNo" )
	private String beneficiaryAccNo;

	@NotBlank(message = "beneficiaryIfscCode can not be blank")
//	@Pattern(regexp =  "^((?=[A-Za-z0-9@])(?![_\\\\-]).)*$", message = "pass valid beneficiaryIfscCode" )
	private String beneficiaryIfscCode;

	@NotBlank(message = "merchantTrxnRefId can not be blank")
	@Size(min = 1, max = 30, message = "merchantTrxnRefId size must be 1 to 30")
//	@Pattern(regexp =  "^((?=[A-Za-z0-9@])(?![_\\\\-]).)*$", message = "pass valid merchantTrxnRefId" )
	private String merchantTrxnRefId;

	public String getBeneficiaryAccNo() {
		return beneficiaryAccNo;
	}

	public void setBeneficiaryAccNo(String beneficiaryAccNo) {
		this.beneficiaryAccNo = beneficiaryAccNo;
	}

	public String getBeneficiaryIfscCode() {
		return beneficiaryIfscCode;
	}

	public void setBeneficiaryIfscCode(String beneficiaryIfscCode) {
		this.beneficiaryIfscCode = beneficiaryIfscCode;
	}

	public String getMerchantTrxnRefId() {
		return merchantTrxnRefId;
	}

	public void setMerchantTrxnRefId(String merchantTrxnRefId) {
		this.merchantTrxnRefId = merchantTrxnRefId;
	}

}
