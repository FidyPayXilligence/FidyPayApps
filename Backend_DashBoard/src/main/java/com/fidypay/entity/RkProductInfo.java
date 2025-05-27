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
//@Table(name = "RK_PRODUCT_INFO")
@EntityListeners(AuditingEntityListener.class)
@Table(name = "RK_PRODUCT_INFO", indexes = {
		@Index(name = "index_RK_PRODUCT_ID", columnList = "RK_PRODUCT_ID"),
		@Index(name = "index_RK_PRODUCT_DETAILS", columnList = "RK_PRODUCT_DETAILS"),
		@Index(name = "index_RK_PRODUCT_LIMIT_DAYS", columnList = "RK_PRODUCT_LIMIT_DAYS"),
		@Index(name = "index_RK_PRODUCT_INTEREST", columnList = "RK_PRODUCT_INTEREST"),
		@Index(name = "index_RK_PRODUCT_OVERDUE_CHARGES", columnList = "RK_PRODUCT_OVERDUE_CHARGES")
		})
public class RkProductInfo implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RK_PRODUCT_ID")
	private long RkProductId;
	
	@Column(name="RK_PRODUCT_DETAILS", nullable = false, length = 1000)
	private String RkProductDetails;
	
	@Column(name="RK_PRODUCT_LIMIT_DAYS", nullable = false, length = 1000)
	private String RkProductLimitDays;
	
	@Column(name="RK_PRODUCT_INTEREST", nullable = false)
	private float RkProductIntrest;
	
	@Column(name="RK_PRODUCT_OVERDUE_CHARGES", nullable = false)
	private float RkProductOverDueCharges;
	
	public RkProductInfo() {
	}

	public long getRkProductId() {
		return RkProductId;
	}

	public void setRkProductId(long rkProductId) {
		RkProductId = rkProductId;
	}

	public String getRkProductDetails() {
		return RkProductDetails;
	}

	public void setRkProductDetails(String rkProductDetails) {
		RkProductDetails = rkProductDetails;
	}

	public String getRkProductLimitDays() {
		return RkProductLimitDays;
	}

	public void setRkProductLimitDays(String rkProductLimitDays) {
		RkProductLimitDays = rkProductLimitDays;
	}

	public float getRkProductIntrest() {
		return RkProductIntrest;
	}

	public void setRkProductIntrest(float rkProductIntrest) {
		RkProductIntrest = rkProductIntrest;
	}

	public float getRkProductOverDueCharges() {
		return RkProductOverDueCharges;
	}

	public void setRkProductOverDueCharges(float rkProductOverDueCharges) {
		RkProductOverDueCharges = rkProductOverDueCharges;
	}
	
}
