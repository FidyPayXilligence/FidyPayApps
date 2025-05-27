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
@Table(name = "MERCHANT_SERVICE_COMMISSION")
@EntityListeners(AuditingEntityListener.class)
public class MerchantServiceCommission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MERCHANT_SERVICE_COMMISSION_ID", nullable = false)
	private long merchantServiceCommisionId;

	
	@Column(name = "MERCHANT_SERVICE_ID", nullable = false)
	private Long merchantServiceId;

	@Column(name = "IS_MERCHANT_SERVICE_COMMISSION_ACTIVE", nullable = false)
	private Character isMerchnatServiceCommisionActive;

	@Column(name = "MERCHANT_SERVICE_COMMISSION_START", nullable = false)
	private long merchantServiceCommissionStart;

	@Column(name = "MERCHANT_SERVICE_COMMISSION_END", nullable = false)
	private long merchantServiceCommissionEnd;

	@Column(name = "MERCHANT_SERVICE_COMMISSION_TYPE",length=44, nullable = false)
	private String merchantServiceCommissionType;

	@Column(name = "MERCHANT_SERVICE_COMMISSION_RATE", nullable = false)
	private double merchantServiceCommissionRate;

	@Column(name = "MERCHANT_SERVICE_COMMISSION_DATE", nullable = false)
	private Timestamp merchantServiceCommissionDate;

	public long getMerchantServiceCommisionId() {
		return merchantServiceCommisionId;
	}

	public void setMerchantServiceCommisionId(long merchantServiceCommisionId) {
		this.merchantServiceCommisionId = merchantServiceCommisionId;
	}


	public Long getMerchantServiceId() {
		return merchantServiceId;
	}

	public void setMerchantServiceId(Long merchantServiceId) {
		this.merchantServiceId = merchantServiceId;
	}

	public Character getIsMerchnatServiceCommisionActive() {
		return isMerchnatServiceCommisionActive;
	}

	public void setIsMerchnatServiceCommisionActive(Character isMerchnatServiceCommisionActive) {
		this.isMerchnatServiceCommisionActive = isMerchnatServiceCommisionActive;
	}

	public long getMerchantServiceCommissionStart() {
		return merchantServiceCommissionStart;
	}

	public void setMerchantServiceCommissionStart(long merchantServiceCommissionStart) {
		this.merchantServiceCommissionStart = merchantServiceCommissionStart;
	}

	public long getMerchantServiceCommissionEnd() {
		return merchantServiceCommissionEnd;
	}

	public void setMerchantServiceCommissionEnd(long merchantServiceCommissionEnd) {
		this.merchantServiceCommissionEnd = merchantServiceCommissionEnd;
	}

	public String getMerchantServiceCommissionType() {
		return merchantServiceCommissionType;
	}

	public void setMerchantServiceCommissionType(String merchantServiceCommissionType) {
		this.merchantServiceCommissionType = merchantServiceCommissionType;
	}

	public double getMerchantServiceCommissionRate() {
		return merchantServiceCommissionRate;
	}

	public void setMerchantServiceCommissionRate(double merchantServiceCommissionRate) {
		this.merchantServiceCommissionRate = merchantServiceCommissionRate;
	}

	public Timestamp getMerchantServiceCommissionDate() {
		return merchantServiceCommissionDate;
	}

	public void setMerchantServiceCommissionDate(Timestamp merchantServiceCommissionDate) {
		this.merchantServiceCommissionDate = merchantServiceCommissionDate;
	}


	
	
}
