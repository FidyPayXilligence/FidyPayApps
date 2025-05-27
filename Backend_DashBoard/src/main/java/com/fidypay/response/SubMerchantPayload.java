package com.fidypay.response;

public class SubMerchantPayload implements Comparable<SubMerchantPayload> {

	private String subMerchantVpa;
	private String bussinessName;

	public String getSubMerchantVpa() {
		return subMerchantVpa;
	}

	public void setSubMerchantVpa(String subMerchantVpa) {
		this.subMerchantVpa = subMerchantVpa;
	}

	public String getBussinessName() {
		return bussinessName;
	}

	public void setBussinessName(String bussinessName) {
		this.bussinessName = bussinessName;
	}

	@Override
	public int compareTo(SubMerchantPayload a) {
		if (this.bussinessName.compareTo(a.bussinessName) != 0) {
			return this.bussinessName.compareTo(a.bussinessName);
		} else {
			return this.subMerchantVpa.compareTo(a.subMerchantVpa);
		}
	}

}
