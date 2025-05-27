package com.fidypay.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BBPS_TRANSACTION_DETAILS")
public class BBPSTrxnDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BBPS_TRANSACTION_ID")
	private Long bbpsTransactionId;

	@Column(name = "REQUEST_ID")
	private Long requestId;

	@Column(name = "RESPONSE_ID")
	private Long responseId;

	@Column(name = "MERCHANT_ID")
	private Long merchantId;

	@Column(name = "TRANSACTION_STATUS", nullable = false, length = 100)
	private String transactionStatus;

	@Column(name = "TRANSACTION_DATE", nullable = false)
	private Date transactionDate;

	@Column(name = "TRANSACTION_AMOUNT", nullable = false)
	private double transactionAmount = 0.0;

	@Column(name = "MERCHANT_TRANSACTION_REF_ID", nullable = false, length = 300)
	private String merchantTransactionRefId;

	@Column(name = "MERCHANT_SERVICE_CHARGE", nullable = false)
	private double merchantServiceCharge;

	@Column(name = "MERCHANT_SERVICE_COMMISION", nullable = false)
	private double merchantServiceCommision;

	@Column(name = "IS_SETTLED", nullable = false)
	private char isSettled;

	@Column(name = "IS_RECONCILE", nullable = false)
	private char isReconcile;

	@Column(name = "TRXN_ID", nullable = false, length = 200)
	private String trxnId;

	@Column(name = "SERVICE_NAME", nullable = false, length = 200)
	private String serviceName;

	@Column(name = "RESPONSE_MESSAGE", nullable = false, length = 200)
	private String responseMessage;

	@Column(name = "PAYMENT_MODE", nullable = false, length = 200)
	private String paymentMode;

	@Column(name = "PAYMENT_ID", nullable = false, length = 200)
	private String paymentId;

	@Column(name = "PAYMENT_STATUS", nullable = false, length = 200)
	private String paymentStatus;

	@Column(name = "MERCHANT_SERVICE_ID", nullable = false)
	private long merchantServiceId;

	@Column(name = "TRANSACTION_STATUS_ID")
	private long transactionStatusId;

	@Column(name = "TRANSACTION_REF_ID", nullable = false, length = 300)
	private String transactionRefId;

	@Column(name = "BANK_ID", nullable = false)
	private String bankId = "NA";

	@Column(name = "TRXN_IDENTIFIER", nullable = false)
	private String trxnIdentifier = "NA";

	@Column(name = "MERCHANT_USER_ID")
	private long merchantUserId = 0;

	@Column(name = "BBPS_PLATFORM_FEE", nullable = false)
	private double bbpsPlatformFee = 0.0;

	public long getMerchantUserId() {
		return merchantUserId;
	}

	public void setMerchantUserId(long merchantUserId) {
		this.merchantUserId = merchantUserId;
	}

	public Long getBbpsTransactionId() {
		return bbpsTransactionId;
	}

	public void setBbpsTransactionId(Long bbpsTransactionId) {
		this.bbpsTransactionId = bbpsTransactionId;
	}

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public Long getResponseId() {
		return responseId;
	}

	public void setResponseId(Long responseId) {
		this.responseId = responseId;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public double getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getMerchantTransactionRefId() {
		return merchantTransactionRefId;
	}

	public void setMerchantTransactionRefId(String merchantTransactionRefId) {
		this.merchantTransactionRefId = merchantTransactionRefId;
	}

	public double getMerchantServiceCharge() {
		return merchantServiceCharge;
	}

	public void setMerchantServiceCharge(double merchantServiceCharge) {
		this.merchantServiceCharge = merchantServiceCharge;
	}

	public double getMerchantServiceCommision() {
		return merchantServiceCommision;
	}

	public void setMerchantServiceCommision(double merchantServiceCommision) {
		this.merchantServiceCommision = merchantServiceCommision;
	}

	public char getIsSettled() {
		return isSettled;
	}

	public void setIsSettled(char isSettled) {
		this.isSettled = isSettled;
	}

	public char getIsReconcile() {
		return isReconcile;
	}

	public void setIsReconcile(char isReconcile) {
		this.isReconcile = isReconcile;
	}

	public String getTrxnId() {
		return trxnId;
	}

	public void setTrxnId(String trxnId) {
		this.trxnId = trxnId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public long getMerchantServiceId() {
		return merchantServiceId;
	}

	public void setMerchantServiceId(long merchantServiceId) {
		this.merchantServiceId = merchantServiceId;
	}

	public long getTransactionStatusId() {
		return transactionStatusId;
	}

	public void setTransactionStatusId(long transactionStatusId) {
		this.transactionStatusId = transactionStatusId;
	}

	public String getTransactionRefId() {
		return transactionRefId;
	}

	public void setTransactionRefId(String transactionRefId) {
		this.transactionRefId = transactionRefId;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getTrxnIdentifier() {
		return trxnIdentifier;
	}

	public void setTrxnIdentifier(String trxnIdentifier) {
		this.trxnIdentifier = trxnIdentifier;
	}

	public double getBbpsPlatformFee() {
		return bbpsPlatformFee;
	}

	public void setBbpsPlatformFee(double bbpsPlatformFee) {
		this.bbpsPlatformFee = bbpsPlatformFee;
	}

	
}