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
@Table(name = "PARTNER_SERVICE_CHARGES")
@EntityListeners(AuditingEntityListener.class)
public class PartnerServiceCharges {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PARTNER_SERVICE_CHARGE_ID", nullable = false)
	private long partnerServiceChargeId;

	
	@Column(name = "PARTNER_SERVICE_ID", nullable = false)
	private long partnerServiceId;

	@Column(name = "PARTNER_SERVICE_CHARGE_START", nullable = false)
	private long partnerServiceChargeStart;

	@Column(name = "PARTNER_SERVICE_CHARGE_END", nullable = false)
	private long partnerServiceChargeEnd;

	@Column(name = "PARTNER_SERVICE_CHARGE_TYPE",length=44, nullable = false)
	private String partnerServiceChargeType;

	@Column(name = "IS_PARTNER_SERVICE_CHARGE_ACTIVE", nullable = false)
	private Character isPartnerServiceChargeActive;

	@Column(name = "PARTNER_SERVICE_CHARGE_RATE", nullable = false)
	private double partnerServiceChargeRate;

	@Column(name = "PARTNER_SERVICE_CHARGE_DATE", nullable = false)
	private Timestamp partnerServiceChargeDate;

	
	
	public long getPartnerServiceChargeId() {
		return partnerServiceChargeId;
	}

	public void setPartnerServiceChargeId(long partnerServiceChargeId) {
		this.partnerServiceChargeId = partnerServiceChargeId;
	}

	public long getPartnerServiceId() {
		return partnerServiceId;
	}

	public void setPartnerServiceId(long partnerServiceId) {
		this.partnerServiceId = partnerServiceId;
	}

	public long getPartnerServiceChargeStart() {
		return partnerServiceChargeStart;
	}

	public void setPartnerServiceChargeStart(long partnerServiceChargeStart) {
		this.partnerServiceChargeStart = partnerServiceChargeStart;
	}

	public long getPartnerServiceChargeEnd() {
		return partnerServiceChargeEnd;
	}

	public void setPartnerServiceChargeEnd(long partnerServiceChargeEnd) {
		this.partnerServiceChargeEnd = partnerServiceChargeEnd;
	}

	public String getPartnerServiceChargeType() {
		return partnerServiceChargeType;
	}

	public void setPartnerServiceChargeType(String partnerServiceChargeType) {
		this.partnerServiceChargeType = partnerServiceChargeType;
	}

	

	public Character getIsPartnerServiceChargeActive() {
		return isPartnerServiceChargeActive;
	}

	public void setIsPartnerServiceChargeActive(Character isPartnerServiceChargeActive) {
		this.isPartnerServiceChargeActive = isPartnerServiceChargeActive;
	}

	public double getPartnerServiceChargeRate() {
		return partnerServiceChargeRate;
	}

	public void setPartnerServiceChargeRate(double partnerServiceChargeRate) {
		this.partnerServiceChargeRate = partnerServiceChargeRate;
	}

	public Timestamp getPartnerServiceChargeDate() {
		return partnerServiceChargeDate;
	}

	public void setPartnerServiceChargeDate(Timestamp partnerServiceChargeDate) {
		this.partnerServiceChargeDate = partnerServiceChargeDate;
	}

	
	
	
	
	
}
