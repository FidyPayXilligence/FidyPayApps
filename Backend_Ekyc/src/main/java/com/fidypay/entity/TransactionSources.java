package com.fidypay.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "TRANSACTION_SOURCES")
@EntityListeners(AuditingEntityListener.class)
public class TransactionSources {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TRXN_SOURCE_ID")
    private long trxnSourceId;
	
	@Column(name="SOURCE_NAME", length = 200)
	private String sourceName;
	
	@Column(name="SOURCE_DETAILS", length = 200)
	private String sourceDetails;
	
	
	 @OneToMany(mappedBy = "transactionSources", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<CoreTransactions> coreTransactionses = new HashSet<CoreTransactions>(0);

	public TransactionSources() {
	}

	public TransactionSources(long trxnSourceId) {
		this.trxnSourceId = trxnSourceId;
	}

	public TransactionSources(long trxnSourceId, String sourceName,
			String sourceDetails, Set<CoreTransactions> coreTransactionses) {
		this.trxnSourceId = trxnSourceId;
		this.sourceName = sourceName;
		this.sourceDetails = sourceDetails;
		this.coreTransactionses = coreTransactionses;
	}

	public long getTrxnSourceId() {
		return this.trxnSourceId;
	}

	public void setTrxnSourceId(long trxnSourceId) {
		this.trxnSourceId = trxnSourceId;
	}

	public String getSourceName() {
		return this.sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getSourceDetails() {
		return this.sourceDetails;
	}

	public void setSourceDetails(String sourceDetails) {
		this.sourceDetails = sourceDetails;
	}

	public Set<CoreTransactions> getCoreTransactionses() {
		return this.coreTransactionses;
	}

	public void setCoreTransactionses(Set<CoreTransactions> coreTransactionses) {
		this.coreTransactionses = coreTransactionses;
	}

}
