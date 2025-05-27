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
@Table(name = "MERCHANT_SERVICE_CHARGES")
@EntityListeners(AuditingEntityListener.class)
public class MerchantServiceCharges {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MERCHANT_SERVICE_CHARGE_ID", nullable = false)
	private long merchantServiceChargeId;

	
	@Column(name = "MERCHANT_SERVICE_ID", nullable = false)
	private long merchantServiceId;

	@Column(name = "MERCHANT_SERVICE_CHARGE_START", nullable = false)
	private long merchantServiceChargeStart;

	@Column(name = "MERCHANT_SERVICE_CHARGE_END", nullable = false)
	private long merchantServiceChargeEnd;

	@Column(name = "MERCHANT_SERVICE_CHARGE_TYPE",length=44, nullable = false)
	private String merchantServiceChargeType;

	
	@Column(name = "IS_MERCHANT_SERVICE_CHARGE_ACTIVE", nullable = false)
	private Character isMerchnatServiceChargeActive;

	@Column(name = "MERCHANT_SERVICE_CHARGE_RATE", nullable = false)
	private double merchantServiceChargeRate;

	@Column(name = "MERCHANT_SERVICE_CHARGE_DATE", nullable = false)
	private Timestamp merchantServiceChargeDate;

	public long getMerchantServiceChargeId() {
		return merchantServiceChargeId;
	}

	public void setMerchantServiceChargeId(long merchantServiceChargeId) {
		this.merchantServiceChargeId = merchantServiceChargeId;
	}

	
    public long getMerchantServiceId() {
		return merchantServiceId;
	}

	public void setMerchantServiceId(long merchantServiceId) {
		this.merchantServiceId = merchantServiceId;
	}

	public long getMerchantServiceChargeStart() {
		return merchantServiceChargeStart;
	}

	public void setMerchantServiceChargeStart(long merchantServiceChargeStart) {
		this.merchantServiceChargeStart = merchantServiceChargeStart;
	}

	public long getMerchantServiceChargeEnd() {
		return merchantServiceChargeEnd;
	}

	public void setMerchantServiceChargeEnd(long merchantServiceChargeEnd) {
		this.merchantServiceChargeEnd = merchantServiceChargeEnd;
	}

	public String getMerchantServiceChargeType() {
		return merchantServiceChargeType;
	}

	public void setMerchantServiceChargeType(String merchantServiceChargeType) {
		this.merchantServiceChargeType = merchantServiceChargeType;
	}

	public Character getIsMerchnatServiceChargeActive() {
		return isMerchnatServiceChargeActive;
	}

	public void setIsMerchnatServiceChargeActive(Character isMerchnatServiceChargeActive) {
		this.isMerchnatServiceChargeActive = isMerchnatServiceChargeActive;
	}

	public double getMerchantServiceChargeRate() {
		return merchantServiceChargeRate;
	}

	public void setMerchantServiceChargeRate(double merchantServiceChargeRate) {
		this.merchantServiceChargeRate = merchantServiceChargeRate;
	}

	public Timestamp getMerchantServiceChargeDate() {
		return merchantServiceChargeDate;
	}

	public void setMerchantServiceChargeDate(Timestamp merchantServiceChargeDate) {
		this.merchantServiceChargeDate = merchantServiceChargeDate;
	}

	


	

	
	
}
