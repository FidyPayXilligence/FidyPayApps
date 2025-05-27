package com.fidypay.response;

public class MerchantChargesDeatilsResponse {

	private long merchantId;
	private long merchantServiceId;

	private long merchantServiceChargesStart;

	private long merchantServiceChargesEnd;

	private double merchantServiceChargesRate;

	private String ServiceName;

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

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

	public long getMerchantServiceChargesStart() {
		return merchantServiceChargesStart;
	}

	public void setMerchantServiceChargesStart(long merchantServiceChargesStart) {
		this.merchantServiceChargesStart = merchantServiceChargesStart;
	}

	public long getMerchantServiceChargesEnd() {
		return merchantServiceChargesEnd;
	}

	public void setMerchantServiceChargesEnd(long merchantServiceChargesEnd) {
		this.merchantServiceChargesEnd = merchantServiceChargesEnd;
	}

	public double getMerchantServiceChargesRate() {
		return merchantServiceChargesRate;
	}

	public void setMerchantServiceChargesRate(double merchantServiceChargesRate) {
		this.merchantServiceChargesRate = merchantServiceChargesRate;
	}

	public String getServiceName() {
		return ServiceName;
	}

	public void setServiceName(String serviceName) {
		ServiceName = serviceName;
	}

}
