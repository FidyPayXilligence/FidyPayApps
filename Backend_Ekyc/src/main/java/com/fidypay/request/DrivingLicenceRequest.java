package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class DrivingLicenceRequest {

	@Size(min = 10, max = 20, message = "dlNumber must be between 10 to 20.")
	@NotBlank(message = "dlNumber cannot be empty")
	private String dlNumber;
	
	@NotBlank(message = "dob cannot be empty")
	private String dob;
	
	@NotBlank(message = "issueDate cannot be empty")
	private String issueDate;
	
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
	public String getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}
	
	
	
}
