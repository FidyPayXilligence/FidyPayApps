package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class UpdateSoundBoxSubscriptionRequest {

	private long soundBoxSubscriptionId;

	@NotBlank(message = "rentalAmount cannot be empty")
	@Pattern(regexp = "^[0-9]\\d*(\\.\\d+)?$", message = "please pass valid rentalAmount")
	private String rentalAmount;

	@NotBlank(message = "frequency cannot be empty")
	@Pattern(regexp = "Monthly||Quarterly||Half yearly||Yearly", message = "Please pass Monthly ,Quarterly , Half yearly or Yearly frequency")
	private String frequency;

	@NotBlank(message = "otc cannot be empty")
	@Pattern(regexp = "^[0-9]\\d*(\\.\\d+)?$", message = "please pass valid otc")
	private String otc;

	@NotBlank(message = "paymentMode cannot be empty")
	@Pattern(regexp = "NEFT||RTGS||FT||IMPS||UPI||CASH", message = "Please pass NEFT,RTGS,FT,IMPS,CASH or UPI paymentMode")
	private String paymentMode;

	@NotBlank(message = "utr cannot be empty")
	private String utr;

	public String getUtr() {
		return utr;
	}

	public void setUtr(String utr) {
		this.utr = utr;
	}

	private String remark;

	public long getSoundBoxSubscriptionId() {
		return soundBoxSubscriptionId;
	}

	public void setSoundBoxSubscriptionId(long soundBoxSubscriptionId) {
		this.soundBoxSubscriptionId = soundBoxSubscriptionId;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
