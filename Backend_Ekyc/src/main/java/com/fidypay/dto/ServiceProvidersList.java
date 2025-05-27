package com.fidypay.dto;

public class ServiceProvidersList {
	
	private String serviceProvider;
	private String serviceProviderCode;
	
	
	public ServiceProvidersList(String serviceProvider, String serviceProviderCode) {
		super();
		this.serviceProvider = serviceProvider;
		this.serviceProviderCode = serviceProviderCode;
	}
	
	
	
	
	@Override
	public String toString() {
		return "ServiceProvidersList [serviceProvider=" + serviceProvider + ", serviceProviderCode="
				+ serviceProviderCode + "]";
	}




	public String getServiceProvider() {
		return serviceProvider;
	}
	public void setServiceProvider(String serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
	public String getServiceProviderCode() {
		return serviceProviderCode;
	}
	public void setServiceProviderCode(String serviceProviderCode) {
		this.serviceProviderCode = serviceProviderCode;
	}
	
	
	

}
