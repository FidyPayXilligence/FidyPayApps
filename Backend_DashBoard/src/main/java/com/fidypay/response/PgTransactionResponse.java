package com.fidypay.response;

public class PgTransactionResponse {

	private int sNo;
	private String transactionStatus;
	private String transactionDate;
	private String merchantTransactionRefId;
	private String transactionAmount;
	private String bankRefID;
	private String serviceName;
	private String trxnRefId;
	private String paymentMode;
	private String paymentId;
	private String  isReconcile;
	private String  isSettled;
	private double charges;

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getMerchantTransactionRefId() {
		return merchantTransactionRefId;
	}

	public void setMerchantTransactionRefId(String merchantTransactionRefId) {
		this.merchantTransactionRefId = merchantTransactionRefId;
	}

	public String getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(String transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getBankRefID() {
		return bankRefID;
	}

	public void setBankRefID(String bankRefID) {
		this.bankRefID = bankRefID;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getTrxnRefId() {
		return trxnRefId;
	}

	public void setTrxnRefId(String trxnRefId) {
		this.trxnRefId = trxnRefId;
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

	public String getIsSettled() {
		return isSettled;
	}

	public void setIsSettled(String isSettled) {
		this.isSettled = isSettled;
	}

	
}
