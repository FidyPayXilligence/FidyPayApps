package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "ENACH_RECONCILIATION")
public class ENachReconciliation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ENACH_RECONCILIATION_ID")
	private long ENachReconciliationId;
	
	@Column(name="MERCHANT_TRANSACTION_REF_ID")
	private String merchantTransactionRefId;

	@Column(name = "MERCHANT_ID")
	private long merchantId;

	@Column(name = "COLLECTION_AMOUNT")
	private double collectionAmount;
	
	@Column(name = "PRINCIPAL_AMOUNT")
	private double principalAmount;
	
	@Column(name = "RECONCILIATION_AMOUNT")
	private  double reconciliationAmount;
	
	@Column(name = "FROM_DATE")
	private String fromDate;
	
	@Column(name = "TO_DATE")
	private String toDate;
	
	@Column(name = "RECONCILIATION_DATE")
	private Timestamp reconciliationDate;
	
	@Column(name = "IS_VERIFIED", length = 1)
	private Character isVerified;
	
	@Column(name = "RECONCILIATION_DETAILS", length = 300)
	private String ReconciliationDetails;

	
	public double getCollectionAmount() {
		return collectionAmount;
	}

	public void setCollectionAmount(double collectionAmount) {
		this.collectionAmount = collectionAmount;
	}

	public double getPrincipalAmount() {
		return principalAmount;
	}

	public void setPrincipalAmount(double principalAmount) {
		this.principalAmount = principalAmount;
	}

	public double getReconciliationAmount() {
		return reconciliationAmount;
	}

	public void setReconciliationAmount(double reconciliationAmount) {
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

	public Timestamp getReconciliationDate() {
		return reconciliationDate;
	}

	public void setReconciliationDate(Timestamp reconciliationDate) {
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
	public long getENachReconciliationId() {
		return ENachReconciliationId;
	}

	public void setENachReconciliationId(long eNachReconciliationId) {
		ENachReconciliationId = eNachReconciliationId;
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

	
	
	
	
}
