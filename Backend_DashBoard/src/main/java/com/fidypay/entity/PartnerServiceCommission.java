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
@Table(name = "PARTNER_SERVICE_COMMISSION")
@EntityListeners(AuditingEntityListener.class)
public class PartnerServiceCommission {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PARTNER_SERVICE_COMMISSION_ID", nullable = false)
	private long partnerServiceCommisionId;

	
	@Column(name = "PARTNER_SERVICE_ID", nullable = false)
	private Long partnerServiceId;

	@Column(name = "IS_PARTNER_SERVICE_COMMISSION_ACTIVE", nullable = false)
	private Character isPartnerServiceCommisionActive;

	@Column(name = "PARTNER_SERVICE_COMMISSION_START", nullable = false)
	private long partnerServiceCommissionStart;

	@Column(name = "PARTNER_SERVICE_COMMISSION_END", nullable = false)
	private long partnerServiceCommissionEnd;

	@Column(name = "PARTNER_SERVICE_COMMISSION_TYPE",length=44, nullable = false)
	private String partnerServiceCommissionType;

	@Column(name = "PARTNER_SERVICE_COMMISSION_RATE", nullable = false)
	private double partnerServiceCommissionRate;

	@Column(name = "PARTNER_SERVICE_COMMISSION_DATE", nullable = false)
	private Timestamp partnerServiceCommissionDate;

	public long getPartnerServiceCommisionId() {
		return partnerServiceCommisionId;
	}

	public void setPartnerServiceCommisionId(long partnerServiceCommisionId) {
		this.partnerServiceCommisionId = partnerServiceCommisionId;
	}

	public Long getPartnerServiceId() {
		return partnerServiceId;
	}

	public void setPartnerServiceId(Long partnerServiceId) {
		this.partnerServiceId = partnerServiceId;
	}

	public Character getIsPartnerServiceCommisionActive() {
		return isPartnerServiceCommisionActive;
	}

	public void setIsPartnerServiceCommisionActive(Character isPartnerServiceCommisionActive) {
		this.isPartnerServiceCommisionActive = isPartnerServiceCommisionActive;
	}

	public long getPartnerServiceCommissionStart() {
		return partnerServiceCommissionStart;
	}

	public void setPartnerServiceCommissionStart(long partnerServiceCommissionStart) {
		this.partnerServiceCommissionStart = partnerServiceCommissionStart;
	}

	public long getPartnerServiceCommissionEnd() {
		return partnerServiceCommissionEnd;
	}

	public void setPartnerServiceCommissionEnd(long partnerServiceCommissionEnd) {
		this.partnerServiceCommissionEnd = partnerServiceCommissionEnd;
	}

	public String getPartnerServiceCommissionType() {
		return partnerServiceCommissionType;
	}

	public void setPartnerServiceCommissionType(String partnerServiceCommissionType) {
		this.partnerServiceCommissionType = partnerServiceCommissionType;
	}

	public double getPartnerServiceCommissionRate() {
		return partnerServiceCommissionRate;
	}

	public void setPartnerServiceCommissionRate(double partnerServiceCommissionRate) {
		this.partnerServiceCommissionRate = partnerServiceCommissionRate;
	}

	public Timestamp getPartnerServiceCommissionDate() {
		return partnerServiceCommissionDate;
	}

	public void setPartnerServiceCommissionDate(Timestamp partnerServiceCommissionDate) {
		this.partnerServiceCommissionDate = partnerServiceCommissionDate;
	}

	
	
	

}
