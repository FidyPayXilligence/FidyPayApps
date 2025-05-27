package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class SubMerchantOnboardingRequest {

	@NotBlank(message = "fullName can not be empty")
	@Size(min = 2, max = 30, message = "fullName can not be more than 30 words")
	private String fullName;

	@NotBlank(message = "businessType can not be empty")
	@Pattern(regexp = "Individual|INDIVIDUAL|Sole Proprietarship|SOLE PROPRIETARSHIP", message = "Invalid businessType Please enter INDIVIDUAL or SOLE PROPRIETARSHIP")
	private String businessType;

	private String email;

	@Pattern(regexp = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$", message = "Please enter valid phone number")
	@NotBlank(message = "phone number can not be empty")
	@Size(min = 10, max = 10, message = "Please enter your 10 digit phone number")
	private String phoneNumber;

	@NotBlank(message = "mcc code can not be empty")
	@Pattern(regexp = "\\d{4}", message = "Please enter valid mcc code")
	private String mcc;

	@NotBlank(message = "notification can not be empty")
	@Pattern(regexp = "true|false", message = "Invalid notification Please enter true or false")
	private String notification;

//	@NotBlank(message = "serialNumber can not be empty")
//	@Size(min = 5, max = 12, message = "serialNumber can not be more than 12 words")
	private String serialNumber;

	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getMcc() {
		return mcc;
	}

	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

}
