package com.fidypay.request;

import javax.validation.constraints.NotBlank;

public class ElectricityBillRequest {

	
	@NotBlank(message = "consumerId cannot be empty")
	private String consumerId;
	
	@NotBlank(message = "serviceProviderCode cannot be empty")
	private String serviceProviderCode;
	
	public String getConsumerId() {
		return consumerId;
	}
	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}
	public String getServiceProviderCode() {
		return serviceProviderCode;
	}
	public void setServiceProviderCode(String serviceProviderCode) {
		this.serviceProviderCode = serviceProviderCode;
	}

	
	
}
