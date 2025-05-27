package com.fidypay.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
//@Table(name = "RK_CORE_TRANSACTION")
@EntityListeners(AuditingEntityListener.class)
@Table(name = "RK_CORE_TRANSACTION", indexes = {
		@Index(name = "index_RK_TRANSACTION_ID", columnList = "RK_TRANSACTION_ID"),
		@Index(name = "index_RK_TRXN_REF_ID", columnList = "RK_TRXN_REF_ID"),
		@Index(name = "index_RK_TRXN_SOURCE_ID", columnList = "RK_TRXN_SOURCE_ID"),
		@Index(name = "index_RK_PRODUCT_ID", columnList = "RK_PRODUCT_ID"),
		@Index(name = "index_RK_TRXN_IDENTIFIER", columnList = "RK_TRXN_IDENTIFIER"),
		@Index(name = "index_RK_TRXN_DETAILS", columnList = "RK_TRXN_DETAILS"),
		@Index(name = "index_RK_TRXN_DATE", columnList = "RK_TRXN_DATE"),
		@Index(name = "index_RK_TRXN_AMOUNT", columnList = "RK_TRXN_AMOUNT"),
		@Index(name = "index_RK_TRXN_STATUS_ID", columnList = "RK_TRXN_STATUS_ID")
		})
public class RkCoreTransaction implements Serializable {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RK_TRANSACTION_ID")
	private long RkTransactionId;
	
	@Column(name="RK_TRXN_REF_ID", nullable = false, length = 1000)
	private String RkTrxnRefId;
	
	@Column(name="RK_TRXN_SOURCE_ID", nullable = false)
	private long RkTrxnSourceId;
	
	@Column(name="RK_PRODUCT_ID", nullable = false)
	private long RkProductId;
	
	@Column(name="RK_TRXN_IDENTIFIER", nullable = false, length = 1000)
	private String RkTrxnIdentifier;
	
	@Column(name="RK_TRXN_DETAILS", nullable = false, length = 1000)
	private String RkTrxnDetails;
	
	@Column(name="RK_TRXN_DATE", nullable = false)
	private Timestamp RkTrxnDate;
	
	@Column(name="RK_TRXN_AMOUNT", nullable = false)
	private double RkTrxnAmount;
	
	@Column(name="RK_TRXN_STATUS_ID", nullable = false)
	private long RkTrxnStatusId;
	
	public RkCoreTransaction() {
	}

	public long getRkTransactionId() {
		return RkTransactionId;
	}

	public void setRkTransactionId(long rkTransactionId) {
		RkTransactionId = rkTransactionId;
	}

	public String getRkTrxnRefId() {
		return RkTrxnRefId;
	}

	public void setRkTrxnRefId(String rkTrxnRefId) {
		RkTrxnRefId = rkTrxnRefId;
	}

	public long getRkTrxnSourceId() {
		return RkTrxnSourceId;
	}

	public void setRkTrxnSourceId(long rkTrxnSourceId) {
		RkTrxnSourceId = rkTrxnSourceId;
	}

	public long getRkProductId() {
		return RkProductId;
	}

	public void setRkProductId(long rkProductId) {
		RkProductId = rkProductId;
	}

	public String getRkTrxnIdentifier() {
		return RkTrxnIdentifier;
	}

	public void setRkTrxnIdentifier(String rkTrxnIdentifier) {
		RkTrxnIdentifier = rkTrxnIdentifier;
	}

	public String getRkTrxnDetails() {
		return RkTrxnDetails;
	}

	public void setRkTrxnDetails(String rkTrxnDetails) {
		RkTrxnDetails = rkTrxnDetails;
	}

	

	public Serializable getRkTrxnDate() {
		return RkTrxnDate;
	}

	public void setRkTrxnDate(Timestamp rkTrxnDate) {
		RkTrxnDate = rkTrxnDate;
	}

	public double getRkTrxnAmount() {
		return RkTrxnAmount;
	}

	public void setRkTrxnAmount(double rkTrxnAmount) {
		RkTrxnAmount = rkTrxnAmount;
	}

	public long getRkTrxnStatusId() {
		return RkTrxnStatusId;
	}

	public void setRkTrxnStatusId(long rkTrxnStatusId) {
		RkTrxnStatusId = rkTrxnStatusId;
	}
	
	
	
}
