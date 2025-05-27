package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "ENACH_TRANSACTION_DETAILS")
@EntityListeners(AuditingEntityListener.class)
public class ENachTransactionDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ENACH_TRANSACTION_ID")
	private Long eNachTransactionId;

	@Column(name = "RESPONSE_ID")
	private Long responseId;

	@Column(name = "CUSTOMER_ID", length = 100, nullable = false)
	private String customerId;

	@Column(name = "MANDATE_ID", length = 100, nullable = false)
	private String mandateId;

	@Column(name = "API_STATUS", length = 100, nullable = false)
	private String apiStatus;

	@Column(name = "TYPE", length = 100, nullable = false)
	private String type;

	@Column(name = "MANDATE_ACTIVATION_DATE", length = 100, nullable = false, unique = true)
	private Timestamp mandateActivationDate;

	@Column(name = "CUSTOMER_NAME", length = 100, nullable = false, unique = true)
	private String customerName;

	@Column(name = "CUSTOMER_BANK_ACCOUNT_NO", length = 100, nullable = false, unique = true)
	private String customerBankAccountNumber;

	@Column(name = "CUSTOMER_BANK_IFSC", length = 100, nullable = false, unique = true)
	private String customerBankIfsc;

	@Column(name = "CUSTOMER_BANK_NAME", length = 100, nullable = false, unique = true)
	private String customerBankName;

	@Column(name = "FREQUENCY", length = 100, nullable = false, unique = true)
	private String frequency;

	@Column(name = "CUSTOMER_ACCOUNT_TYPE", length = 100, nullable = false, unique = true)
	private String customerAccountType;

	@Column(name = "INSTRUMENT_TYPE", length = 100, nullable = false, unique = true)
	private String instrumentType;

	@Column(name = "MERCHANT_ID", nullable = false)
	private long merchantId;

	@Column(name = "MERCHANT_SERVICE_ID", nullable = false)
	private long merchantServiceId;

	@Column(name = "TRANSACTION_STATUS", length = 100, nullable = false)
	private String transactionStatus;

	@Column(name = "TRANSACTION_DATE", nullable = false)
	private Timestamp transactionDate;

	@Column(name = "MERCHANT_TRANSACTION_REF_ID", length = 200, nullable = false)
	private String merchantTransactionRefId;

	@Column(name = "TRANSACTION_AMOUNT", nullable = false)
	private double transactionAmount;

	@Column(name = "ENACH_ID", length = 100, nullable = false)
	private String eNachId;

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

	@Column(name = "MANDATE_CANCELLATION_DATE", nullable = false)
	private Timestamp mandateCancellationDate;

	@Column(name = "MANDATE_CANCELLATION_ID", nullable = false)
	private String mandateCancellationId;

	@Column(name = "SERVICE_PROVIDER_NAME", nullable = false)
	private String serviceProviderName;

	@Column(name = "SERVICE_PROVIDER_UTILITY_CODE", nullable = false)
	private String serviceProviderUtilityCode;

	@Column(name = "ENACH_UMRN", nullable = false)
	private String eNachUMRN;

	@Column(name = "BANK_ID", nullable = false)
	private String bankId;

	@Column(name = "REQUEST_SOURCE", nullable = false)
	private String requestSource;

	@Column(name = "IS_INITIATED", length = 1, nullable = false)
	private char isInitiated;

	@Column(name = "IS_PENDING", length = 1, nullable = false)
	private char isPending;

	@Column(name = "IS_REGISTERED", length = 1, nullable = false)
	private char isRegistered;

	@Column(name = "FINAL_COLLECTION_DATE", nullable = false)
	private String finalCollectionDate;

	@Column(name = "CATEGORY_CODE", nullable = false)
	private String categoryCode;

	@Column(name = "DEBIT_TYPE", nullable = false)
	private String debitType;

	public ENachTransactionDetails() {
	}

	public ENachTransactionDetails(Long responseId, String customerId, String mandateId, String apiStatus, String type,
			Timestamp mandateActivationDate, String customerName, String customerBankAccountNumber,
			String customerBankIfsc, String customerBankName, String frequency, String customerAccountType,
			String instrumentType, long merchantId, long merchantServiceId, String transactionStatus,
			Timestamp transactionDate, String merchantTransactionRefId, double transactionAmount, String eNachId,
			double merchantServiceCommision, double merchantServiceCharge, char isReconcile, char isSettled,
			String remark, String serviceName, String trxnRefId, long transactionStatusId,
			Timestamp mandateCancellationDate, String mandateCancellationId, String serviceProviderName,
			String serviceProviderUtilityCode, String eNachUMRN, String bankId, String requestSource) {
		super();
		this.responseId = responseId;
		this.customerId = customerId;
		this.mandateId = mandateId;
		this.apiStatus = apiStatus;
		this.type = type;
		this.mandateActivationDate = mandateActivationDate;
		this.customerName = customerName;
		this.customerBankAccountNumber = customerBankAccountNumber;
		this.customerBankIfsc = customerBankIfsc;
		this.customerBankName = customerBankName;
		this.frequency = frequency;
		this.customerAccountType = customerAccountType;
		this.instrumentType = instrumentType;
		this.merchantId = merchantId;
		this.merchantServiceId = merchantServiceId;
		this.transactionStatus = transactionStatus;
		this.transactionDate = transactionDate;
		this.merchantTransactionRefId = merchantTransactionRefId;
		this.transactionAmount = transactionAmount;
		this.eNachId = eNachId;
		this.merchantServiceCommision = merchantServiceCommision;
		this.merchantServiceCharge = merchantServiceCharge;
		this.isReconcile = isReconcile;
		this.isSettled = isSettled;
		this.remark = remark;
		this.serviceName = serviceName;
		this.trxnRefId = trxnRefId;
		this.transactionStatusId = transactionStatusId;
		this.mandateCancellationDate = mandateCancellationDate;
		this.mandateCancellationId = mandateCancellationId;
		this.serviceProviderName = serviceProviderName;
		this.serviceProviderUtilityCode = serviceProviderUtilityCode;
		this.eNachUMRN = eNachUMRN;
		this.bankId = bankId;
		this.requestSource = requestSource;
	}

	public Long geteNachTransactionId() {
		return eNachTransactionId;
	}

	public void seteNachTransactionId(Long eNachTransactionId) {
		this.eNachTransactionId = eNachTransactionId;
	}

	public Long getResponseId() {
		return responseId;
	}

	public void setResponseId(Long responseId) {
		this.responseId = responseId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getMandateId() {
		return mandateId;
	}

	public void setMandateId(String mandateId) {
		this.mandateId = mandateId;
	}

	public String getApiStatus() {
		return apiStatus;
	}

	public void setApiStatus(String apiStatus) {
		this.apiStatus = apiStatus;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Timestamp getMandateActivationDate() {
		return mandateActivationDate;
	}

	public void setMandateActivationDate(Timestamp mandateActivationDate) {
		this.mandateActivationDate = mandateActivationDate;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerBankAccountNumber() {
		return customerBankAccountNumber;
	}

	public void setCustomerBankAccountNumber(String customerBankAccountNumber) {
		this.customerBankAccountNumber = customerBankAccountNumber;
	}

	public String getCustomerBankIfsc() {
		return customerBankIfsc;
	}

	public void setCustomerBankIfsc(String customerBankIfsc) {
		this.customerBankIfsc = customerBankIfsc;
	}

	public String getCustomerBankName() {
		return customerBankName;
	}

	public void setCustomerBankName(String customerBankName) {
		this.customerBankName = customerBankName;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getCustomerAccountType() {
		return customerAccountType;
	}

	public void setCustomerAccountType(String customerAccountType) {
		this.customerAccountType = customerAccountType;
	}

	public String getInstrumentType() {
		return instrumentType;
	}

	public void setInstrumentType(String instrumentType) {
		this.instrumentType = instrumentType;
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

	public String geteNachId() {
		return eNachId;
	}

	public void seteNachId(String eNachId) {
		this.eNachId = eNachId;
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

	public Timestamp getMandateCancellationDate() {
		return mandateCancellationDate;
	}

	public void setMandateCancellationDate(Timestamp mandateCancellationDate) {
		this.mandateCancellationDate = mandateCancellationDate;
	}

	public String getMandateCancellationId() {
		return mandateCancellationId;
	}

	public void setMandateCancellationId(String mandateCancellationId) {
		this.mandateCancellationId = mandateCancellationId;
	}

	public String getServiceProviderName() {
		return serviceProviderName;
	}

	public void setServiceProviderName(String serviceProviderName) {
		this.serviceProviderName = serviceProviderName;
	}

	public String getServiceProviderUtilityCode() {
		return serviceProviderUtilityCode;
	}

	public void setServiceProviderUtilityCode(String serviceProviderUtilityCode) {
		this.serviceProviderUtilityCode = serviceProviderUtilityCode;
	}

	public String geteNachUMRN() {
		return eNachUMRN;
	}

	public void seteNachUMRN(String eNachUMRN) {
		this.eNachUMRN = eNachUMRN;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getRequestSource() {
		return requestSource;
	}

	public void setRequestSource(String requestSource) {
		this.requestSource = requestSource;
	}

	public char getIsInitiated() {
		return isInitiated;
	}

	public void setIsInitiated(char isInitiated) {
		this.isInitiated = isInitiated;
	}

	public char getIsPending() {
		return isPending;
	}

	public void setIsPending(char isPending) {
		this.isPending = isPending;
	}

	public char getIsRegistered() {
		return isRegistered;
	}

	public void setIsRegistered(char isRegistered) {
		this.isRegistered = isRegistered;
	}

	public String getFinalCollectionDate() {
		return finalCollectionDate;
	}

	public void setFinalCollectionDate(String finalCollectionDate) {
		this.finalCollectionDate = finalCollectionDate;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getDebitType() {
		return debitType;
	}

	public void setDebitType(String debitType) {
		this.debitType = debitType;
	}

}