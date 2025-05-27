package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class VerifyPassportRequest {
	
	@NotBlank(message = "name cannot be empty")
	private String name;
	//@Size(message = "file number should 12 length")
	
	@NotBlank(message = "fileNumber cannot be empty")
	@Pattern(regexp = "^[a-zA-Z0-9]{15,15}$",message = "Please enter valid fileNumber")
	private String fileNumber;
	
	@NotBlank(message = "dob cannot be empty")
    private String dob;
	
//	@NotBlank(message = "fuzzy cannot be empty")
//	@Pattern(regexp = "true|false", message = "Please Pass true or false on fuzzy parameter")
//    private boolean fuzzy;

    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
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
//	public boolean isFuzzy() {
//		return fuzzy;
//	}
//	public void setFuzzy(boolean fuzzy) {
//		this.fuzzy = fuzzy;
//	}
    
    
}
