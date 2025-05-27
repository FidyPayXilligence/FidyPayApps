package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class MerchantInfoUpdateRequest {

	private String pgRedirectUrl;

	private String pgCallbackUrl;

	private String eNachRedirectUrl;

	private String eNachCallbackUrl;

	private String upiCallbackUrl;

	private String payoutCallbackUrl;

	private String eCollectValidateUrl;

	private String eCollectNotifyUrl;

	private String bbpsCallbackUrl;

	private String eCollectCorpId;

	private String bankIdUpi;

	private String bankIdJson;

	private String partnerKeyUpi;

	private String bulkPayoutCallBackURL;

	private String debitPresentationCallbackUrl;

	public String getDebitPresentationCallbackUrl() {
		return debitPresentationCallbackUrl;
	}

	public void setDebitPresentationCallbackUrl(String debitPresentationCallbackUrl) {
		this.debitPresentationCallbackUrl = debitPresentationCallbackUrl;
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

}
