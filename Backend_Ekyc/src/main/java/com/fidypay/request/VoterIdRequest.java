package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class VoterIdRequest {

	@Size(min = 6, max = 15, message = "voterId must be between 6 to 15.")
	@NotBlank(message = "epicNumber cannot be empty")
	private String epicNumber;
	
	
	@NotBlank(message = "voterName cannot be empty")
	@Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Invalid voterName, Special character not allowed")
	private String voterName;
	
	@NotBlank(message = "state cannot be empty")
	@Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Invalid state, Special character not allowed")
	private String state;
	
	public String getEpicNumber() {
		return epicNumber;
	}
	public void setEpicNumber(String epicNumber) {
		this.epicNumber = epicNumber;
	}
	public String getVoterName() {
		return voterName;
	}
	public void setVoterName(String voterName) {
		this.voterName = voterName;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	
}
