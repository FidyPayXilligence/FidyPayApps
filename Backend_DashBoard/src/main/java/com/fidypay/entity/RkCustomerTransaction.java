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
//@Table(name = "RK_CUSTOMER_TRANSACTION")
@EntityListeners(AuditingEntityListener.class)
@Table(name = "RK_CUSTOMER_TRANSACTION", indexes = {
		@Index(name = "index_CUST_RK_TRXN_ID", columnList = "CUST_RK_TRXN_ID"),
		@Index(name = "index_RK_TRANSACTION_ID", columnList = "RK_TRANSACTION_ID"),
		@Index(name = "index_CUSTOMER_ID", columnList = "CUSTOMER_ID")
		})
public class RkCustomerTransaction implements Serializable {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CUST_RK_TRXN_ID")
	private long CustRkTrxnId;
	
	@Column(name="RK_TRANSACTION_ID", nullable = false)
	private long RkTransactionId;
	
	@Column(name="CUSTOMER_ID", nullable = false)
	private long CustomerId;
	
	public RkCustomerTransaction() {
	}

	public long getCustRkTrxnId() {
		return CustRkTrxnId;
	}

	public void setCustRkTrxnId(long custRkTrxnId) {
		CustRkTrxnId = custRkTrxnId;
	}

	public long getRkTransactionId() {
		return RkTransactionId;
	}

	public void setRkTransactionId(long rkTransactionId) {
		RkTransactionId = rkTransactionId;
	}

	public long getCustomerId() {
		return CustomerId;
	}

	public void setCustomerId(long customerId) {
		CustomerId = customerId;
	}
	
	
	
}
