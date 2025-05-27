package com.fidypay.response;

import java.sql.Timestamp;

public class ENachReconciliationListResponse {


	private long merchantId;
	private String collectionAmount;
	private String principalAmount;
	private  String reconciliationAmount;
	private String fromDate;
	private String toDate;
	private String reconciliationDate;
	private Character isVerified;
	private String ReconciliationDetails;
	
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
	public Character getIsVerified() {
		return isVerified;
	}
	public void setIsVerified(Character isVerified) {
		this.isVerified = isVerified;
	}
	public String getReconciliationDetails() {
		return ReconciliationDetails;
	}
	public void setReconciliationDetails(String reconciliationDetails) {
		ReconciliationDetails = reconciliationDetails;
	}

	
}