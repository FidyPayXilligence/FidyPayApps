package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PG_TRANSACTION_DETAIL")
public class PGTransactionDetail {

	@Id
	@org.springframework.data.annotation.Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PG_TRANSACTION_ID", nullable = false)
	private long pgTransactionId;

	@Column(name = "MERCHANT_ID", nullable = false)
	private long merchantId;

	@Column(name = "TRANSACTION_DATE", nullable = false)
	private Timestamp transactionDate;

	@Column(name = "TRANSACTION_AMOUNT", nullable = false)
	private double transactionAmount;

	@Column(name = "ADDITIONAL_AMOUNT", nullable = false)
	private double additionalAmount;

	@Column(name = "MERCHANT_TRANSACTION_REF_ID", nullable = false)
	private String merchantTransactionRefId;

	@Column(name = "MERCHANT_VPA", nullable = false)
	private String merchantVPA;

	@Column(name = "PAYER_VPA", nullable = false)
	private String payerVpa;

	@Column(name = "BANK_CODE", nullable = false)
	private String bankCode;

	@Column(name = "CARD_NUMBER", nullable = false)
	private String cardNumber;

	@Column(name = "CARD_TYPE", nullable = false)
	private String cardType;

	@Column(name = "CARD_HOLDER_NAME", nullable = false)
	private String cardHolderName;

	@Column(name = "CUSTOMER_NAME", nullable = false)
	private String customerName;

	@Column(name = "CUSTOMER_MOBILE", nullable = false)
	private String customerMobile;

	@Column(name = "CUSTOMER_EMAIL", nullable = false)
	private String customerEmail;

	@Column(name = "PAYMENT_MODE", nullable = false)
	private String paymentMode;

	@Column(name = "PAYMENT_ID", nullable = false)
	private String paymentId;

	@Column(name = "BANK_REFERENCE_NUMBER", nullable = false)
	private String bankReferanceNumber;

	@Column(name = "TRXN_REF_ID")
	private String trxnRefId;

	@Column(name = "REFUND_ID", nullable = false)
	private String refundId;

	@Column(name = "REFUND_DATE", nullable = false)
	private Timestamp refundDate;

	@Column(name = "REFUND_MESSAGE", nullable = false)
	private String refundMessage;

	@Column(name = "REMARK", nullable = false)
	private String remark;

	@Column(name = "API_STATUS", nullable = false)
	private String apiStatus;

	@Column(name = "TRANSACTION_STATUS", nullable = false)
	private String transactionStatus;

	@Column(name = "SERVICE_NAME")
	private String serviceName;

	@Column(name = "MERCHANT_SERVICE_COMMISSION", nullable = false)
	private double merchantServiceCommission;

	@Column(name = "MERCHANT_SERVICE_CHARGE", nullable = false)
	private double merchantServiceCharge;

	@Column(name = "IS_RECONCILE", nullable = false)
	private char isReconcile;

	@Column(name = "IS_SETTELED", nullable = false)
	private char isSettled;

	@Column(name = "MERCHANT_SERVICE_ID", nullable = false)
	private Long merchantServiceId;

	@Column(name = "TRANSACTION_STATUS_ID")
	private long transactionStatusId;

	@Column(name = "MERCHANT_USER_ID")
	private long merchantUserId;

	public PGTransactionDetail() {
	}

