package com.fidypay.response;

public class SettlementPayLoad {

	private String amount;
	private String date;
	private String vpa;
	private String status;
	private String customerRefId;
	private String merchantTrxnRefId;
	private String subMerchantBussinessName;
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getVpa() {
		return vpa;
	}
	public void setVpa(String vpa) {
		this.vpa = vpa;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCustomerRefId() {
		return customerRefId;
	}
	public void setCustomerRefId(String customerRefId) {
		this.customerRefId = customerRefId;
	}
	public String getMerchantTrxnRefId() {
		return merchantTrxnRefId;
	}
	public void setMerchantTrxnRefId(String merchantTrxnRefId) {
		this.merchantTrxnRefId = merchantTrxnRefId;
	}
	public String getSubMerchantBussinessName() {
		return subMerchantBussinessName;
	}
	public void setSubMerchantBussinessName(String subMerchantBussinessName) {
		this.subMerchantBussinessName = subMerchantBussinessName;
	}
	
	

}
