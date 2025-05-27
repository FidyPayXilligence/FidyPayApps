package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SOUND_BOX_CHARGES")
public class SoundBoxCharges {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SOUND_BOX_CHARGES_ID")
	private Long soundBoxChargesId;

	@Column(name = "SOUND_BOX_SUBSCRIPTION_ID")
	private long soundBoxSubscriptionId;

	@Column(name = "START_DATE")
	private Timestamp startDate;

	@Column(name = "END_DATE")
	private Timestamp endDate;

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

	@Column(name = "IS_PAYMENT_RECEIVED")
	private String isPaymentReceived;

	@Column(name = "UTR")
	private String utr;

	@Column(name = "DATE")
	private Timestamp date;

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public Long getSoundBoxChargesId() {
		return soundBoxChargesId;
	}

	public void setSoundBoxChargesId(Long soundBoxChargesId) {
		this.soundBoxChargesId = soundBoxChargesId;
	}

	public long getSoundBoxSubscriptionId() {
		return soundBoxSubscriptionId;
	}

	public void setSoundBoxSubscriptionId(long soundBoxSubscriptionId) {
		this.soundBoxSubscriptionId = soundBoxSubscriptionId;
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

	public String getIsPaymentReceived() {
		return isPaymentReceived;
	}

	public void setIsPaymentReceived(String isPaymentReceived) {
		this.isPaymentReceived = isPaymentReceived;
	}

	public String getUtr() {
		return utr;
	}

	public void setUtr(String utr) {
		this.utr = utr;
	}

}