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
@Table(name = "OTP_VERFICATION")
@EntityListeners(AuditingEntityListener.class)
public class OTPVerification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "OTP_VERIFICATION_ID", nullable = false, unique = true)
	private long otpVerificationID;

	@Column(name = "MERCHANT_ID", nullable = false)
	private long merchantId;

	@Column(name = "CREATION_DATE", length = 44, nullable = false)
	private Timestamp creationDate;

	@Column(name = "OTP", length = 24, nullable = false)
	private String otp;

	@Column(name = "OTP_REF_ID", nullable = false)
	private String otpRefId;

	@Column(name = "BANK_ID", length = 50)
	private String bankId;

	@Column(name = "MERCHANT_BANK_IFSC", nullable = false)
	private String merchantBankIfsc;

	@Column(name = "MERCHANT_BANK_ACCOUNT_NUMBER", nullable = false)
	private String merchantBankAccountNumber;

	public OTPVerification() {
	}

	public OTPVerification(long merchantId, Timestamp creationDate, String otp, String otpRefId, String bankId,
			String merchantBankIfsc, String merchantBankAccountNumber) {
		this.merchantId = merchantId;
		this.creationDate = creationDate;
		this.otp = otp;
		this.otpRefId = otpRefId;
		this.bankId = bankId;
		this.merchantBankIfsc = merchantBankIfsc;
		this.merchantBankAccountNumber = merchantBankAccountNumber;
	}

	public long getOtpVerificationID() {
		return otpVerificationID;
	}

	public void setOtpVerificationID(long otpVerificationID) {
		this.otpVerificationID = otpVerificationID;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
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
