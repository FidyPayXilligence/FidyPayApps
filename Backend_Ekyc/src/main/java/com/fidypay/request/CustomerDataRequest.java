package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CustomerDataRequest {

	@NotBlank(message = "name cannot be empty.")
	@Size(min = 2, max = 40, message = "name size should be 2 to 40.")
	@Pattern(regexp = "^[A-Za-z\\s]+$", message = "name should be characters.")
	private String name;

	@NotBlank(message = "mobile cannot be empty.")
	@Pattern(regexp = "^[6789]\\d{9}$", message = "Provided mobile number has to be strictly 10 digits long and start with one of 9, 8, 7 or 6.")
	private String mobile;

	@Pattern(regexp = "()|AADHAAR|DRIVING_LICENSE|PAN|PASSPORT|VOTERID", message = "Invalid documentType. Valid document types are: AADHAAR/DRIVING_LICENSE/PAN/PASSPORT/VOTERID.")
	private String documentType;

	private String id_value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getId_value() {
		return id_value;
	}

	public void setId_value(String id_value) {
		this.id_value = id_value;
	}

}
