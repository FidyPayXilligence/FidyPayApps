package com.fidypay.entity;

import java.sql.Timestamp;

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
@Table(name = "MERCHANT_SUB_MERCHANT_INFO_V2", indexes = {
		@Index(name = "index_SUB_MERCHANT_INFO_ID", columnList = "SUB_MERCHANT_INFO_ID"),
		@Index(name = "index_MERCHANT_ID", columnList = "MERCHANT_ID"),
		@Index(name = "index_SUB_MERCHANT_ID", columnList = "SUB_MERCHANT_ID"),
		@Index(name = "index_SUB_MERCHANT_INFO", columnList = "SUB_MERCHANT_INFO"),
		@Index(name = "index_SUB_MERCHANT_REGISTER_INFO", columnList = "SUB_MERCHANT_REGISTER_INFO"),
		@Index(name = "index_SUB_MERCHANT_ADDITIONAL_INFO", columnList = "SUB_MERCHANT_ADDITIONAL_INFO"),
		@Index(name = "index_IS_DELETED", columnList = "IS_DELETED"),
		@Index(name = "index_SUB_MERCHANT_BANK_DETAILS", columnList = "SUB_MERCHANT_BANK_DETAILS"),
		@Index(name = "index_SUB_MERCHNAT_TYPE", columnList = "SUB_MERCHNAT_TYPE"),
		@Index(name = "index_SUB_MERCHANT_QR_STRING", columnList = "SUB_MERCHANT_QR_STRING"),
		@Index(name = "index_SUB_MERCHNAT_STATUS", columnList = "SUB_MERCHNAT_STATUS"),
		@Index(name = "index_SUB_MERCHANT_KEY", columnList = "SUB_MERCHANT_KEY"),
		@Index(name = "index_SUB_MERCHANT_ACTION", columnList = "SUB_MERCHANT_ACTION"),
		@Index(name = "index_SUB_MERCHANT_DATE", columnList = "SUB_MERCHANT_DATE"),
		@Index(name = "index_SUB_MERCHANT_BUSSINESS_NAME", columnList = "SUB_MERCHANT_BUSSINESS_NAME"),
		@Index(name = "index_SUB_MERCHANT_USER_REQUEST", columnList = "SUB_MERCHANT_USER_REQUEST"),
		@Index(name = "index_SUB_MERCHANT_NAME", columnList = "SUB_MERCHANT_NAME"),
		@Index(name = "index_SUB_MERCHANT_MOBILE_NUMBER", columnList = "SUB_MERCHANT_MOBILE_NUMBER"),
		@Index(name = "index_SUB_MERCHANT_EMAIL_ID", columnList = "SUB_MERCHANT_EMAIL_ID"),
		@Index(name = "index_SUB_MERCHANT_PAN", columnList = "SUB_MERCHANT_PAN"),
		@Index(name = "index_SUB_MERCHANT_MCC", columnList = "SUB_MERCHANT_MCC"),
		@Index(name = "index_SUB_MERCHANT_EDIT_ACTION", columnList = "SUB_MERCHANT_EDIT_ACTION"),
		@Index(name = "index_SUB_MERCHANT_MODIFIED_DATE", columnList = "SUB_MERCHANT_MODIFIED_DATE") })
public class MerchantSubMerchantInfoV2 {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SUB_MERCHANT_INFO_ID")
	private long subMerchantInfoIdV2;

	@Column(name = "MERCHANT_ID", length = 18)
	private long merchantId;

	@Column(name = "SUB_MERCHANT_ID", nullable = false, length = 500)
	private String subMerchantId;

	@Column(name = "SUB_MERCHANT_INFO", nullable = false, length = 2000)
	private String subMerchantInfo;

	@Column(name = "SUB_MERCHANT_REGISTER_INFO", nullable = false, length = 3000)
	private String subMerchantRegisterInfo;

	@Column(name = "SUB_MERCHANT_ADDITIONAL_INFO", nullable = false, length = 500)
	private String subMerchantAdditionalInfo;

	@Column(name = "IS_DELETED")
	private char isDeleted;

