package com.fidypay.response;

public class MerchantCommissionDeatilsResponse {

	private long merchantId;
	private long merchantServiceId;

	private long merchantServiceCommissionStart;

	private long merchantServiceCommissionEnd;

	private double merchantServiceCommissionRate;

	private String serviceName;

	private String type;

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public long getMerchantServiceId() {
		return merchantServiceId;
	}

	public void setMerchantServiceId(long merchantServiceId) {
		this.merchantServiceId = merchantServiceId;
	}

	public long getMerchantServiceCommissionStart() {
		return merchantServiceCommissionStart;
	}

	public void setMerchantServiceCommissionStart(long merchantServiceCommissionStart) {
		this.merchantServiceCommissionStart = merchantServiceCommissionStart;
	}

	public long getMerchantServiceCommissionEnd() {
		return merchantServiceCommissionEnd;
	}

	public void setMerchantServiceCommissionEnd(long merchantServiceCommissionEnd) {
		this.merchantServiceCommissionEnd = merchantServiceCommissionEnd;
	}

	public double getMerchantServiceCommissionRate() {
		return merchantServiceCommissionRate;
	}

	public void setMerchantServiceCommissionRate(double merchantServiceCommissionRate) {
		this.merchantServiceCommissionRate = merchantServiceCommissionRate;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
