package com.fidypay.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "WALLET_TRXN_TYPE")
@EntityListeners(AuditingEntityListener.class)
public class WalletTrxnType {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "WALLET_TRXN_TYPE_ID")
	private long walletTrxnTypeId;
	
	@Column(name="TRANSACTION_TYPE_NAME", length = 20)
	private String transactionTypeName;
	
	@Column(name="TRANSACTION_TYPE_DETAILS", length = 20)
	private String transactionTypeDetails;

	public WalletTrxnType() {
	}

	public WalletTrxnType(long walletTrxnTypeId) {
		this.walletTrxnTypeId = walletTrxnTypeId;
	}

	public WalletTrxnType(long walletTrxnTypeId,
			String transactionTypeName, String transactionTypeDetails) {
		this.walletTrxnTypeId = walletTrxnTypeId;
		this.transactionTypeName = transactionTypeName;
		this.transactionTypeDetails = transactionTypeDetails;
	}

	public long getWalletTrxnTypeId() {
		return this.walletTrxnTypeId;
	}

	public void setWalletTrxnTypeId(long walletTrxnTypeId) {
		this.walletTrxnTypeId = walletTrxnTypeId;
	}

	public String getTransactionTypeName() {
		return this.transactionTypeName;
	}

	public void setTransactionTypeName(String transactionTypeName) {
		this.transactionTypeName = transactionTypeName;
	}

	public String getTransactionTypeDetails() {
		return this.transactionTypeDetails;
	}

	public void setTransactionTypeDetails(String transactionTypeDetails) {
		this.transactionTypeDetails = transactionTypeDetails;
	}

}
