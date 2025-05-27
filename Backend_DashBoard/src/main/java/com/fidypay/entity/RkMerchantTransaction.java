package com.fidypay.entity;

import java.io.Serializable;

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
//@Table(name = "RK_MERCHANT_TRANSACTION")
@EntityListeners(AuditingEntityListener.class)
@Table(name = "RK_MERCHANT_TRANSACTION", indexes = {
		@Index(name = "index_MERCHANT_RK_TRXN_ID", columnList = "MERCHANT_RK_TRXN_ID"),
		@Index(name = "index_RK_TRANSACTION_ID", columnList = "RK_TRANSACTION_ID"),
		@Index(name = "index_MERCHANT_ID", columnList = "MERCHANT_ID"),
		@Index(name = "index_MERCHANT_ORDER_ID", columnList = "MERCHANT_ORDER_ID")
		})
public class RkMerchantTransaction implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MERCHANT_RK_TRXN_ID")
	private long MerchantRkTrxnId;

	@Column(name="RK_TRANSACTION_ID", nullable = false)
	private long RkTransactionId;
	
	@Column(name="MERCHANT_ID", nullable = false)
	private long MerchantId;
	
	@Column(name="MERCHANT_ORDER_ID", nullable = false, length = 1000)
	private String MerchantOrderId;
	
	public RkMerchantTransaction() {
	}

    

	public long getMerchantRkTrxnId() {
		return MerchantRkTrxnId;
	}



	public void setMerchantRkTrxnId(long merchantRkTrxnId) {
		MerchantRkTrxnId = merchantRkTrxnId;
	}



	public long getRkTransactionId() {
		return RkTransactionId;
	}

	public void setRkTransactionId(long rkTransactionId) {
		RkTransactionId = rkTransactionId;
	}

	public long getMerchantId() {
		return MerchantId;
	}

	public void setMerchantId(long merchantId) {
		MerchantId = merchantId;
	}

	public String getMerchantOrderId() {
		return MerchantOrderId;
	}

	public void setMerchantOrderId(String merchantOrderId) {
		MerchantOrderId = merchantOrderId;
	}
	
	
	
}
