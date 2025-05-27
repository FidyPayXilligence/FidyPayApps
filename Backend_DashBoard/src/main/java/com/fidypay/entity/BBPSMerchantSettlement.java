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
@Table(name = "BBPS_MERCHANT_SETTLEMENT")
@EntityListeners(AuditingEntityListener.class)
public class BBPSMerchantSettlement {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BBPS_MERCHANT_SETTLEMENT_ID")
	private long bbpsMerchantSettlementId;
	
	@Column(name="MERCHANT_TRXN_REF_ID", length = 200, nullable = false)
	private String merchanTrxnRefId;
	
	@Column(name="SETTLEMENT_DETAILS", length = 500, nullable = false)
	private String settlementDetails;
	
	@Column(name="MERCHANT_ID", nullable = false)
	private Long merchantId;
	
	@Column(name="SETTLEMENT_AMOUNT", nullable = false)
	private double settlementAmount;
	
	@Column(name="SETTLEMENT_DATE", nullable = false)
	private Timestamp settlementDate;
	
	@Column(name = "FROM_DATE")
	private Timestamp fromDate;

	@Column(name = "TO_DATE")
	private Timestamp toDate;

	@Column(name = "MERCHANT_SERVICE_ID")
	private Long merchantServiceId;

	public long getBbpsMerchantSettlementId() {
		return bbpsMerchantSettlementId;
	}

	public void setBbpsMerchantSettlementId(long bbpsMerchantSettlementId) {
		this.bbpsMerchantSettlementId = bbpsMerchantSettlementId;
	}

	public String getMerchanTrxnRefId() {
		return merchanTrxnRefId;
	}

	public void setMerchanTrxnRefId(String merchanTrxnRefId) {
		this.merchanTrxnRefId = merchanTrxnRefId;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
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

	public String getSettlementDetails() {
		return settlementDetails;
	}

	public void setSettlementDetails(String settlementDetails) {
		this.settlementDetails = settlementDetails;
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

	public Long getMerchantServiceId() {
		return merchantServiceId;
	}

	public void setMerchantServiceId(Long merchantServiceId) {
		this.merchantServiceId = merchantServiceId;
	}
	
	

}
