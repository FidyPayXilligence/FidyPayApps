package com.fidypay.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CreateOrderRequest {

	private String amount;
	private String mobile;
	private String email;
	@JsonIgnore
	private String redirectUrl;
	private String vpa;
	private String trxnNote;
	private String merchantTrxnRefId;
	@JsonIgnore
	private String imageUrl;
	private String name;
	

	@JsonIgnore
	private long merchantId;

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getVpa() {
		return vpa;
	}

	public void setVpa(String vpa) {
		this.vpa = vpa;
	}

	public String getTrxnNote() {
		return trxnNote;
	}

	public void setTrxnNote(String trxnNote) {
		this.trxnNote = trxnNote;
	}

	public String getMerchantTrxnRefId() {
		return merchantTrxnRefId;
	}

	public void setMerchantTrxnRefId(String merchantTrxnRefId) {
		this.merchantTrxnRefId = merchantTrxnRefId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
