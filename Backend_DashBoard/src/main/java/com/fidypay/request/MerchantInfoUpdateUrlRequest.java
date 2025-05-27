package com.fidypay.request;

public class MerchantInfoUpdateUrlRequest {

	private long merchantId;

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
	
	private String bulkPayoutCallBackURL; 

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
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

	public String getBulkPayoutCallBackURL() {
		return bulkPayoutCallBackURL;
	}

	public void setBulkPayoutCallBackURL(String bulkPayoutCallBackURL) {
		this.bulkPayoutCallBackURL = bulkPayoutCallBackURL;
	}

	
	
}
