package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PanToMobileRequest {

	@NotBlank(message = "name cannot be empty.")
	@Size(min = 2, max = 40, message = "name size should be 2 to 40.")
//	@Pattern(regexp = "^[A-Za-z\\s]+$", message = "name should be characters.")
	private String name;

	@NotBlank(message = "mobile cannot be empty.")
//	@Pattern(regexp = "^[6789]\\d{9}$", message = "Provided mobile number has to be strictly 10 digits long and start with one of 9, 8, 7 or 6.")
	private String mobile;

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

}
