package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CibilScoreValidateRequest {
	@NotBlank(message = "firstName can not be blank")
	@Size(min = 1, max = 30, message = "Please enter firstName between 1 to 30 alphabets.")
	@Pattern(regexp = "^[a-zA-Z\\s]+$", message = "firstName should be in alphabets.")
	private String firstName;

	@NotBlank(message = "lastName can not be blank")
	@Size(min = 1, max = 30, message = "Please enter lastName between 1 to 30 alphabets.")
	@Pattern(regexp = "^[a-zA-Z\\s]+$", message = "lastName should be in alphabets.")
	private String lastName;

	@NotBlank(message = "phoneNumber can not be blank")
	@Pattern(regexp = "\\d{10}", message = "Please pass valid 10 digit phoneNumber.")
	private String phoneNumber;

//	@NotBlank(message = "panNumber can not be blank")
//	@Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "Please pass valid panNumber.")
//	private String panNumber;

	@NotBlank(message = "dob can not be blank")
	@Pattern(regexp = "([0-9]{4})-([0-9]{2})-([0-9]{2})", message = "Invalid Date Of Birth(dob) format. The format should be yyyy-mm-dd.")
	private String dob;

	@NotBlank(message = "otp can not be blank")
	@Pattern(regexp = "[0-9]{4}", message = "Please pass valid 4 digit otp.")
	private String otp;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

//	public String getPanNumber() {
//		return panNumber;
//	}
//
//	public void setPanNumber(String panNumber) {
//		this.panNumber = panNumber;
//	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

}
