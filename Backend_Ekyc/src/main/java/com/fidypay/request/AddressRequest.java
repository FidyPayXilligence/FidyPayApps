package com.fidypay.request;

import javax.validation.constraints.NotBlank;

public class AddressRequest {

	@NotBlank(message = "address1 cannot be empty")
	private String address1;
	
	@NotBlank(message = "address2 cannot be empty")
	private String address2;
	
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	
	
	
}
