package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class TANRequest {

	@NotBlank(message = "TAN Number can not be blank")
	@Pattern(regexp = "^(?=[A-Za-z0-9]*\\d[A-Za-z0-9]*$)[A-Za-z0-9]{10}$", message = "Please Pass valid TAN Number")
	private String tanNumber;

	@NotBlank(message = "companyName can not be blank")
	@Size(min = 1, max = 60, message = "companyName size must be 1 to 60")
	private String companyName;

	public String getTanNumber() {
		return tanNumber;
	}

	public void setTanNumber(String tanNumber) {
		this.tanNumber = tanNumber;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}
