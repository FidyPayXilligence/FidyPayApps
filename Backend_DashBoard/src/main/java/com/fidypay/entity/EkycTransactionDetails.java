package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//transaction details entity class
@Entity
@Table(name = "EKYC_TRANSACTION_DETAILS")
public class EkycTransactionDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EKYC_TRANSACTION_ID")
	private Long ekycTransactionId;

	@Column(name = "REQUEST_ID", nullable = false)
	private Long requestId;

	@Column(name = "MERCHANT_ID", nullable = false)
	private Long merchantId;

	@Column(name = "MERCHANT_SERVICE_ID", nullable = false)
	private Long merchantServiceId;

	@Column(name = "STATUS", nullable = false)
	private String status;

	@Column(name = "API_STATUS", nullable = false)
	private String apiStatus;

	@Column(name = "MERCHANT_SERVICE_COMMISION", nullable = false)
	private Double merchantServiceCommision;

	@Column(name = "MERCHANT_SERVICE_CHARGE", nullable = false)
	private Double merchantServiceCharge;

	@Column(name = "EKYC_SERVICENAME", nullable = false)
	private String ekycServicename;

	@Column(name = "CREATION_DATE", nullable = false)
	private Timestamp creationDate;

	@Column(name = "MERCHANT_TRANSACTION_REF_ID", length = 45, nullable = false)
	private String merchantTransactionRefId;

	@Column(name = "TRXN_REF_ID", length = 200)
	private String trxnRefId;

	@Column(name = "EKYC_ID", length = 200)
	private String eKycId;
	
	
	@Column(name = "TRANSACTION_STATUS_ID")
	private long transactionStatusId;
	
	
	 @Column(name = "IS_RECONCILE")
	 private Character isReconcile;

	public EkycTransactionDetails() {
	}

	public EkycTransactionDetails(Long requestId, Long merchantId, Long merchantServiceId, String status,
			String apiStatus, Double merchantServiceCommision, Double merchantServiceCharge, String ekycServicename,
			Timestamp creationDate, String merchantTransactionRefId, String trxnRefId, String eKycId, long transactionStatusId) {
		this.requestId = requestId;
		this.merchantId = merchantId;
		this.merchantServiceId = merchantServiceId;
		this.status = status;
		this.apiStatus = apiStatus;
		this.merchantServiceCommision = merchantServiceCommision;
		this.merchantServiceCharge = merchantServiceCharge;
		this.ekycServicename = ekycServicename;
		this.creationDate = creationDate;
		this.merchantTransactionRefId = merchantTransactionRefId;
		this.trxnRefId = trxnRefId;
		this.eKycId = eKycId;
		this.transactionStatusId = transactionStatusId;
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

	public Character getIsReconcile() {
		return isReconcile;
	}

	public void setIsReconcile(Character isReconcile) {
		this.isReconcile = isReconcile;
	}
	
	

}