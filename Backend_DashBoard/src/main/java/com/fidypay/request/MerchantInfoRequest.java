package com.fidypay.request;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class MerchantInfoRequest {

	@NotNull(message = "merchantId can not be empty.")
	private long merchantId;

	@Size(min = 2, max = 200, message = "merchantBussinessName size should be 2 to 200.")
	@NotEmpty(message = "merchantBussinessName can not be empty.")
	private String merchantBussinessName;

	@Size(min = 2, max = 200, message = "pgRedirectUrl size should be 2 to 200.")
	@NotEmpty(message = "pgRedirectUrl can not be empty.")
	private String pgRedirectUrl;

	@Size(min = 2, max = 200, message = "pgCallbackUrl size should be 2 to 200.")
	@NotEmpty(message = "pgCallbackUrl can not be empty.")
	private String pgCallbackUrl;

	@Size(min = 2, max = 200, message = "imageUrl size should be 2 to 200.")
	@NotEmpty(message = "imageUrl can not be empty.")
	private String imageUrl;

	@Size(min = 2, max = 200, message = "eNachRedirectUrl size should be 2 to 200.")
	@NotEmpty(message = "eNachRedirectUrl can not be empty.")
	private String eNachRedirectUrl;

	@Size(min = 2, max = 200, message = "eNachCallbackUrl size should be 2 to 200.")
	@NotEmpty(message = "eNachCallbackUrl can not be empty.")
	private String eNachCallbackUrl;

	@Size(min = 2, max = 200, message = "upiCallbackUrl size should be 2 to 200.")
	@NotEmpty(message = "upiCallbackUrl can not be empty.")
	private String upiCallbackUrl;

	@Size(min = 2, max = 200, message = "payoutCallbackUrl size should be 2 to 200.")
	@NotEmpty(message = "payoutCallbackUrl can not be empty.")
	private String payoutCallbackUrl;

	@Size(min = 2, max = 200, message = "eCollectValidateUrl size should be 2 to 200.")
	@NotEmpty(message = "eCollectValidateUrl can not be empty.")
	private String eCollectValidateUrl;

	@Size(min = 2, max = 200, message = "eCollectNotifyUrl size should be 2 to 200.")
	@NotEmpty(message = "eCollectNotifyUrl can not be empty.")
	private String eCollectNotifyUrl;

	@Size(min = 2, max = 200, message = "bbpsCallbackUrl size should be 2 to 200.")
	@NotEmpty(message = "bbpsCallbackUrl can not be empty.")
	private String bbpsCallbackUrl;

	@Size(min = 2, max = 200, message = "clientId size should be 2 to 200.")
	@NotEmpty(message = "clientId can not be empty.")
	private String clientId;

	@Size(min = 2, max = 200, message = "clientSecrate size should be 2 to 200.")
	@NotEmpty(message = "clientSecrate can not be empty.")
	private String clientSecret;

	@Size(min = 2, max = 200, message = "username size should be 2 to 200.")
	@NotEmpty(message = "username can not be empty.")
	private String username;

	@Size(min = 2, max = 200, message = "password size should be 2 to 200.")
	@NotEmpty(message = "password can not be empty.")
	private String password;

	@Size(min = 2, max = 200, message = "eCollectCorpId size should be 2 to 200.")
	@NotEmpty(message = "eCollectCorpId can not be empty.")
	private String eCollectCorpId;

	@Size(min = 2, max = 200, message = "bankIdUpi size should be 2 to 200.")
	@Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Invalid bankIdUpi, Special character and numbers not allowed.")
	@NotEmpty(message = "bankIdUpi can not be empty.")
	private String bankIdUpi;

	@Size(min = 2, max = 200, message = "bankIdJson size should be 2 to 200.")
	@Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Invalid bankIdJson, Special character and numbers not allowed.")
	@NotEmpty(message = "bankIdJson can not be empty.")
	private String bankIdJson;

	@Size(min = 2, max = 200, message = "partnerKeyUpi size should be 2 to 200.")
	@NotEmpty(message = "partnerKeyUpi can not be empty.")
	private String partnerKeyUpi;

	@Size(min = 2, max = 200, message = "bulkPayoutCallBackURL size should be 2 to 200.")
	@NotEmpty(message = "bulkPayoutCallBackURL can not be empty.")
	private String bulkPayoutCallBackURL;
	
	@Size(min = 2, max = 300, message = "debitPresentationCallbackUrl size should be 2 to 300.")
	@NotEmpty(message = "debitPresentationCallbackUrl can not be empty.")
	private String debitPresentationCallbackUrl;

	public MerchantInfoRequest() {
	}

	public MerchantInfoRequest(long merchantId, String merchantBussinessName, String pgRedirectUrl,
			String pgCallbackUrl, String imageUrl, String eNachRedirectUrl, String eNachCallbackUrl,
			String upiCallbackUrl, String payoutCallbackUrl, String eCollectValidateUrl, String eCollectNotifyUrl,
			String bbpsCallbackUrl, String clientId, String clientSecret, String username, String password,
			String eCollectCorpId, String bankIdUpi, String bankIdJson, String partnerKeyUpi,
			String bulkPayoutCallBackURL,String debitPresentationCallbackUrl) {
			this.merchantId = merchantId;
			this.merchantBussinessName = merchantBussinessName;
			this.pgRedirectUrl = pgRedirectUrl;
			this.pgCallbackUrl = pgCallbackUrl;
			this.imageUrl = imageUrl;
			this.eNachRedirectUrl = eNachRedirectUrl;
			this.eNachCallbackUrl = eNachCallbackUrl;
			this.upiCallbackUrl = upiCallbackUrl;
			this.payoutCallbackUrl = payoutCallbackUrl;
			this.eCollectValidateUrl = eCollectValidateUrl;
			this.eCollectNotifyUrl = eCollectNotifyUrl;
			this.bbpsCallbackUrl = bbpsCallbackUrl;
			this.clientId = clientId;
			this.clientSecret = clientSecret;
			this.username = username;
			this.password = password;
			this.eCollectCorpId = eCollectCorpId;
			this.bankIdUpi = bankIdUpi;
			this.bankIdJson = bankIdJson;
			this.partnerKeyUpi = partnerKeyUpi;
			this.bulkPayoutCallBackURL = bulkPayoutCallBackURL;
			this.debitPresentationCallbackUrl=debitPresentationCallbackUrl;
			}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantBussinessName() {
		return merchantBussinessName;
	}

	public void setMerchantBussinessName(String merchantBussinessName) {
		this.merchantBussinessName = merchantBussinessName;
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

}
