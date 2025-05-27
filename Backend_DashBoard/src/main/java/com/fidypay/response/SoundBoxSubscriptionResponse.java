package com.fidypay.response;

public class SoundBoxSubscriptionResponse {

	private int sNo;

	private long soundBoxSubscriptionId;

	private long subMerchantInfoId;

	private long merchantId;

	private String date;

	private String soundTId;

	private String soundBoxLanguage;

	private Character isActive;

	private String rentalAmount;

	private String frequency;

	private String otc;

	private String paymentMode;

	private String deactivationDate;

	private String startDate;

	private String endDate;

	private String soundBoxProvider;

	private String remark;

	private Character isDeleted;

	public Character getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Character isDeleted) {
		this.isDeleted = isDeleted;
	}

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public Character getIsActive() {
		return isActive;
	}

	public void setIsActive(Character isActive) {
		this.isActive = isActive;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getDeactivationDate() {
		return deactivationDate;
	}

	public void setDeactivationDate(String deactivationDate) {
		this.deactivationDate = deactivationDate;
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

	public String getSoundBoxProvider() {
		return soundBoxProvider;
	}

	public void setSoundBoxProvider(String soundBoxProvider) {
		this.soundBoxProvider = soundBoxProvider;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRentalAmount() {
		return rentalAmount;
	}

	public void setRentalAmount(String rentalAmount) {
		this.rentalAmount = rentalAmount;
	}

	public String getOtc() {
		return otc;
	}

	public void setOtc(String otc) {
		this.otc = otc;
	}

}
