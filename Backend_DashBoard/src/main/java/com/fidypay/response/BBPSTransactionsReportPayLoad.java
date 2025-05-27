package com.fidypay.response;

public class BBPSTransactionsReportPayLoad {

	private int sNo;

	private Long bbpsTransactionId;

	private Long merchantId;

	private String transactionStatus;

	private String transactionDate;

	private String transactionAmount;

	private String merchantTransactionRefId;

	private double  merchantServiceCharge;

	private double merchantServiceCommision;

	private String  isSettled;

	private String  isReconcile;

	private String trxnId;

	private String serviceName;

	private String trxnRefId;

	private String paymentStatus;
	private String paymentMode;
	
	private String mobile;

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

	public Long getBbpsTransactionId() {
		return bbpsTransactionId;
	}

	public void setBbpsTransactionId(Long bbpsTransactionId) {
		this.bbpsTransactionId = bbpsTransactionId;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
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

	public String getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(String transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getMerchantTransactionRefId() {
		return merchantTransactionRefId;
	}

	public void setMerchantTransactionRefId(String merchantTransactionRefId) {
		this.merchantTransactionRefId = merchantTransactionRefId;
	}

	public double getMerchantServiceCharge() {
		return merchantServiceCharge;
	}

	public void setMerchantServiceCharge(double merchantServiceCharge) {
		this.merchantServiceCharge = merchantServiceCharge;
	}

	public double getMerchantServiceCommision() {
		return merchantServiceCommision;
	}

	public void setMerchantServiceCommision(double merchantServiceCommision) {
		this.merchantServiceCommision = merchantServiceCommision;
	}

	public String getIsSettled() {
		return isSettled;
	}

	public void setIsSettled(String isSettled) {
		this.isSettled = isSettled;
	}

	public String getIsReconcile() {
		return isReconcile;
	}

	public void setIsReconcile(String isReconcile) {
		this.isReconcile = isReconcile;
	}

	public String getTrxnId() {
		return trxnId;
	}

	public void setTrxnId(String trxnId) {
		this.trxnId = trxnId;
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

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	
	
}
