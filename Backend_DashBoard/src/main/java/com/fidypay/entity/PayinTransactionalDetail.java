package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PAYIN_TRANSACTION_DETAIL")
public class PayinTransactionalDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PAYIN_TRANSACTION_ID", nullable = false)
	private long payinTransactionId;

	@Column(name = "REQUEST_ID")
	private Long requestId;

	@Column(name = "MERCHANT_ID", nullable = false)
	private long merchantId;

	@Column(name = "MERCHANT_SERVICE_ID", nullable = false)
	private long merchantServiceId;

	@Column(name = "TRANSACTION_DATE", nullable = false)
	private Timestamp transactionDate;

	@Column(name = "PG_MERCHANT_ID", nullable = false)
	private String pgMerchantId;

	@Column(name = "PAYER_VPA", nullable = false)
	private String payerVpa;

	@Column(name = "PAYEE_VPA", nullable = false)
	private String payeeVpa;

	@Column(name = "MERCHANT_TRANSACTION_REF_ID", nullable = false)
	private String merchantTransactionRefId;

	@Column(name = "UTR", nullable = false)
	private String utr;

	@Column(name = "BANK_REFERENCE_ID", nullable = false)
	private String bankReferenceId;

	@Column(name = "NPCI_REFERENCE_ID", nullable = false)
	private String npciReferenceId;

	@Column(name = "TRANSACTION_STATUS", nullable = false)
	private String transactionStatus;

	@Column(name = "BANK_SIDE_STATUS", nullable = false)
	private String bankSideStatus;

	@Column(name = "MERCHANT_SERVICE_CHARGE", nullable = false)
	private double merchantServiceCharge;

	@Column(name = "MERCHANT_SERVICE_COMMISSION", nullable = false)
	private double merchantServiceCommission;

	@Column(name = "IS_RECONCILE", nullable = false)
	private char isReconcile;

	@Column(name = "IS_SETTELED", nullable = false)
	private char isSettled;

	@Column(name = "REMARK", nullable = false)
	private String remark;

	@Column(name = "REFUND_ID", nullable = false)
	private String refundId;

	@Column(name = "TRANSACTION_AMOUNT", nullable = false)
	private double transactionAmount=0.0;

	@Column(name = "MERCHANT_BUSSINESS_NAME")
	private String merchantBussinessName;

	@Column(name = "SUBMERCHANT_BUSSINESS_NAME")
	private String subMerchantBussinessName;
	
	
	@Column(name = "SERVICE_NAME")
	private String serviceName;
	
	
	@Column(name = "TRXN_REF_ID")
	private String trxnRefId;
	
	@Column(name = "TYPE")
	private String type;

	@Column(name = "TRANSACTION_STATUS_ID")
	private long transactionStatusId;

	
	public PayinTransactionalDetail() {
	}

	public PayinTransactionalDetail(long payinTransactionId, Long requestId, long merchantId, long merchantServiceId,
			Timestamp transactionDate, String pgMerchantId, String payerVpa, String payeeVpa,
			String merchantTransactionRefId, String utr, String bankReferenceId, String npciReferenceId,
			String transactionStatus, String bankSideStatus, double merchantServiceCharge,
			double merchantServiceCommission, char isReconcile, char isSettled, String remark, String refundId,
			double transactionAmount, String merchantBussinessName, String subMerchantBussinessName) {
		this.payinTransactionId = payinTransactionId;
		this.requestId = requestId;
		this.merchantId = merchantId;
		this.merchantServiceId = merchantServiceId;
		this.transactionDate = transactionDate;
		this.pgMerchantId = pgMerchantId;
		this.payerVpa = payerVpa;
		this.payeeVpa = payeeVpa;
		this.merchantTransactionRefId = merchantTransactionRefId;
		this.utr = utr;
		this.bankReferenceId = bankReferenceId;
		this.npciReferenceId = npciReferenceId;
		this.transactionStatus = transactionStatus;
		this.bankSideStatus = bankSideStatus;
		this.merchantServiceCharge = merchantServiceCharge;
		this.merchantServiceCommission = merchantServiceCommission;
		this.isReconcile = isReconcile;
		this.isSettled = isSettled;
		this.remark = remark;
		this.refundId = refundId;
		this.transactionAmount = transactionAmount;
		this.merchantBussinessName = merchantBussinessName;
		this.subMerchantBussinessName = subMerchantBussinessName;
	}

	public long getPayinTransactionId() {
		return payinTransactionId;
	}

	public void setPayinTransactionId(long payinTransactionId) {
		this.payinTransactionId = payinTransactionId;
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

	public Timestamp getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Timestamp transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getPgMerchantId() {
		return pgMerchantId;
	}

	public void setPgMerchantId(String pgMerchantId) {
		this.pgMerchantId = pgMerchantId;
	}

	public String getPayerVpa() {
		return payerVpa;
	}

	public void setPayerVpa(String payerVpa) {
		this.payerVpa = payerVpa;
	}

	public String getPayeeVpa() {
		return payeeVpa;
	}

	public void setPayeeVpa(String payeeVpa) {
		this.payeeVpa = payeeVpa;
	}

	public String getMerchantTransactionRefId() {
		return merchantTransactionRefId;
	}

	public void setMerchantTransactionRefId(String merchantTransactionRefId) {
		this.merchantTransactionRefId = merchantTransactionRefId;
	}

	public String getUtr() {
		return utr;
	}

	public void setUtr(String utr) {
		this.utr = utr;
	}

	public String getBankReferenceId() {
		return bankReferenceId;
	}

	public void setBankReferenceId(String bankReferenceId) {
		this.bankReferenceId = bankReferenceId;
	}

	public String getNpciReferenceId() {
		return npciReferenceId;
	}

	public void setNpciReferenceId(String npciReferenceId) {
		this.npciReferenceId = npciReferenceId;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getBankSideStatus() {
		return bankSideStatus;
	}

	public void setBankSideStatus(String bankSideStatus) {
		this.bankSideStatus = bankSideStatus;
	}

	public double getMerchantServiceCharge() {
		return merchantServiceCharge;
	}

	public void setMerchantServiceCharge(double merchantServiceCharge) {
		this.merchantServiceCharge = merchantServiceCharge;
	}

	public double getMerchantServiceCommission() {
		return merchantServiceCommission;
	}

	public void setMerchantServiceCommission(double merchantServiceCommission) {
		this.merchantServiceCommission = merchantServiceCommission;
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

	public String getRefundId() {
		return refundId;
	}

	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public double getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getMerchantBussinessName() {
		return merchantBussinessName;
	}

	public void setMerchantBussinessName(String merchantBussinessName) {
		this.merchantBussinessName = merchantBussinessName;
	}

	public String getSubMerchantBussinessName() {
		return subMerchantBussinessName;
	}

	public void setSubMerchantBussinessName(String subMerchantBussinessName) {
		this.subMerchantBussinessName = subMerchantBussinessName;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getTransactionStatusId() {
		return transactionStatusId;
	}

	public void setTransactionStatusId(long transactionStatusId) {
		this.transactionStatusId = transactionStatusId;
	}
	
	
	

}
