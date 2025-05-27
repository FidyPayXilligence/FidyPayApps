package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SearchByDLRequest {
	
	@Size(min = 10, max = 20, message = "dlNumber must be between 10 to 20.")
	@NotBlank(message = "dlNumber cannot be empty")
	private String dlNumber;
	
	@NotBlank(message = "dob cannot be empty")
	private String dob;
	
	
	public String getDlNumber() {
		return dlNumber;
	}
	public void setDlNumber(String dlNumber) {
		this.dlNumber = dlNumber;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}

	
	
}
