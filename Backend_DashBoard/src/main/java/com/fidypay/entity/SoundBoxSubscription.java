package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SOUND_BOX_SUBSCRIPTION")
public class SoundBoxSubscription {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SOUND_BOX_SUBSCRIPTION_ID")
	private long soundBoxSubscriptionId;

	@Column(name = "SUB_MERCHANT_INFO_ID")
	private long subMerchantInfoId;

	@Column(name = "MERCHANT_ID")
	private long merchantId;

	@Column(name = "DATE")
	private Timestamp date;

	@Column(name = "SOUND_BOX_TID")
	private String soundTId;

	@Column(name = "SOUND_BOX_LANGUAGE")
	private String soundBoxLanguage;

	@Column(name = "IS_ACTIVE")
	private Character isActive;

	@Column(name = "RENTAL_AMOUNT")
	private Double rentalAmount;

	@Column(name = "FREQUENCY")
	private String frequency;

	@Column(name = "OTC")
	private Double otc;

	@Column(name = "PAYMENT_MODE")
	private String paymentMode;

	@Column(name = "DEACTIVATION_DATE")
	private Timestamp deactivationDate;

	@Column(name = "START_DATE")
	private Timestamp startDate;

	@Column(name = "END_DATE")
	private Timestamp endDate;

	@Column(name = "SOUND_BOX_PROVIDER")
	private String soundBoxProvider;

	@Column(name = "REMARK")
	private String remark;

	@Column(name = "UTR")
	private String utr;

	@Column(name = "IS_DELETED")
	private Character isDeleted;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public long getSoundBoxSubscriptionId() {
		return soundBoxSubscriptionId;
	}

	public void setSoundBoxSubscriptionId(long soundBoxSubscriptionId) {
		this.soundBoxSubscriptionId = soundBoxSubscriptionId;
	}

	public long getSubMerchantInfoId() {
		return subMerchantInfoId;
	}

	public void setSubMerchantInfoId(long subMerchantInfoId) {
		this.subMerchantInfoId = subMerchantInfoId;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public String getSoundTId() {
		return soundTId;
	}

	public void setSoundTId(String soundTId) {
		this.soundTId = soundTId;
	}

	public String getSoundBoxLanguage() {
		return soundBoxLanguage;
	}

	public void setSoundBoxLanguage(String soundBoxLanguage) {
		this.soundBoxLanguage = soundBoxLanguage;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public Character getIsActive() {
		return isActive;
	}

	public void setIsActive(Character isActive) {
		this.isActive = isActive;
	}

	public Double getRentalAmount() {
		return rentalAmount;
	}

	public void setRentalAmount(Double rentalAmount) {
		this.rentalAmount = rentalAmount;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public Double getOtc() {
		return otc;
	}

	public void setOtc(Double otc) {
		this.otc = otc;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public Timestamp getDeactivationDate() {
		return deactivationDate;
	}

	public void setDeactivationDate(Timestamp deactivationDate) {
		this.deactivationDate = deactivationDate;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public String getSoundBoxProvider() {
		return soundBoxProvider;
	}

	public void setSoundBoxProvider(String soundBoxProvider) {
		this.soundBoxProvider = soundBoxProvider;
	}

	public String getUtr() {
		return utr;
	}

	public void setUtr(String utr) {
		this.utr = utr;
	}

	public Character getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Character isDeleted) {
		this.isDeleted = isDeleted;
	}
}
