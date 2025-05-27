package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ENACH_MERCHANT_SETTLEMENT")
public class ENachMerchantSettelment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ENACH_MERCHANT_SETTELMENT_ID")
	private long eNachMerchantSettelmentId;

	@Column(name = "IS_VERFIED")
	private Character isVerfied;

	@Column(name = "MERCHANT_TRANSACTION_REF_ID")
	private String merchantTransactionRefId;

	@Column(name = "SETTLEMENT_DETAILS", length = 500, nullable = false)
	private String settlementDetails;

	@Column(name = "MERCHANT_ID", nullable = false)
	private long merchantId;

	@Column(name = "SETTLEMENT_AMOUNT", nullable = false)
	private double settlementAmount;

	@Column(name = "SETTLEMENT_DATE", nullable = false)
	private Timestamp settlementDate;

	@Column(name = "FROM_DATE")
	private String fromDate;

	@Column(name = "TO_DATE")
	private String toDate;

	@Column(name = "MERCHANT_SERVICE_ID")
	private Long merchantServiceId;

	@Column(name = "AMOUNT")
	private double amount;

	@Column(name = "TOTAL_TRANSACTION")
	private Integer totalTransaction;

	@Column(name = "SERVICE_NAME")
	private String serviceName;

	@Column(name = "UTR")
	private String utr;

	public long geteNachMerchantSettelmentId() {
		return eNachMerchantSettelmentId;
	}

	public void seteNachMerchantSettelmentId(long eNachMerchantSettelmentId) {
		this.eNachMerchantSettelmentId = eNachMerchantSettelmentId;
	}

	public Character getIsVerfied() {
		return isVerfied;
	}

	public void setIsVerfied(Character isVerfied) {
		this.isVerfied = isVerfied;
	}

	public String getMerchantTransactionRefId() {
		return merchantTransactionRefId;
	}

	public void setMerchantTransactionRefId(String merchantTransactionRefId) {
		this.merchantTransactionRefId = merchantTransactionRefId;
	}

	public String getSettlementDetails() {
		return settlementDetails;
	}

	public void setSettlementDetails(String settlementDetails) {
		this.settlementDetails = settlementDetails;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public double getSettlementAmount() {
		return settlementAmount;
	}

	public void setSettlementAmount(double settlementAmount) {
		this.settlementAmount = settlementAmount;
	}

	public Timestamp getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(Timestamp settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String startDate) {
		this.fromDate = startDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public Long getMerchantServiceId() {
		return merchantServiceId;
	}

	public void setMerchantServiceId(Long merchantServiceId) {
		this.merchantServiceId = merchantServiceId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Integer getTotalTransaction() {
		return totalTransaction;
	}

	public void setTotalTransaction(Integer totalTransaction) {
		this.totalTransaction = totalTransaction;
	}

	public String getUtr() {
		return utr;
	}

	public void setUtr(String utr) {
		this.utr = utr;
	}

}
