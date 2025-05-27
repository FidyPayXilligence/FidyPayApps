package com.fidypay.response;

public class EKYCTransactionResponse {

	
    private int sNo;
	private Long merchantId;
	private String merchantName;
	private String transactionDate;
	private String status;
	private String serviceName;
	private String merchantTransactionRefId;
	private String trxnRefId;
	private String ekycId;
	private String isReconcile;
	private double charges;
	
	
	public int getsNo() {
		return sNo;
	}
	public void setsNo(int sNo) {
		this.sNo = sNo;
	}
	public Long getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getMerchantTransactionRefId() {
		return merchantTransactionRefId;
	}
	public void setMerchantTransactionRefId(String merchantTransactionRefId) {
		this.merchantTransactionRefId = merchantTransactionRefId;
	}
	public String getTrxnRefId() {
		return trxnRefId;
	}
	public void setTrxnRefId(String trxnRefId) {
		this.trxnRefId = trxnRefId;
	}
	public String getEkycId() {
		return ekycId;
	}
	public void setEkycId(String ekycId) {
		this.ekycId = ekycId;
	}
	
	public double getCharges() {
		return charges;
	}
	public void setCharges(double charges) {
		this.charges = charges;
	}
	public String getIsReconcile() {
		return isReconcile;
	}
	public void setIsReconcile(String isReconcile) {
		this.isReconcile = isReconcile;
	}
	
	
	
	
	
	
}