	@Column(name = "SUB_MERCHANT_BANK_DETAILS", nullable = false, length = 1000)
	private String subMerchantBankDetails;

	@Column(name = "SUB_MERCHNAT_TYPE", nullable = false)
	private String subMerchantType;

	@Column(name = "SUB_MERCHANT_QR_STRING", nullable = false)
	private String subMerchantQRString;

	@Column(name = "SUB_MERCHNAT_STATUS", nullable = false)
	private String subMerchantStatus;

	@Column(name = "SUB_MERCHANT_KEY", nullable = false)
	private String subMerchantKey;

	@Column(name = "SUB_MERCHANT_ACTION", nullable = false)
	private String subMerchantAction;

	@Column(name = "SUB_MERCHANT_DATE", nullable = false)
	private Timestamp subMerchantDate;

	@Column(name = "SUB_MERCHANT_BUSSINESS_NAME", nullable = false)
	private String subMerchantBussinessName;

	@Column(name = "SUB_MERCHANT_USER_REQUEST", nullable = false)
	private String subMerchantUserRequest;

	@Column(name = "SUB_MERCHANT_NAME", nullable = false)
	private String subMerchantName;

	@Column(name = "SUB_MERCHANT_MOBILE_NUMBER", nullable = false)
	private String subMerchantMobileNumber;

	@Column(name = "SUB_MERCHANT_EMAIL_ID", nullable = false)
	private String subMerchantEmailId;

	@Column(name = "SUB_MERCHANT_PAN", nullable = false)
	private String subMerchantPan;

	@Column(name = "SUB_MERCHANT_MCC", nullable = false)
	private String subMerchantMCC;

	@Column(name = "SUB_MERCHANT_EDIT_ACTION", nullable = false)
	private String subMerchantEditAction;

	@Column(name = "SUB_MERCHANT_MODIFIED_DATE", nullable = false)
	private Timestamp subMerchanModifiedtDate;

	@Column(name = "SUB_MERCHANT_GST", nullable = false)
	private String subMerchantGst;

	@Column(name = "BANK_ID", nullable = false)
	private String bankId;

	@Column(name = "SOUND_BOX_TID", nullable = false)
	private String soundTId;

	@Column(name = "OTHER_DOCUMENT", nullable = false)
	private String otherDocument;

	@Column(name = "SOUND_BOX_PROVIDER", nullable = false)
	private String soundBoxProvider;

	@Column(name = "SOUND_BOX_LANGUAGE", nullable = false)
	private String soundBoxLanguage;

	public MerchantSubMerchantInfoV2() {

	}

	public MerchantSubMerchantInfoV2(long subMerchantInfoIdV2, long merchantId, String subMerchantId,
			String subMerchantInfo, String subMerchantRegisterInfo, String subMerchantAdditionalInfo, char isDeleted,
			String subMerchantBankDetails, String subMerchantType, String subMerchantQRString, String subMerchantStatus,
			String subMerchantKey, String subMerchantAction, Timestamp subMerchantDate,
			String subMerchantBussinessName) {

		this.subMerchantInfoIdV2 = subMerchantInfoIdV2;
		this.merchantId = merchantId;
		this.subMerchantId = subMerchantId;
		this.subMerchantInfo = subMerchantInfo;
		this.subMerchantRegisterInfo = subMerchantRegisterInfo;
		this.subMerchantAdditionalInfo = subMerchantAdditionalInfo;
		this.isDeleted = isDeleted;
		this.subMerchantBankDetails = subMerchantBankDetails;
		this.subMerchantType = subMerchantType;
		this.subMerchantQRString = subMerchantQRString;
		this.subMerchantStatus = subMerchantStatus;
		this.subMerchantKey = subMerchantKey;
		this.subMerchantAction = subMerchantAction;
		this.subMerchantDate = subMerchantDate;
		this.subMerchantBussinessName = subMerchantBussinessName;
	}

	public long getSubMerchantInfoIdV2() {
		return subMerchantInfoIdV2;
	}

