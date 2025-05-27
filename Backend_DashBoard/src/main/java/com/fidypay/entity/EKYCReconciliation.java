package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EKYC_RECONCILIATION")
public class EKYCReconciliation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EKYCRECONCILIATION_ID")
	private long eKycReconciliationId;

	@Column(name = "RECONCILIATION_DATE")
	private Timestamp reconciliationDate;

	@Column(name = "FROM_DATE")
	private String fromDate;

	@Column(name = "TO_DATE")
	private String toDate;

	@Column(name = "MERCHANT_ID")
	private long merchantId;

	@Column(name = "TOTAL_TRXN")
	private long totalTrxn;

	@Column(name = "TOTAL_AMOUNT")
	private Double totalAmount;

	@Column(name = "SERVICE_PROVIDER_ID")
	private long serviceProviderId;

	@Column(name = "MERCHANT_SERVICE_ID")
	private long merchantServiceId;

	@Column(name = "SERVICE_NAME")
	private String serviceName;

	@Column(name = "RECONCILIATION_TOTAL_TRXN")
	private long reconciliationTotalTrxn;

	@Column(name = "RECONCILIATION_TOTAL_AMOUNT")
	private Double reconciliationTotalAmount;

	public long geteKycReconciliationId() {
		return eKycReconciliationId;
	}

	public void seteKycReconciliationId(long eKycReconciliationId) {
		this.eKycReconciliationId = eKycReconciliationId;
	}

	public Timestamp getReconciliationDate() {
		return reconciliationDate;
	}

	public void setReconciliationDate(Timestamp reconciliationDate) {
		this.reconciliationDate = reconciliationDate;
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

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public long getTotalTrxn() {
		return totalTrxn;
	}

	public void setTotalTrxn(long totalTrxn) {
		this.totalTrxn = totalTrxn;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public long getServiceProviderId() {
		return serviceProviderId;
	}

	public void setServiceProviderId(long serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}

	public long getMerchantServiceId() {
		return merchantServiceId;
	}

	public void setMerchantServiceId(long merchantServiceId) {
		this.merchantServiceId = merchantServiceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public long getReconciliationTotalTrxn() {
		return reconciliationTotalTrxn;
	}

	public void setReconciliationTotalTrxn(long reconciliationTotalTrxn) {
		this.reconciliationTotalTrxn = reconciliationTotalTrxn;
	}

	public Double getReconciliationTotalAmount() {
		return reconciliationTotalAmount;
	}

	public void setReconciliationTotalAmount(Double reconciliationTotalAmount) {
		this.reconciliationTotalAmount = reconciliationTotalAmount;
	}

}
