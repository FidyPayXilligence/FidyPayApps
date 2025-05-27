package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class VoterIdRequestV2 {

	@Size(min = 6, max = 15, message = "voterId must be between 6 to 15.")
	@NotBlank(message = "voterId cannot be empty")
	private String voterId;

	public String getVoterId() {
		return voterId;
	}

	public void setVoterId(String voterId) {
		this.voterId = voterId;
	}

}
