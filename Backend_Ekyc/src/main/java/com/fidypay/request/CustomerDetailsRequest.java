package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CustomerDetailsRequest {

	@NotBlank(message = "customerId can not be blank")
	@Size(min = 2, max = 42, message = "customerId size must be 2 to 30")
	private String customerId;

	@NotBlank(message = "customerName can not be blank")
	@Size(min = 2, max = 30, message = "customerName size must be 2 to 30")
	private String customerName;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

}
