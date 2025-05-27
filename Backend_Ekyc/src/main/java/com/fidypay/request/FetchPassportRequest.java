package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class FetchPassportRequest {

	@Size(min = 8, max = 20, message = "file number must be between 8 to 20")
	@NotBlank(message = "fileNumber cannot be empty")
	private String fileNumber;

	@NotBlank(message = "dob cannot be empty.Please please try dd-MM-yyyy format")
	private String dob;

	public String getFileNumber() {
		return fileNumber;
	}

	public void setFileNumber(String fileNumber) {
		this.fileNumber = fileNumber;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

}