	public PGTransactionDetail(long pgTransactionId, long merchantId, Timestamp transactionDate,
			double transactionAmount, double additionalAmount, String merchantTransactionRefId, String merchantVPA,
			String payerVpa, String bankCode, String cardNumber, String cardType, String cardHolderName,
			String customerName, String customerMobile, String customerEmail, String paymentMode, String paymentId,
			String bankReferanceNumber, String trxnRefId, String refundId, Timestamp refundDate, String refundMessage,
			String remark, String apiStatus, String transactionStatus, String serviceName,
			double merchantServiceCommission, double merchantServiceCharge, char isReconcile, char isSettled,
			Long merchantServiceId, long transactionStatusId, long merchantUserId) {
		super();
		this.pgTransactionId = pgTransactionId;
		this.merchantId = merchantId;
		this.transactionDate = transactionDate;
		this.transactionAmount = transactionAmount;
		this.additionalAmount = additionalAmount;
		this.merchantTransactionRefId = merchantTransactionRefId;
		this.merchantVPA = merchantVPA;
		this.payerVpa = payerVpa;
		this.bankCode = bankCode;
		this.cardNumber = cardNumber;
		this.cardType = cardType;
		this.cardHolderName = cardHolderName;
		this.customerName = customerName;
		this.customerMobile = customerMobile;
		this.customerEmail = customerEmail;
		this.paymentMode = paymentMode;
		this.paymentId = paymentId;
		this.bankReferanceNumber = bankReferanceNumber;
		this.trxnRefId = trxnRefId;
		this.refundId = refundId;
		this.refundDate = refundDate;
		this.refundMessage = refundMessage;
		this.remark = remark;
		this.apiStatus = apiStatus;
		this.transactionStatus = transactionStatus;
		this.serviceName = serviceName;
		this.merchantServiceCommission = merchantServiceCommission;
		this.merchantServiceCharge = merchantServiceCharge;
		this.isReconcile = isReconcile;
		this.isSettled = isSettled;
		this.merchantServiceId = merchantServiceId;
		this.transactionStatusId = transactionStatusId;
		this.merchantUserId = merchantUserId;
	}

	public long getPgTransactionId() {
		return pgTransactionId;
	}

	public void setPgTransactionId(long pgTransactionId) {
		this.pgTransactionId = pgTransactionId;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public Timestamp getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Timestamp transactionDate) {
		this.transactionDate = transactionDate;
	}

	public double getAdditionalAmount() {
		return additionalAmount;
	}

	public void setAdditionalAmount(double additionalAmount) {
		this.additionalAmount = additionalAmount;
	}

	public String getMerchantTransactionRefId() {
		return merchantTransactionRefId;
	}

	public void setMerchantTransactionRefId(String merchantTransactionRefId) {
		this.merchantTransactionRefId = merchantTransactionRefId;
	}

	public String getMerchantVPA() {
		return merchantVPA;
	}

	public void setMerchantVPA(String merchantVPA) {
		this.merchantVPA = merchantVPA;
	}

	public String getPayerVpa() {
		return payerVpa;
	}

	public void setPayerVpa(String payerVpa) {
		this.payerVpa = payerVpa;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getCardHolderName() {
		return cardHolderName;
	}

	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerMobile() {
		return customerMobile;
	}

	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
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

	public String getBankReferanceNumber() {
		return bankReferanceNumber;
	}

	public void setBankReferanceNumber(String bankReferanceNumber) {
		this.bankReferanceNumber = bankReferanceNumber;
	}

	public String getTrxnRefId() {
		return trxnRefId;
	}

	public void setTrxnRefId(String trxnRefId) {
		this.trxnRefId = trxnRefId;
	}

	public String getRefundId() {
		return refundId;
	}

	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}

	public Timestamp getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(Timestamp refundDate) {
		this.refundDate = refundDate;
	}

	public String getRefundMessage() {
		return refundMessage;
	}

	public void setRefundMessage(String refundMessage) {
		this.refundMessage = refundMessage;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getApiStatus() {
		return apiStatus;
	}

	public void setApiStatus(String apiStatus) {
		this.apiStatus = apiStatus;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public double getMerchantServiceCommission() {
		return merchantServiceCommission;
	}

	public void setMerchantServiceCommission(double merchantServiceCommission) {
		this.merchantServiceCommission = merchantServiceCommission;
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

	public double getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public long getTransactionStatusId() {
		return transactionStatusId;
	}

	public void setTransactionStatusId(long transactionStatusId) {
		this.transactionStatusId = transactionStatusId;
	}

	public Long getMerchantServiceId() {
		return merchantServiceId;
	}

	public void setMerchantServiceId(Long merchantServiceId) {
		this.merchantServiceId = merchantServiceId;
	}

	public long getMerchantUserId() {
		return merchantUserId;
	}

	public void setMerchantUserId(long merchantUserId) {
		this.merchantUserId = merchantUserId;
	}

}
