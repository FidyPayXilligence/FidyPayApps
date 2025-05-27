package com.fidypay.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "RK_MERCHANT_INFO")
@EntityListeners(AuditingEntityListener.class)
public class RkMerchantInfo implements Serializable {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RK_MERCHANT_INFO_ID")
	private long RkMerchantInfoId;
	
	@Column(name="MERCHANT_ID", nullable = false)
	private long MerchantId;
	
	@Column(name="MERCHANT_CALLBACK_URL", nullable = false, length = 1000)
	private String MerchantCallbackUrl;
	
	@Column(name="MERCHANT_CODE", nullable = false, length = 1000)
    private String MerchantCode;
	
	public RkMerchantInfo() {
	}

	public long getRkMerchantInfoId() {
		return RkMerchantInfoId;
	}

	public void setRkMerchantInfoId(long rkMerchantInfoId) {
		RkMerchantInfoId = rkMerchantInfoId;
	}

	public long getMerchantId() {
		return MerchantId;
	}

	public void setMerchantId(long merchantId) {
		MerchantId = merchantId;
	}

	public String getMerchantCallbackUrl() {
		return MerchantCallbackUrl;
	}

	public void setMerchantCallbackUrl(String merchantCallbackUrl) {
		MerchantCallbackUrl = merchantCallbackUrl;
	}

	public String getMerchantCode() {
		return MerchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		MerchantCode = merchantCode;
	}

	
}
