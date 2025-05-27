package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PAYIN_REQUEST")
public class PayinRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PAYIN_REQUEST_ID", nullable = false, length = 10)
	private long payinRequestId;

	@Column(name = "MERCHANT_ID", nullable = false)
	private long merchantId;

	@Column(name = "REQUEST_DATE", nullable = false)
	private Timestamp requestDate;

	@Column(name = "USER_REQUEST", nullable = false)
	private String userRequest;

	@Column(name = "BANK_REQUEST", nullable = false)
	private String bankRequest;

	@Column(name = "MERCHANT_TRANSACTION_REF_ID", nullable = false)
	private String merchantTransactionRefId;

	@Column(name = "AMOUNT", nullable = false)
	private double amount;

	@Column(name = "PAYER_VPA", nullable = false)
	private String payerVPA;

	@Column(name = "PAYEE_VPA", nullable = false)
	private String payeeVPA;

	@Column(name = "API", nullable = false)
	private String api;

	@Column(name = "PG_MERCHANT_ID", nullable = false, length = 200)
	private String pgMerchantId;

	public PayinRequest() {

	}

	public PayinRequest(long payinRequestId, long merchantId, Timestamp requestDate, String userRequest,
			String bankRequest, String merchantTransactionRefId, double amount, String payerVPA, String payeeVPA,
			String api, String pgMerchantId) {

		this.payinRequestId = payinRequestId;
		this.merchantId = merchantId;
		this.requestDate = requestDate;
		this.userRequest = userRequest;
		this.bankRequest = bankRequest;
		this.merchantTransactionRefId = merchantTransactionRefId;
		this.amount = amount;
		this.payerVPA = payerVPA;
		this.payeeVPA = payeeVPA;
		this.api = api;
		this.pgMerchantId = pgMerchantId;
	}

	public long getPayinRequestId() {
		return payinRequestId;
	}

	public void setPayinRequestId(long payinRequestId) {
		this.payinRequestId = payinRequestId;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public Timestamp getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Timestamp requestDate) {
		this.requestDate = requestDate;
	}

	public String getUserRequest() {
		return userRequest;
	}

	public void setUserRequest(String userRequest) {
		this.userRequest = userRequest;
	}

	public String getBankRequest() {
		return bankRequest;
	}

	public void setBankRequest(String bankRequest) {
		this.bankRequest = bankRequest;
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

	public String getPayerVPA() {
		return payerVPA;
	}

	public void setPayerVPA(String payerVPA) {
		this.payerVPA = payerVPA;
	}

	public String getPayeeVPA() {
		return payeeVPA;
	}

	public void setPayeeVPA(String payeeVPA) {
		this.payeeVPA = payeeVPA;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getPgMerchantId() {
		return pgMerchantId;
	}

	public void setPgMerchantId(String pgMerchantId) {
		this.pgMerchantId = pgMerchantId;
	}

}
