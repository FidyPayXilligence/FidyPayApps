package com.fidypay.response;

public class ENachReconciliationReportPayLoad {

	private int sNo;

	private String merchantTransactionRefId;

	private long merchantId;

	private String collectionAmount;

	private String principalAmount;

	private String reconciliationAmount;

	private String fromDate;

	private String toDate;

	private String reconciliationDate;

	private String isVerified;

	private String ReconciliationDetails;

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

	public String getMerchantTransactionRefId() {
		return merchantTransactionRefId;
	}

	public void setMerchantTransactionRefId(String merchantTransactionRefId) {
		this.merchantTransactionRefId = merchantTransactionRefId;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public String getCollectionAmount() {
		return collectionAmount;
	}

	public void setCollectionAmount(String collectionAmount) {
		this.collectionAmount = collectionAmount;
	}

	public String getPrincipalAmount() {
		return principalAmount;
	}

	public void setPrincipalAmount(String principalAmount) {
		this.principalAmount = principalAmount;
	}

	public String getReconciliationAmount() {
		return reconciliationAmount;
	}

	public void setReconciliationAmount(String reconciliationAmount) {
		this.reconciliationAmount = reconciliationAmount;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getReconciliationDate() {
		return reconciliationDate;
	}

	public void setReconciliationDate(String reconciliationDate) {
		this.reconciliationDate = reconciliationDate;
	}

	
	
	public String getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(String isVerified) {
		this.isVerified = isVerified;
	}

	public String getReconciliationDetails() {
		return ReconciliationDetails;
	}

	public void setReconciliationDetails(String reconciliationDetails) {
		ReconciliationDetails = reconciliationDetails;
	}

}