	public void setSubMerchantInfoIdV2(long subMerchantInfoIdV2) {
		this.subMerchantInfoIdV2 = subMerchantInfoIdV2;
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

	public String getSubMerchantType() {
		return subMerchantType;
	}

	public void setSubMerchantType(String subMerchantType) {
		this.subMerchantType = subMerchantType;
	}

	public String getSubMerchantQRString() {
		return subMerchantQRString;
	}

	public void setSubMerchantQRString(String subMerchantQRString) {
		this.subMerchantQRString = subMerchantQRString;
	}

	public String getSubMerchantStatus() {
		return subMerchantStatus;
	}

	public void setSubMerchantStatus(String subMerchantStatus) {
		this.subMerchantStatus = subMerchantStatus;
	}

	public String getSubMerchantKey() {
		return subMerchantKey;
	}

	public void setSubMerchantKey(String subMerchantKey) {
		this.subMerchantKey = subMerchantKey;
	}

	public String getSubMerchantAction() {
		return subMerchantAction;
	}

	public void setSubMerchantAction(String subMerchantAction) {
		this.subMerchantAction = subMerchantAction;
	}

	public Timestamp getSubMerchantDate() {
		return subMerchantDate;
	}

	public void setSubMerchantDate(Timestamp subMerchantDate) {
		this.subMerchantDate = subMerchantDate;
	}

	public String getSubMerchantBussinessName() {
		return subMerchantBussinessName;
	}

	public void setSubMerchantBussinessName(String subMerchantBussinessName) {
		this.subMerchantBussinessName = subMerchantBussinessName;
	}

	public String getSubMerchantUserRequest() {
		return subMerchantUserRequest;
	}

	public void setSubMerchantUserRequest(String subMerchantUserRequest) {
		this.subMerchantUserRequest = subMerchantUserRequest;
	}

	public String getSubMerchantName() {
		return subMerchantName;
	}

	public void setSubMerchantName(String subMerchantName) {
		this.subMerchantName = subMerchantName;
	}

	public String getSubMerchantMobileNumber() {
		return subMerchantMobileNumber;
	}

	public void setSubMerchantMobileNumber(String subMerchantMobileNumber) {
		this.subMerchantMobileNumber = subMerchantMobileNumber;
	}

	public String getSubMerchantEmailId() {
		return subMerchantEmailId;
	}

	public void setSubMerchantEmailId(String subMerchantEmailId) {
		this.subMerchantEmailId = subMerchantEmailId;
	}

	public String getSubMerchantPan() {
		return subMerchantPan;
	}

	public void setSubMerchantPan(String subMerchantPan) {
		this.subMerchantPan = subMerchantPan;
	}

	public String getSubMerchantMCC() {
		return subMerchantMCC;
	}

	public void setSubMerchantMCC(String subMerchantMCC) {
		this.subMerchantMCC = subMerchantMCC;
	}

	public String getSubMerchantEditAction() {
		return subMerchantEditAction;
	}

	public void setSubMerchantEditAction(String subMerchantEditAction) {
		this.subMerchantEditAction = subMerchantEditAction;
	}

	public Timestamp getSubMerchanModifiedtDate() {
		return subMerchanModifiedtDate;
	}

	public void setSubMerchanModifiedtDate(Timestamp subMerchanModifiedtDate) {
		this.subMerchanModifiedtDate = subMerchanModifiedtDate;
	}

	public String getSubMerchantGst() {
		return subMerchantGst;
	}

	public void setSubMerchantGst(String subMerchantGst) {
		this.subMerchantGst = subMerchantGst;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getSoundTId() {
		return soundTId;
	}

	public void setSoundTId(String soundTId) {
		this.soundTId = soundTId;
	}

	public String getOtherDocument() {
		return otherDocument;
	}

	public void setOtherDocument(String otherDocument) {
		this.otherDocument = otherDocument;
	}

	public String getSoundBoxProvider() {
		return soundBoxProvider;
	}

	public void setSoundBoxProvider(String soundBoxProvider) {
		this.soundBoxProvider = soundBoxProvider;
	}

	public String getSoundBoxLanguage() {
		return soundBoxLanguage;
	}

	public void setSoundBoxLanguage(String soundBoxLanguage) {
		this.soundBoxLanguage = soundBoxLanguage;
	}

}
