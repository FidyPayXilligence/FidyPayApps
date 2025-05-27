package com.fidypay.dto;

import org.json.simple.JSONObject;

public class BillPayRequestRoute {

	private JSONObject customerParams;
	private String customerMobile;
	private String billerId;
	private String amount;
	private String quickPay;
	private String splitPay;
	private String splitPayAmount;
	private String category;
	private String merchantTrxnRefId;
	private String customerName;
	private String additionalInfo;
	private String remark;
	private String paymentMode;
	private String paymentId;
	
	private String custId;
	
	
	public JSONObject getCustomerParams() {
		return customerParams;
	}
	public void setCustomerParams(JSONObject customerParams) {
		this.customerParams = customerParams;
	}
	public String getCustomerMobile() {
		return customerMobile;
	}
	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}
	public String getBillerId() {
		return billerId;
	}
	public void setBillerId(String billerId) {
		this.billerId = billerId;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getQuickPay() {
		return quickPay;
	}
	public void setQuickPay(String quickPay) {
		this.quickPay = quickPay;
	}
	public String getSplitPay() {
		return splitPay;
	}
	public void setSplitPay(String splitPay) {
		this.splitPay = splitPay;
	}
	public String getSplitPayAmount() {
		return splitPayAmount;
	}
	public void setSplitPayAmount(String splitPayAmount) {
		this.splitPayAmount = splitPayAmount;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getMerchantTrxnRefId() {
		return merchantTrxnRefId;
	}
	public void setMerchantTrxnRefId(String merchantTrxnRefId) {
		this.merchantTrxnRefId = merchantTrxnRefId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public String getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	
		
	
}
