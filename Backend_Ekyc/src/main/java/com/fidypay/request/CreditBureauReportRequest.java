package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CreditBureauReportRequest {

	@NotBlank(message = "name cannot be empty.")
	@Size(min = 2, max = 40, message = "name size should be 2 to 40.")
	@Pattern(regexp = "^[A-Za-z\\s]+$", message = "name should be characters.")
	private String name;

	@NotBlank(message = "dob cannot be empty.")
	@Pattern(regexp = "^[0-9]{2}-[0-9]{2}-[0-9]{4}$", message = "dob(Date of Birth) format is invalid. Hint:`DD-MM-YYYY`.")
	private String dob;

	@NotBlank(message = "addressType cannot be empty.")
	@Pattern(regexp = "H|O|X", message = "Invalid addressType. Allowed types are: H(Home)/ O(Office)/ X(Others).")
	private String addressType;

	@NotBlank(message = "address cannot be empty.")
	@Size(min = 2, max = 100, message = "address size should be 2 to 100.")
	private String address;

	@NotBlank(message = "pincode cannot be empty.")
	@Pattern(regexp = "^[1-9]{1}[0-9]{2}[0-9]{3}$", message = "Invalid pincode.")
	private String pincode;

	@NotBlank(message = "mobile cannot be empty.")
	@Pattern(regexp = "^[6789]\\d{9}$", message = "Provided mobile number has to be strictly 10 digits long and start with one of 9, 8, 7 or 6.")
	private String mobile;

	@NotBlank(message = "inquiryPurpose cannot be empty.")
	@Pattern(regexp = "BL|CL|CC|GL|HL|PL", message = "Invalid inquiryPurpose. Allowed types are: BL(BusinessLoan)/ CL(ConsumerLoan)/ CC(CreditCard)/ GL(GoldLoan)/ HL(HomeLoan)/ PL(PersonalLoan).")
	private String inquiryPurpose;

	@NotBlank(message = "documentType cannot be empty.")
	@Pattern(regexp = "PAN", message = "documentType should be 'PAN'.")
	private String documentType;

	@NotBlank(message = "documentId cannot be empty.")
	@Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "Invalid documentId format.Ex. ABCDE1234F")
	private String documentId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getInquiryPurpose() {
		return inquiryPurpose;
	}

	public void setInquiryPurpose(String inquiryPurpose) {
		this.inquiryPurpose = inquiryPurpose;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

}
