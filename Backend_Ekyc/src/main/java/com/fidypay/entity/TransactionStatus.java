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
@Table(name = "TRANSACTION_STATUS")
@EntityListeners(AuditingEntityListener.class)
public class TransactionStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TRANSACTION_STATUS_ID")
	private long transactionStatusId;

	@Column(name = "STATUS_NAME", length = 200)
	private String statusName;

	@Column(name = "STATUS_DETAILS", length = 200)
	private String statusDetails;

	@OneToMany(mappedBy = "transactionStatus", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<CoreTransactions> coreTransactionses = new HashSet<CoreTransactions>(0);

	public TransactionStatus() {
	}

	public TransactionStatus(long transactionStatusId) {
		this.transactionStatusId = transactionStatusId;
	}

	public TransactionStatus(long transactionStatusId, String statusName, String statusDetails,
			Set<CoreTransactions> coreTransactionses) {
		this.transactionStatusId = transactionStatusId;
		this.statusName = statusName;
		this.statusDetails = statusDetails;
		this.coreTransactionses = coreTransactionses;
	}

	public long getTransactionStatusId() {
		return this.transactionStatusId;
	}

	public void setTransactionStatusId(long transactionStatusId) {
		this.transactionStatusId = transactionStatusId;
	}

	public String getStatusName() {
		return this.statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getStatusDetails() {
		return this.statusDetails;
	}

	public void setStatusDetails(String statusDetails) {
		this.statusDetails = statusDetails;
	}

	public Set<CoreTransactions> getCoreTransactionses() {
		return this.coreTransactionses;
	}

	public void setCoreTransactionses(Set<CoreTransactions> coreTransactionses) {
		this.coreTransactionses = coreTransactionses;
	}

}
