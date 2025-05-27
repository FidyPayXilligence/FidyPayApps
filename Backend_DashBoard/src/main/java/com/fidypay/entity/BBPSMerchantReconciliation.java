package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "BBPS_MERCHANT_RECONCILIATION")
@EntityListeners(AuditingEntityListener.class)
public class BBPSMerchantReconciliation {

	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BBPS_MERCHANT_RECON_ID")
	private long bbpsMerchantReconId;
	
	@Column(name = "MERCHANT_ID")
	private Long merchantId;

	@Column(name = "RECONCILIATION_DATE")
	private Timestamp reconciliationDate;

	@Column(name = "RECONCILIATION_DETAILS", length = 300)
	private String reconciliationDetails;

	@Column(name = "RECON_TOTAL_AMOUNT")
	private Double reconTotalAmount;
	
	@Column(name = "RECON_SETTLEMENT_AMOUNT")
	private Double reconSettlementAmount;
	
	@Column(name = "RECON_AMOUNT_GST")
	private Double recoAmountGst;
	
	@Column(name = "RECON_AMOUNT_TDS")
	private Double recoAmountTds;

	@Column(name = "IS_VERIFIED", length = 1)
	private Character isVerified;

	@Column(name = "RECON_TOTAL_TRANSACTION_COUNT")
	private int reconTotalTransactionCount;

	@Column(name = "FROM_DATE")
	private Timestamp fromDate;

	@Column(name = "TO_DATE")
	private Timestamp toDate;
	
	@Column(name = "SERVICE_NAME", length = 300)
	private String serviceName;
	
	@Column(name = "MERCHANT_SERVICE_ID")
	private Long merchantServiceId;

	public long getBbpsMerchantReconId() {
		return bbpsMerchantReconId;
	}

	public void setBbpsMerchantReconId(long bbpsMerchantReconId) {
		this.bbpsMerchantReconId = bbpsMerchantReconId;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public Timestamp getReconciliationDate() {
		return reconciliationDate;
	}

	public void setReconciliationDate(Timestamp reconciliationDate) {
		this.reconciliationDate = reconciliationDate;
	}

	public String getReconciliationDetails() {
		return reconciliationDetails;
	}

	public void setReconciliationDetails(String reconciliationDetails) {
		this.reconciliationDetails = reconciliationDetails;
	}

	public Double getReconTotalAmount() {
		return reconTotalAmount;
	}

	public void setReconTotalAmount(Double reconTotalAmount) {
		this.reconTotalAmount = reconTotalAmount;
	}

	public Character getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Character isVerified) {
		this.isVerified = isVerified;
	}

	public int getReconTotalTransactionCount() {
		return reconTotalTransactionCount;
	}

	public void setReconTotalTransactionCount(int reconTotalTransactionCount) {
		this.reconTotalTransactionCount = reconTotalTransactionCount;
	}

	public Timestamp getFromDate() {
		return fromDate;
	}

	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}

	public Timestamp getToDate() {
		return toDate;
	}

	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Double getReconSettlementAmount() {
		return reconSettlementAmount;
	}

	public void setReconSettlementAmount(Double reconSettlementAmount) {
		this.reconSettlementAmount = reconSettlementAmount;
	}

	public Double getRecoAmountGst() {
		return recoAmountGst;
	}

	public void setRecoAmountGst(Double recoAmountGst) {
		this.recoAmountGst = recoAmountGst;
	}

	public Double getRecoAmountTds() {
		return recoAmountTds;
	}

	public void setRecoAmountTds(Double recoAmountTds) {
		this.recoAmountTds = recoAmountTds;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Long getMerchantServiceId() {
		return merchantServiceId;
	}

	public void setMerchantServiceId(Long merchantServiceId) {
		this.merchantServiceId = merchantServiceId;
	}

	
	
}
