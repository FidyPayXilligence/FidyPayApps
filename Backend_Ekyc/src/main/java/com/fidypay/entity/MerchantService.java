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
@Table(name = "MERCHANT_SERVICE")
@EntityListeners(AuditingEntityListener.class)
public class MerchantService {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MERCHANT_SERVICE_ID", nullable = false)
	private Long merchantServiceId;


	@Column(name = "MERCHANT_ID", nullable = false)
	private Long merchantId;

	@Column(name = "SERVICE_ID", nullable = false)
	private Long serviceId;

	
	@Column(name = "SERVICE_PROVIDER_ID", nullable = false)
	private Long serrviceProviderId;
	
	
	@Column(name = "SERVICE_TYPE", length = 200, nullable = false)
	private String serviceType;

	@Column(name = "MERCHANT_SERVICE_CREATION_DATE", nullable = false)
	private Timestamp merchantServiceCreationDate;

	@Column(name = "IS_MERCHANT_SERVICE_ACTIVE", nullable = false)
	private Character isMerchantServiceActive;

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

	public Long getMerchantServiceId() {
		return merchantServiceId;
	}

	public void setMerchantServiceId(Long merchantServiceId) {
		this.merchantServiceId = merchantServiceId;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
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

	public Timestamp getMerchantServiceCreationDate() {
		return merchantServiceCreationDate;
	}

	public void setMerchantServiceCreationDate(Timestamp merchantServiceCreationDate) {
		this.merchantServiceCreationDate = merchantServiceCreationDate;
	}

	public Character getIsMerchantServiceActive() {
		return isMerchantServiceActive;
	}

	public void setIsMerchantServiceActive(Character isMerchantServiceActive) {
		this.isMerchantServiceActive = isMerchantServiceActive;
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
