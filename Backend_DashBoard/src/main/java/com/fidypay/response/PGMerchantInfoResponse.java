package com.fidypay.response;

public class PGMerchantInfoResponse {

	private String date;
	private String apiMerchantId;
	private String pgMerchantInfoKey;
	private Character isActive;
	private String bankId;

	public Character getIsActive() {
		return isActive;
	}

	public void setIsActive(Character isActive) {
		this.isActive = isActive;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPgMerchantInfoKey() {
		return pgMerchantInfoKey;
	}

	public void setPgMerchantInfoKey(String pgMerchantInfoKey) {
		this.pgMerchantInfoKey = pgMerchantInfoKey;
	}

	public String getApiMerchantId() {
		return apiMerchantId;
	}

	public void setApiMerchantId(String apiMerchantId) {
		this.apiMerchantId = apiMerchantId;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

}
