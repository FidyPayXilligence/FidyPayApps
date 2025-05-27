package com.fidypay.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
//@Table(name = "MERCHANT_SUB_MERCHANT_INFO")
@EntityListeners(AuditingEntityListener.class)
@Table(name = "MERCHANT_SUB_MERCHANT_INFO", indexes = {
		@Index(name = "index_SUB_MERCHANT_INFO_ID", columnList = "SUB_MERCHANT_INFO_ID"),
		@Index(name = "index_MERCHANT_ID", columnList = "MERCHANT_ID"),
		@Index(name = "index_SUB_MERCHANT_ID", columnList = "SUB_MERCHANT_ID"),
		@Index(name = "index_SUB_MERCHANT_INFO", columnList = "SUB_MERCHANT_INFO"),
		@Index(name = "index_SUB_MERCHANT_REGISTER_INFO", columnList = "SUB_MERCHANT_REGISTER_INFO"),
		@Index(name = "index_SUB_MERCHANT_ADDITIONAL_INFO", columnList = "SUB_MERCHANT_ADDITIONAL_INFO"),
		@Index(name = "index_IS_DELETED", columnList = "IS_DELETED"),
		@Index(name = "index_SUB_MERCHANT_BANK_DETAILS", columnList = "SUB_MERCHANT_BANK_DETAILS")
		})
public class MerchantSubMerchantInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SUB_MERCHANT_INFO_ID")
	private long subMerchantInfoId;
	
	@Column(name="MERCHANT_ID",length=18)
	private long merchantId;
	
	@Column(name="SUB_MERCHANT_ID",nullable = false,length=500)
	private String subMerchantId;
	
	@Column(name="SUB_MERCHANT_INFO",nullable = false,length=2000)
	private String subMerchantInfo;
	
	@Column(name="SUB_MERCHANT_REGISTER_INFO",nullable = false,length=3000)
	private String subMerchantRegisterInfo;
	
	@Column(name="SUB_MERCHANT_ADDITIONAL_INFO",nullable = false,length=500)
	private String subMerchantAdditionalInfo;
	
	@Column(name="IS_DELETED")
	private char isDeleted;

	@Column(name="SUB_MERCHANT_BANK_DETAILS",nullable = false,length=1000)
	private String subMerchantBankDetails;
	
	
	public MerchantSubMerchantInfo() {
	}

	public MerchantSubMerchantInfo(long subMerchantInfoId, long merchantId, String subMerchantId,
			String subMerchantInfo, String subMerchantRegisterInfo, String subMerchantAdditionalInfo, char isDeleted,String subMerchantBankDetails) {
		super();
		this.subMerchantInfoId = subMerchantInfoId;
		this.merchantId = merchantId;
		this.subMerchantId = subMerchantId;
		this.subMerchantInfo = subMerchantInfo;
		this.subMerchantRegisterInfo = subMerchantRegisterInfo;
		this.subMerchantAdditionalInfo = subMerchantAdditionalInfo;
		this.isDeleted = isDeleted;
		this.subMerchantBankDetails = subMerchantBankDetails;
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

	public String getSubMerchantId() {
		return subMerchantId;
	}

	public void setSubMerchantId(String subMerchantId) {
		this.subMerchantId = subMerchantId;
	}

	public String getSubMerchantInfo() {
		return subMerchantInfo;
	}

	public void setSubMerchantInfo(String subMerchantInfo) {
		this.subMerchantInfo = subMerchantInfo;
	}

	public String getSubMerchantRegisterInfo() {
		return subMerchantRegisterInfo;
	}

	public void setSubMerchantRegisterInfo(String subMerchantRegisterInfo) {
		this.subMerchantRegisterInfo = subMerchantRegisterInfo;
	}

	public String getSubMerchantAdditionalInfo() {
		return subMerchantAdditionalInfo;
	}

	public void setSubMerchantAdditionalInfo(String subMerchantAdditionalInfo) {
		this.subMerchantAdditionalInfo = subMerchantAdditionalInfo;
	}

	public char getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getSubMerchantBankDetails() {
		return subMerchantBankDetails;
	}

	public void setSubMerchantBankDetails(String subMerchantBankDetails) {
		this.subMerchantBankDetails = subMerchantBankDetails;
	}
	
	
	

}
