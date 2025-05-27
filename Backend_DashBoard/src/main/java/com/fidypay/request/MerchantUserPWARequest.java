package com.fidypay.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class MerchantUserPWARequest {

	@NotBlank(message = "merchantUserName can not be empty")
	@Pattern(regexp = "^[ A-Za-z0-9_-]*$", message = "merchantUserName should be alphanumeric and only[_-] are allowed.")
	@Size(min = 2, max = 45, message = "merchantUserName size should be 2 to 45.")
	private String merchantUserName;

	@Email(regexp = "(?:[a-zA-Z0-9_{|}~-]+(?:\\.[a-zA-Z0-9_{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-zA-Z0-9-._](?:[a-zA-Z0-9-._]*[a-zA-Z0-9-._])?\\.)+[a-zA-Z0-9-._](?:[a-zA-Z0-9-._]*[a-zA-Z0-9-._])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9--._]*[a-z0-9-._]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
	@NotBlank(message = "merchantUserEmail can not be empty")
	@Size(min = 2, max = 45, message = "merchantUserEmail size should be 2 to 45.")
	private String merchantUserEmail;

	@Pattern(regexp = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$", message = "please enter valid merchantUserMobileNo")
	@NotBlank(message = "merchantUserMobileNo can not be empty")
	@Size(min = 10, max = 10, message = "Please enter your 10 digit merchantUserMobileNo")
	private String merchantUserMobileNo;

	public String getMerchantUserName() {
		return merchantUserName;
	}

	public void setMerchantUserName(String merchantUserName) {
		this.merchantUserName = merchantUserName;
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

}
