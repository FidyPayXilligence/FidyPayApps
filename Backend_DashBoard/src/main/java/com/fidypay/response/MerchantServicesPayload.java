package com.fidypay.response;

public class MerchantServicesPayload implements Comparable<MerchantServicesPayload>{

	private Long serviceId;
	private Long merchantServiceId;
	private String serviceName;
	private String categoryName;
	
	public Long getServiceId() {
		return serviceId;
	}
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public Long getMerchantServiceId() {
		return merchantServiceId;
	}
	public void setMerchantServiceId(Long merchantServiceId) {
		this.merchantServiceId = merchantServiceId;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	@Override
	public int compareTo(MerchantServicesPayload a) {
		if (this.serviceName.compareTo(a.serviceName) != 0) {
			return this.serviceName.compareTo(a.serviceName);
		} else {
			return this.categoryName.compareTo(a.categoryName);
		}
	}
	
}
