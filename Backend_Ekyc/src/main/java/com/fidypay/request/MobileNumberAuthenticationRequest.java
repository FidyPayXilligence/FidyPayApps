package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class MobileNumberAuthenticationRequest {
	
	@Pattern(regexp = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$", message = "Please enter valid mobile number")
	@NotBlank(message = "mobile number can not be empty")
	@Size(min = 10, max = 10, message = "Please enter your 10 digit mobile number")
	private String mobileNo;

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	
	
	

}
