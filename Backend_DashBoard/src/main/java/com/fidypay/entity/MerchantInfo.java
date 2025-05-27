package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MERCHANT_INFO")
public class MerchantInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MERCHANT_INFO_ID")
	private long merchantInfoId;

	@Column(name = "MERCHANT_ID", nullable = false)
	private long merchantId;

	@Column(name = "CREATION_DATE", length = 44, nullable = false)
	private Timestamp creationDate;

	@Column(name = "MERCHANT_BUSINESS_NAME", nullable = false)
	private String merchantBusinessName;

	@Column(name = "PG_REDIRECT_URL", nullable = false)
	private String pgRedirectUrl;

	@Column(name = "PG_CALLBACK_URL", nullable = false)
	private String pgCallbackUrl;

	@Column(name = "IMAGE_URL", nullable = false)
	private String imageUrl;

	@Column(name = "ENACH_REDIRECT_URL", nullable = false)
	private String eNachRedirectUrl;

	@Column(name = "ENACH_CALLBACK_URL", nullable = false)
	private String eNachCallbackUrl;

	@Column(name = "UPI_CALLBACK_URL", nullable = false)
	private String upiCallbackUrl;

	@Column(name = "PAYOUT_CALLBACK_URL", nullable = false)
	private String payoutCallbackUrl;

	@Column(name = "ECOLLECT_VALIDATE_URL", nullable = false)
	private String eCollectValidateUrl;

	@Column(name = "ECOLLECT_NOTIFY_URL", nullable = false)
	private String eCollectNotifyUrl;

	@Column(name = "BBPS_CALLBACK_URL", nullable = false)
	private String bbpsCallbackUrl;

	@Column(name = "CLIENT_ID", nullable = false)
	private String clientId;

	@Column(name = "CLIENT_SECRET", nullable = false)
	private String clientSecret;

	@Column(name = "USERNAME", nullable = false)
	private String username;

	@Column(name = "PASSWORD", nullable = false)
	private String password;

	@Column(name = "ECOLLECT_CORP_ID", nullable = false)
	private String eCollectCorpId;

	@Column(name = "BANK_ID_UPI", nullable = false)
	private String bankIdUpi;

	@Column(name = "BANK_ID_JSON", nullable = false)
	private String bankIdJson;

	@Column(name = "PARTNER_KEY_UPI", nullable = false)
	private String partnerKeyUpi;

	@Column(name = "IS_MERCHANT_ACTIVE", nullable = false)
	private Character isMerchantActive;

	@Column(name = "BULK_PAYOUT_CALLBACK_URL", nullable = false)
	private String bulkPayoutCallBackURL;

	@Column(name = "DEBIT_PRESENTATION_CALLBACK_URL", nullable = false)
	private String debitPresentationCallbackUrl;

	@Column(name = "QR_IMAGE", nullable = false)
	private String qrImage;

	@Column(name = "OTHER_INFO1", nullable = false)
	private String otherInfo1;

	@Column(name = "BBPS_PLATFORM_FEE", nullable = false)
	private double bbpsPlatformFee;

	public String getOtherInfo1() {
		return otherInfo1;
	}

	public void setOtherInfo1(String otherInfo1) {
		this.otherInfo1 = otherInfo1;
	}

	public long getMerchantInfoId() {
		return merchantInfoId;
	}

	public void setMerchantInfoId(long merchantInfoId) {
		this.merchantInfoId = merchantInfoId;
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

	public String getMerchantBusinessName() {
		return merchantBusinessName;
	}

	public void setMerchantBusinessName(String merchantBusinessName) {
		this.merchantBusinessName = merchantBusinessName;
	}

	public String getPgRedirectUrl() {
		return pgRedirectUrl;
	}

	public void setPgRedirectUrl(String pgRedirectUrl) {
		this.pgRedirectUrl = pgRedirectUrl;
	}

	public String getPgCallbackUrl() {
		return pgCallbackUrl;
	}

	public void setPgCallbackUrl(String pgCallbackUrl) {
		this.pgCallbackUrl = pgCallbackUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String geteNachRedirectUrl() {
		return eNachRedirectUrl;
	}

	public void seteNachRedirectUrl(String eNachRedirectUrl) {
		this.eNachRedirectUrl = eNachRedirectUrl;
	}

	public String geteNachCallbackUrl() {
		return eNachCallbackUrl;
	}

	public void seteNachCallbackUrl(String eNachCallbackUrl) {
		this.eNachCallbackUrl = eNachCallbackUrl;
	}

	public String getUpiCallbackUrl() {
		return upiCallbackUrl;
	}

	public void setUpiCallbackUrl(String upiCallbackUrl) {
		this.upiCallbackUrl = upiCallbackUrl;
	}

	public String getPayoutCallbackUrl() {
		return payoutCallbackUrl;
	}

	public void setPayoutCallbackUrl(String payoutCallbackUrl) {
		this.payoutCallbackUrl = payoutCallbackUrl;
	}

	public String geteCollectValidateUrl() {
		return eCollectValidateUrl;
	}

	public void seteCollectValidateUrl(String eCollectValidateUrl) {
		this.eCollectValidateUrl = eCollectValidateUrl;
	}

	public String geteCollectNotifyUrl() {
		return eCollectNotifyUrl;
	}

	public void seteCollectNotifyUrl(String eCollectNotifyUrl) {
		this.eCollectNotifyUrl = eCollectNotifyUrl;
	}

	public String getBbpsCallbackUrl() {
		return bbpsCallbackUrl;
	}

	public void setBbpsCallbackUrl(String bbpsCallbackUrl) {
		this.bbpsCallbackUrl = bbpsCallbackUrl;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String geteCollectCorpId() {
		return eCollectCorpId;
	}

	public void seteCollectCorpId(String eCollectCorpId) {
		this.eCollectCorpId = eCollectCorpId;
	}

	public String getBankIdUpi() {
		return bankIdUpi;
	}

	public void setBankIdUpi(String bankIdUpi) {
		this.bankIdUpi = bankIdUpi;
	}

	public String getBankIdJson() {
		return bankIdJson;
	}

	public void setBankIdJson(String bankIdJson) {
		this.bankIdJson = bankIdJson;
	}

	public String getPartnerKeyUpi() {
		return partnerKeyUpi;
	}

	public void setPartnerKeyUpi(String partnerKeyUpi) {
		this.partnerKeyUpi = partnerKeyUpi;
	}

	public Character getIsMerchantActive() {
		return isMerchantActive;
	}

	public void setIsMerchantActive(Character isMerchantActive) {
		this.isMerchantActive = isMerchantActive;
	}

	public String getBulkPayoutCallBackURL() {
		return bulkPayoutCallBackURL;
	}

	public void setBulkPayoutCallBackURL(String bulkPayoutCallBackURL) {
		this.bulkPayoutCallBackURL = bulkPayoutCallBackURL;
	}

	public String getDebitPresentationCallbackUrl() {
		return debitPresentationCallbackUrl;
	}

	public void setDebitPresentationCallbackUrl(String debitPresentationCallbackUrl) {
		this.debitPresentationCallbackUrl = debitPresentationCallbackUrl;
	}

	public String getQrImage() {
		return qrImage;
	}

	public void setQrImage(String qrImage) {
		this.qrImage = qrImage;
	}

	public double getBbpsPlatformFee() {
		return bbpsPlatformFee;
	}

	public void setBbpsPlatformFee(double bbpsPlatformFee) {
		this.bbpsPlatformFee = bbpsPlatformFee;
	}

}
