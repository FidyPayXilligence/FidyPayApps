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
//@Table(name = "RK_CUSTOMER_INFO")
@EntityListeners(AuditingEntityListener.class)
@Table(name = "RK_CUSTOMER_INFO", indexes = {
		@Index(name = "index_RkCustInfoId", columnList = "RkCustInfoId"),
		@Index(name = "index_CUST_RK_LIMIT", columnList = "CUST_RK_LIMIT"),
		@Index(name = "index_RK_PRODUCT_ID", columnList = "RK_PRODUCT_ID")
		})
public class RkCustomerInfo implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RkCustInfoId")
	private long RkCustInfoId;
	
	@Column(name="CUST_RK_LIMIT", nullable = false)
	private float CustRkLimit;
	
	@Column(name="RK_PRODUCT_ID", nullable = false)
	private long RkProductId;
	
	public RkCustomerInfo() {
	}

	public long getRkCustInfoId() {
		return RkCustInfoId;
	}

	public void setRkCustInfoId(long rkCustInfoId) {
		RkCustInfoId = rkCustInfoId;
	}

	public float getCustRkLimit() {
		return CustRkLimit;
	}

	public void setCustRkLimit(float custRkLimit) {
		CustRkLimit = custRkLimit;
	}

	public long getRkProductId() {
		return RkProductId;
	}

	public void setRkProductId(long rkProductId) {
		RkProductId = rkProductId;
	}
	
	
	
}
