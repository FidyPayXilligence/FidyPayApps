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
@Table(name = "PARTNER_SERVICE")
@EntityListeners(AuditingEntityListener.class)
public class PartnerServices {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PARTNER_SERVICE_ID", nullable = false)
	private Long partnerServiceId;


	@Column(name = "PARTNER_ID", nullable = false)
	private Long partnerId;

	@Column(name = "SERVICE_ID", nullable = false)
	private Long serviceId;

	
	@Column(name = "SERVICE_PROVIDER_ID", nullable = false)
	private Long serrviceProviderId;
	
	
	@Column(name = "SERVICE_TYPE", length = 200, nullable = false)
	private String serviceType;

	@Column(name = "PARTNER_SERVICE_CREATION_DATE", nullable = false)
	private Timestamp partnerServiceCreationDate;

	@Column(name = "IS_PARTNER_SERVICE_ACTIVE", nullable = false)
	private Character isPartnerServiceActive;

	@Column(name = "SUBCRIPTION_AMOUNT", nullable = false)
	private double subscriptionAmount;

	@Column(name = "OTC", nullable = false)
	private double otc;

	@Column(name = "AMC", nullable = false)
	private double amc;

	@Column(name = "SUBCRIPTION_CYCLE", length = 24, nullable = false)
	private String subscriptionCycle;

	@Column(name = "REMARK", length = 300)
	private String remark;

	public Long getPartnerServiceId() {
		return partnerServiceId;
	}

	public void setPartnerServiceId(Long partnerServiceId) {
		this.partnerServiceId = partnerServiceId;
	}

	public Long getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(Long partnerId) {
		this.partnerId = partnerId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public Long getSerrviceProviderId() {
		return serrviceProviderId;
	}

	public void setSerrviceProviderId(Long serrviceProviderId) {
		this.serrviceProviderId = serrviceProviderId;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public Timestamp getPartnerServiceCreationDate() {
		return partnerServiceCreationDate;
	}

	public void setPartnerServiceCreationDate(Timestamp partnerServiceCreationDate) {
		this.partnerServiceCreationDate = partnerServiceCreationDate;
	}

	public Character getIsPartnerServiceActive() {
		return isPartnerServiceActive;
	}

	public void setIsPartnerServiceActive(Character isPartnerServiceActive) {
		this.isPartnerServiceActive = isPartnerServiceActive;
	}

	public double getSubscriptionAmount() {
		return subscriptionAmount;
	}

	public void setSubscriptionAmount(double subscriptionAmount) {
		this.subscriptionAmount = subscriptionAmount;
	}

	public double getOtc() {
		return otc;
	}

	public void setOtc(double otc) {
		this.otc = otc;
	}

	public double getAmc() {
		return amc;
	}

	public void setAmc(double amc) {
		this.amc = amc;
	}

	public String getSubscriptionCycle() {
		return subscriptionCycle;
	}

	public void setSubscriptionCycle(String subscriptionCycle) {
		this.subscriptionCycle = subscriptionCycle;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	

	
	

}
