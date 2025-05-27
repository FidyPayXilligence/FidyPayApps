package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PAYIN_RESPONSE")
public class PayinResponse {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PAYIN_RESPONSE_ID", nullable = false, length = 10)
	private long payinResponseId;

	@Column(name = "MERCHANT_ID", nullable = false)
	private long merchantId;

	@Column(name = "RESPONSE_DATE", nullable = false)
	private Timestamp responseDate;

	@Column(name = "USER_RESPONSE", nullable = false)
	private String userResponse;

	@Column(name = "BANK_RESPONSE", nullable = false)
	private String bankResponse;

	@Column(name = "MERCHANT_TRANSACTION_REF_ID", nullable = false)
	private String merchantTransactionRefId;

	@Column(name = "AMOUNT", nullable = false)
	private double amount;

	@Column(name = "API", nullable = false)
	private String api;

	@Column(name = "UTR", nullable = false, length = 200)
	private String UTR;

	@Column(name = "STATUS", nullable = false, length = 200)
	private String status;

	@Column(name = "PG_MERCHANT_ID", nullable = false, length = 200)
	private String pgMerchantId;

	public PayinResponse() {

	}

	public PayinResponse(long payinResponseId, long merchantId, Timestamp responseDate, String userResponse,
			String bankResponse, String merchantTransactionRefId, double amount, String api, String uTR, String status,
			String pgMerchantId) {
		this.payinResponseId = payinResponseId;
		this.merchantId = merchantId;
		this.responseDate = responseDate;
		this.userResponse = userResponse;
		this.bankResponse = bankResponse;
		this.merchantTransactionRefId = merchantTransactionRefId;
		this.amount = amount;
		this.api = api;
		UTR = uTR;
		this.status = status;
		this.pgMerchantId = pgMerchantId;
	}

	public long getPayinResponseId() {
		return payinResponseId;
	}

	public void setPayinResponseId(long payinResponseId) {
		this.payinResponseId = payinResponseId;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public Timestamp getResponseDate() {
		return responseDate;
	}

	public void setResponseDate(Timestamp responseDate) {
		this.responseDate = responseDate;
	}

	public String getUserResponse() {
		return userResponse;
	}

	public void setUserResponse(String userResponse) {
		this.userResponse = userResponse;
	}

	public String getBankResponse() {
		return bankResponse;
	}

	public void setBankResponse(String bankResponse) {
		this.bankResponse = bankResponse;
	}

	public String getMerchantTransactionRefId() {
		return merchantTransactionRefId;
	}

	public void setMerchantTransactionRefId(String merchantTransactionRefId) {
		this.merchantTransactionRefId = merchantTransactionRefId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getUTR() {
		return UTR;
	}

	public void setUTR(String uTR) {
		UTR = uTR;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPgMerchantId() {
		return pgMerchantId;
	}

	public void setPgMerchantId(String pgMerchantId) {
		this.pgMerchantId = pgMerchantId;
	}

}
