package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PG_MERCHANT_INFO")
public class PGMerchantInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PG_MERCHANT_INFO_ID")
	private long pgMerchantInfoId;

	@Column(name = "DATE", nullable = false)
	private Timestamp date;

	@Column(name = "MERCHANT_ID", nullable = false)
	private long merchantId;

	@Column(name = "PG_MERCHANT_KEY", nullable = false)
	private String pgMerchantKey;

	@Column(name = "REMARK", nullable = false)
	private String remark;

	@Column(name = "ENCRYPTION_KEY", nullable = false)
	private String encryptionKey;

	@Column(name = "ADDITIONAL_INFO")
	private String additionalInfo;

	@Column(name = "API_MERCHANT_ID")
	private String apiMerchantId;

	@Column(name = "IS_ACTIVE")
	private Character isActive;

	@Column(name = "BANK_ID", length = 50)
	private String bankId;

	public long getPgMerchantInfoId() {
		return pgMerchantInfoId;
	}

	public void setPgMerchantInfoId(long pgMerchantInfoId) {
		this.pgMerchantInfoId = pgMerchantInfoId;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public String getPgMerchantKey() {
		return pgMerchantKey;
	}

	public void setPgMerchantKey(String pgMerchantKey) {
		this.pgMerchantKey = pgMerchantKey;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getApiMerchantId() {
		return apiMerchantId;
	}

	public void setApiMerchantId(String apiMerchantId) {
		this.apiMerchantId = apiMerchantId;
	}

	public Character getIsActive() {
		return isActive;
	}

	public void setIsActive(Character isActive) {
		this.isActive = isActive;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

}
