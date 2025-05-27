package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PAYOUT_TRANSACTION_DETAILS")
public class PayoutTransactionDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PAYOUT_TRANSACTION_ID")
	private Long payoutTransactionId;

	@Column(name = "REQUEST_ID")
	private Long requestId;

	@Column(name = "MERCHANT_ID", nullable = false)
	private long merchantId;

	@Column(name = "MERCHANT_SERVICE_ID", nullable = false)
	private long merchantServiceId;

	@Column(name = "BANK_ACCOUNT_KEY", length = 200, nullable = false)
	private String bankAccountKey;

	@Column(name = "DEBITOR_ID", length = 100, nullable = false)
	private String debitorId;

	@Column(name = "BANK_TRANSACTION_IDENTIFICATION", length = 200, nullable = false)
	private String bankTransactionIdentification;

	@Column(name = "TRANSACTION_STATUS", length = 100, nullable = false)
	private String transactionStatus;

	@Column(name = "TRANSACTION_DATE", nullable = false)
	private Timestamp transactionDate;

	@Column(name = "MERCHANT_TRANSACTION_REF_ID", length = 200, nullable = false)
	private String merchantTransactionRefId;

	@Column(name = "TRANSACTION_AMOUNT", nullable = false)
	private double transactionAmount;

	@Column(name = "DEBITOR_ACCOUNT_NUMBER", length = 200, nullable = false)
	private String debitorAcountNumber;

	@Column(name = "DEBITOR_IFSC", length = 100, nullable = false)
	private String debitorIfsc;

	@Column(name = "CREDITOR_ACCOUNT_NUMBER", length = 200, nullable = false)
	private String creditorAccountNumber;

	@Column(name = "CREDITOR_IFSC", length = 100, nullable = false)
	private String creditorIfsc;

	@Column(name = "CREDITOR_NAME", length = 200, nullable = false)
	private String creditorName;

	@Column(name = "CREDITOR_EMAIL", length = 200, nullable = false)
	private String creditorEmail;

	@Column(name = "CREDITOR_MOBILE", length = 100, nullable = false)
	private String creditorMobile;

	@Column(name = "TRANSACTION_TYPE", length = 100, nullable = false)
	private String transactionType;

	@Column(name = "UTR", length = 200, nullable = false)
	private String utr;

	@Column(name = "BANK_SIDE_STATUS", length = 100, nullable = false)
	private String bankSideStatus;

	@Column(name = "MERCHANT_SERVICE_COMMISION", nullable = false)
	private double merchantServiceCommision;

	@Column(name = "MERCHANT_SERVICE_CHARGE", nullable = false)
	private double merchantServiceCharge;

	@Column(name = "IS_RECONCILE", length = 1, nullable = false)
	private char isReconcile;

	@Column(name = "IS_SETTLED", length = 1, nullable = false)
	private char isSettled;

	@Column(name = "REMARK", length = 300, nullable = false)
	private String remark;

	@Column(name = "SERVICE_NAME", length = 200)
	private String serviceName;

	@Column(name = "TRXN_REF_ID", length = 200)
	private String trxnRefId;

	@Column(name = "TRANSACTION_STATUS_ID")
	private long transactionStatusId;
	
	public PayoutTransactionDetails() {
	}

	public PayoutTransactionDetails(Long payoutTransactionId, Long requestId, long merchantId, long merchantServiceId,
			String bankAccountKey, String debitorId, String bankTransactionIdentification, String transactionStatus,
			Timestamp transactionDate, String merchantTransactionRefId, double transactionAmount,
			String debitorAcountNumber, String debitorIfsc, String creditorAccountNumber, String creditorIfsc,
			String creditorName, String creditorEmail, String creditorMobile, String transactionType, String utr,
			String bankSideStatus, double merchantServiceCommision, double merchantServiceCharge, char isReconcile,
			char isSettled, String remark, String serviceName, String trxnRefId) {
		this.payoutTransactionId = payoutTransactionId;
		this.requestId = requestId;
		this.merchantId = merchantId;
		this.merchantServiceId = merchantServiceId;
		this.bankAccountKey = bankAccountKey;
		this.debitorId = debitorId;
		this.bankTransactionIdentification = bankTransactionIdentification;
		this.transactionStatus = transactionStatus;
		this.transactionDate = transactionDate;
		this.merchantTransactionRefId = merchantTransactionRefId;
		this.transactionAmount = transactionAmount;
		this.debitorAcountNumber = debitorAcountNumber;
		this.debitorIfsc = debitorIfsc;
		this.creditorAccountNumber = creditorAccountNumber;
		this.creditorIfsc = creditorIfsc;
		this.creditorName = creditorName;
		this.creditorEmail = creditorEmail;
		this.creditorMobile = creditorMobile;
		this.transactionType = transactionType;
		this.utr = utr;
		this.bankSideStatus = bankSideStatus;
		this.merchantServiceCommision = merchantServiceCommision;
		this.merchantServiceCharge = merchantServiceCharge;
		this.isReconcile = isReconcile;
		this.isSettled = isSettled;
		this.remark = remark;
		this.serviceName = serviceName;
		this.trxnRefId = trxnRefId;
	}

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public PayoutTransactionDetails(Long payoutTransactionId) {
		this.payoutTransactionId = payoutTransactionId;
	}

	public Long getPayoutTransactionId() {
		return payoutTransactionId;
	}

	public void setPayoutTransactionId(Long payoutTransactionId) {
		this.payoutTransactionId = payoutTransactionId;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public long getMerchantServiceId() {
		return merchantServiceId;
	}

	public void setMerchantServiceId(long merchantServiceId) {
		this.merchantServiceId = merchantServiceId;
	}

	public String getBankAccountKey() {
		return bankAccountKey;
	}

	public void setBankAccountKey(String bankAccountKey) {
		this.bankAccountKey = bankAccountKey;
	}

	public String getDebitorId() {
		return debitorId;
	}

	public void setDebitorId(String debitorId) {
		this.debitorId = debitorId;
	}

	public String getBankTransactionIdentification() {
		return bankTransactionIdentification;
	}

	public void setBankTransactionIdentification(String bankTransactionIdentification) {
		this.bankTransactionIdentification = bankTransactionIdentification;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public Timestamp getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Timestamp transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getMerchantTransactionRefId() {
		return merchantTransactionRefId;
	}

	public void setMerchantTransactionRefId(String merchantTransactionRefId) {
		this.merchantTransactionRefId = merchantTransactionRefId;
	}

	public double getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getDebitorAcountNumber() {
		return debitorAcountNumber;
	}

	public void setDebitorAcountNumber(String debitorAcountNumber) {
		this.debitorAcountNumber = debitorAcountNumber;
	}

	public String getDebitorIfsc() {
		return debitorIfsc;
	}

	public void setDebitorIfsc(String debitorIfsc) {
		this.debitorIfsc = debitorIfsc;
	}

	public String getCreditorAccountNumber() {
		return creditorAccountNumber;
	}

	public void setCreditorAccountNumber(String creditorAccountNumber) {
		this.creditorAccountNumber = creditorAccountNumber;
	}

	public String getCreditorIfsc() {
		return creditorIfsc;
	}

	public void setCreditorIfsc(String creditorIfsc) {
		this.creditorIfsc = creditorIfsc;
	}

	public String getCreditorName() {
		return creditorName;
	}

	public void setCreditorName(String creditorName) {
		this.creditorName = creditorName;
	}

	public String getCreditorEmail() {
		return creditorEmail;
	}

	public void setCreditorEmail(String creditorEmail) {
		this.creditorEmail = creditorEmail;
	}

	public String getCreditorMobile() {
		return creditorMobile;
	}

	public void setCreditorMobile(String creditorMobile) {
		this.creditorMobile = creditorMobile;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getUtr() {
		return utr;
	}

	public void setUtr(String utr) {
		this.utr = utr;
	}

	public String getBankSideStatus() {
		return bankSideStatus;
	}

	public void setBankSideStatus(String bankSideStatus) {
		this.bankSideStatus = bankSideStatus;
	}

	public double getMerchantServiceCommision() {
		return merchantServiceCommision;
	}

	public void setMerchantServiceCommision(double merchantServiceCommision) {
		this.merchantServiceCommision = merchantServiceCommision;
	}

	public double getMerchantServiceCharge() {
		return merchantServiceCharge;
	}

	public void setMerchantServiceCharge(double merchantServiceCharge) {
		this.merchantServiceCharge = merchantServiceCharge;
	}

	public char getIsReconcile() {
		return isReconcile;
	}

	public void setIsReconcile(char isReconcile) {
		this.isReconcile = isReconcile;
	}

	public char getIsSettled() {
		return isSettled;
	}

	public void setIsSettled(char isSettled) {
		this.isSettled = isSettled;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getTrxnRefId() {
		return trxnRefId;
	}

	public void setTrxnRefId(String trxnRefId) {
		this.trxnRefId = trxnRefId;
	}

	public long getTransactionStatusId() {
		return transactionStatusId;
	}

	public void setTransactionStatusId(long transactionStatusId) {
		this.transactionStatusId = transactionStatusId;
	}

	
	
}