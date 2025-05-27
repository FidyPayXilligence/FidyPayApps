package com.fidypay.response;

public class OTPVerificationResponse {

	private int sNO;

	private long merchantId;

	private String creationDate;

	private String otp;

	private String otpRefId;

	private String bankId;

	private String merchantBankIfsc;

	private String merchantBankAccountNumber;

	public int getsNO() {
		return sNO;
	}

	public void setsNO(int sNO) {
		this.sNO = sNO;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getOtpRefId() {
		return otpRefId;
	}

	public void setOtpRefId(String otpRefId) {
		this.otpRefId = otpRefId;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getMerchantBankIfsc() {
		return merchantBankIfsc;
	}

	public void setMerchantBankIfsc(String merchantBankIfsc) {
		this.merchantBankIfsc = merchantBankIfsc;
	}

	public String getMerchantBankAccountNumber() {
		return merchantBankAccountNumber;
	}

	public void setMerchantBankAccountNumber(String merchantBankAccountNumber) {
		this.merchantBankAccountNumber = merchantBankAccountNumber;
	}
	
	

}
