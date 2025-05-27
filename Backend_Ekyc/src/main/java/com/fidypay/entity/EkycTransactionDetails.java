package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fidypay.encryption.Encryption;

//transaction details entity class
@Entity
@Table(name = "EKYC_TRANSACTION_DETAILS")
//@Document(indexName = "ekyc_transaction_details", createIndex = true)
public class EkycTransactionDetails {

	@Id
//	@org.springframework.data.annotation.Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EKYC_TRANSACTION_ID")
//	@Field(name = "EKYC_TRANSACTION_ID")
	private Long ekycTransactionId;

	@Column(name = "REQUEST_ID", nullable = false)
//	@Field(name = "REQUEST_ID")
	private Long requestId;

	@Column(name = "MERCHANT_ID", nullable = false)
//	@Field(name = "MERCHANT_ID")
	private Long merchantId;

	@Column(name = "MERCHANT_SERVICE_ID", nullable = false)
//	@Field(name = "MERCHANT_SERVICE_ID")
	private Long merchantServiceId;

	@Column(name = "STATUS", nullable = false)
//	@Field(name = "STATUS", type = FieldType.Keyword)
	private String status;

	@Column(name = "API_STATUS", nullable = false)
//	@Field(name = "API_STATUS")
	private String apiStatus;

	@Column(name = "MERCHANT_SERVICE_COMMISION", nullable = false)
//	@Field(name = "MERCHANT_SERVICE_COMMISION")
	private Double merchantServiceCommision;

	@Column(name = "MERCHANT_SERVICE_CHARGE", nullable = false)
//	@Field(name = "MERCHANT_SERVICE_CHARGE")
	private Double merchantServiceCharge;

	@Column(name = "EKYC_SERVICENAME", nullable = false)
//	@Field(name = "EKYC_SERVICENAME", type = FieldType.Keyword)
	private String ekycServicename;

	@Column(name = "CREATION_DATE", nullable = false)
//	@Field(name = "CREATION_DATE", type = FieldType.Date, store = true  ,format = DateFormat.custom, pattern = "yyyy-MM-dd HH.mm.ss.S")
	private Timestamp creationDate;

	@Column(name = "MERCHANT_TRANSACTION_REF_ID", length = 45, nullable = false)
//	@Field(name = "MERCHANT_TRANSACTION_REF_ID")
	private String merchantTransactionRefId;

	@Column(name = "TRXN_REF_ID", length = 200)
//	@Field(name = "TRXN_REF_ID")
	private String trxnRefId;

	@Column(name = "EKYC_ID", length = 200)
//	@Field(name = "EKYC_ID")
	private String eKycId;

	@Column(name = "TRANSACTION_STATUS_ID")
//	@Field(name = "TRANSACTION_STATUS_ID")
	private long transactionStatusId;

	@Column(name = "SERVICE_PROVIDER_ID")
//	@Field(name = "SERVICE_PROVIDER_ID")
	private long serviceProviderId;

	@Column(name = "IS_RECONCILE")
//	@Field(name = "IS_RECONCILE")
	private char isReconcile;

	public EkycTransactionDetails() {
	}

	public EkycTransactionDetails(Long requestId, Long merchantId, Long merchantServiceId, String status,
			String apiStatus, Double merchantServiceCommision, Double merchantServiceCharge, String ekycServicename,
			Timestamp creationDate, String merchantTransactionRefId, String trxnRefId, String eKycId,
			long transactionStatusId, long serviceProviderId, char isReconcile) {

		this.requestId = requestId;
		this.merchantId = merchantId;
		this.merchantServiceId = merchantServiceId;
		this.status = status;
		this.apiStatus = apiStatus;
		this.merchantServiceCommision = merchantServiceCommision;
		this.merchantServiceCharge = merchantServiceCharge;
		this.ekycServicename = ekycServicename;
		this.creationDate = creationDate;
		this.merchantTransactionRefId = Encryption.encString(merchantTransactionRefId);
		this.trxnRefId = Encryption.encString(trxnRefId);
		this.eKycId = Encryption.encString(eKycId);
		this.transactionStatusId = transactionStatusId;
		this.serviceProviderId = serviceProviderId;
		this.isReconcile = isReconcile;
	}

	public EkycTransactionDetails(Long ekycTransactionId) {
		this.ekycTransactionId = ekycTransactionId;
	}

	public Long getEkycTransactionId() {
		return ekycTransactionId;
	}

	public void setEkycTransactionId(Long ekycTransactionId) {
		this.ekycTransactionId = ekycTransactionId;
	}

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public Long getMerchantServiceId() {
		return merchantServiceId;
	}

	public void setMerchantServiceId(Long merchantServiceId) {
		this.merchantServiceId = merchantServiceId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getApiStatus() {
		return apiStatus;
	}

	public void setApiStatus(String apiStatus) {
		this.apiStatus = apiStatus;
	}

	public Double getMerchantServiceCommision() {
		return merchantServiceCommision;
	}

	public void setMerchantServiceCommision(Double merchantServiceCommision) {
		this.merchantServiceCommision = merchantServiceCommision;
	}

	public Double getMerchantServiceCharge() {
		return merchantServiceCharge;
	}

	public void setMerchantServiceCharge(Double merchantServiceCharge) {
		this.merchantServiceCharge = merchantServiceCharge;
	}

	public String getEkycServicename() {
		return ekycServicename;
	}

	public void setEkycServicename(String ekycServicename) {
		this.ekycServicename = ekycServicename;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	public String getMerchantTransactionRefId() {
		return merchantTransactionRefId;
	}

	public void setMerchantTransactionRefId(String merchantTransactionRefId) {
		this.merchantTransactionRefId = merchantTransactionRefId;
	}

	public String getTrxnRefId() {
		return trxnRefId;
	}

	public void setTrxnRefId(String trxnRefId) {
		this.trxnRefId = trxnRefId;
	}

	public String geteKycId() {
		return eKycId;
	}

	public void seteKycId(String eKycId) {
		this.eKycId = eKycId;
	}

	public long getServiceProviderId() {
		return serviceProviderId;
	}

	public void setServiceProviderId(long serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}

	public char getIsReconcile() {
		return isReconcile;
	}

	public void setIsReconcile(char isReconcile) {
		this.isReconcile = isReconcile;
	}

	public long getTransactionStatusId() {
		return transactionStatusId;
	}

	public void setTransactionStatusId(long transactionStatusId) {
		this.transactionStatusId = transactionStatusId;
	}

	@Override
	public String toString() {
		return "EkycTransactionDetails [ekycTransactionId=" + ekycTransactionId + ", requestId=" + requestId
				+ ", merchantId=" + merchantId + ", merchantServiceId=" + merchantServiceId + ", status=" + status
				+ ", apiStatus=" + apiStatus + ", merchantServiceCommision=" + merchantServiceCommision
				+ ", merchantServiceCharge=" + merchantServiceCharge + ", ekycServicename=" + ekycServicename
				+ ", creationDate=" + creationDate + ", merchantTransactionRefId=" + merchantTransactionRefId
				+ ", trxnRefId=" + trxnRefId + ", eKycId=" + eKycId + ", transactionStatusId=" + transactionStatusId
				+ ", serviceProviderId=" + serviceProviderId + ", isReconcile=" + isReconcile + "]";
	}
}
