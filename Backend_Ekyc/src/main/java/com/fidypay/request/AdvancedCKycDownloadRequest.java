package com.fidypay.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class AdvancedCKycDownloadRequest {

	@Size(min = 2, max = 30, message = "Name size should be 2 to 30.")
	@Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Invalid Name, Special characters and Number not allowed.")
	@NotEmpty(message = "Name can not be empty.")
	private String name;

	@Pattern(regexp = "[0-9]{2}-[0-9]{2}-[0-9]{4}", message = "Invalid DOB, Please pass in dd-mm-yyyy format.")
	@NotEmpty(message = "DOB can not be empty.")
	private String dob;

	@Pattern(regexp = "M|F|O", message = "Invalid Gender. Valid Gender types are M(Male), F(Female), O(Other).")
	@NotEmpty(message = "Gender can not be empty.")
	private String gender;

	@Pattern(regexp = "AADHAAR|PAN|PASSPORT|VOTERID|DRIVING_LICENSE", message = "Invalid Document Type. Valid document types are AADHAAR, PAN, PASSPORT, VOTERID, DRIVING_LICENSE.")
	@NotEmpty(message = "Document Type can not be empty.")
	private String documentType;

	@NotEmpty(message = "Document Id can not be empty.")
	private String documentId;

	@Size(max = 100, message = "CallbackURL should be less than 100 characters.")
	@NotEmpty(message = "CallbackURL can not be empty, Please pass atleast ('NA')for SynchronousAdvancedCkyc and (http/https) URL for AsynchronousAdvancedCkyc.")
	private String callbackURL;

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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
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

	public String getCallbackURL() {
		return callbackURL;
	}

	public void setCallbackURL(String callbackURL) {
		this.callbackURL = callbackURL;
	}

}
