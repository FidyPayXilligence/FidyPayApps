package com.fidypay.dto;

public class MerchantInfoResponse {

	private long merchantInfoId;

	private long merchantId;

	private String creationDate;

	private String merchantBusinessName;

	private String pgRedirectUrl;

	private String pgCallbackUrl;

	private String imageUrl;

	private String eNachRedirectUrl;

	private String eNachCallbackUrl;

	private String upiCallbackUrl;

	private String payoutCallbackUrl;

	private String eCollectValidateUrl;

	private String eCollectNotifyUrl;

	private String bbpsCallbackUrl;

	private String clientId;

	private String clientSecret;

	private String username;

	private String password;

	private String eCollectCorpId;

	private String bankIdUpi;

	private String bankIdJson;

	private String partnerKeyUpi;

	private Character isMerchantActive;

	private String bulkPayoutCallBackURL;

	private String qrImage;

	private String debitPresentationCallbackUrl;

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

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
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

}
