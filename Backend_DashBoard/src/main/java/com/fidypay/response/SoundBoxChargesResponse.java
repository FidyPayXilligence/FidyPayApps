package com.fidypay.response;

public class SoundBoxChargesResponse {

	private int sNo;

	private Long soundBoxChargesId;

	private long soundBoxSubscriptionId;

	private String startDate;

	private String endDate;

	private Character isActive;

	private String rentalAmount;

	private String frequency;

	private String otc;

	private String paymentMode;

	private String isPaymentReceived;

	private String utr;

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
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

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Character getIsActive() {
		return isActive;
	}

	public void setIsActive(Character isActive) {
		this.isActive = isActive;
	}

	public String getRentalAmount() {
		return rentalAmount;
	}

	public void setRentalAmount(String rentalAmount) {
		this.rentalAmount = rentalAmount;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getOtc() {
		return otc;
	}

	public void setOtc(String otc) {
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