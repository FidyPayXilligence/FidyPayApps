package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class VerifyPassportRequestV2 {
	
	@NotBlank(message = "name cannot be empty")
	private String name;
	//@Size(message = "file number should 12 length")
	
	
	@NotBlank(message = "fileNumber cannot be empty")
	@Pattern(regexp = "^[A-PR-WYa-pr-wy][1-9]\\\\d\\\\s?\\\\d{4}[1-9]$",message = "Please enter valid fileNumber")
	private String passportNumber;
	
	@NotBlank(message = "dob cannot be empty")
    private String dob;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassportNumber() {
		return passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}
	
	

}
