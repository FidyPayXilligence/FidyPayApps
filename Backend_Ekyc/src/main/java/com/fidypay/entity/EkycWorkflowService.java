package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EKYC_WORKFLOW_SERVICE")
public class EkycWorkflowService {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EKYC_WORKFLOW_SERVICE_ID")
	private Long ekycWorkflowServiceId;

	@Column(name = "CREATION_DATE", nullable = false)
	private Timestamp creationDate;

	@Column(name = "MERCHANT_ID", nullable = false)
	private Long merchantId;

	@Column(name = "IS_VERIFIED", nullable = false)
	private char isVerified;

	@Column(name = "EKYC_WORKFLOW_ID", nullable = false)
	private Long ekycWorkflowId;

	@Column(name = "SERVICE_UNIQUE_ID", nullable = false)
	private String serviceUniqueId;

	@Column(name = "EKYC_USER_ID", nullable = false)
	private Long ekycUserId;

	@Column(name = "DOCUMENT_RESPONSE", nullable = false)
	private String documentResponse;

	@Column(name = "SERVICE_ID", nullable = false)
	private Long serviceId;

	@Column(name = "SERVICE_NAME", nullable = false)
	private String serviceName;

	@Column(name = "DOCUMENT_ID", nullable = false)
	private String documentId;

	@Column(name = "DOCUMENT_REQUEST", nullable = false)
	private String documentRequest;
	
	@Column(name = "DOCUMENT_JSON", nullable = false)
	private String documentJSon;

    @Column(name = "RE_KYC")
	private Character reKyc;
	
	@Column(name = "TITLE")
	private String title;
	
	@Column(name = "REJECT_REASON")
	private String rejectReason;
	
	
	public Long getEkycWorkflowServiceId() {
		return ekycWorkflowServiceId;
	}

	public void setEkycWorkflowServiceId(Long ekycWorkflowServiceId) {
		this.ekycWorkflowServiceId = ekycWorkflowServiceId;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public char getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(char isVerified) {
		this.isVerified = isVerified;
	}

	public Long getEkycWorkflowId() {
		return ekycWorkflowId;
	}

	public void setEkycWorkflowId(Long ekycWorkflowId) {
		this.ekycWorkflowId = ekycWorkflowId;
	}

	public Long getEkycUserId() {
		return ekycUserId;
	}

	public void setEkycUserId(Long ekycUserId) {
		this.ekycUserId = ekycUserId;
	}

	public String getDocumentResponse() {
		return documentResponse;
	}

	public void setDocumentResponse(String documentResponse) {
		this.documentResponse = documentResponse;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getDocumentRequest() {
		return documentRequest;
	}

	public void setDocumentRequest(String documentRequest) {
		this.documentRequest = documentRequest;
	}

	public String getServiceUniqueId() {
		return serviceUniqueId;
	}

	public void setServiceUniqueId(String serviceUniqueId) {
		this.serviceUniqueId = serviceUniqueId;
	}

	public String getDocumentJSon() {
		return documentJSon;
	}

	public void setDocumentJSon(String documentJSon) {
		this.documentJSon = documentJSon;
	}

	public Character getReKyc() {
		return reKyc;
	}

	public void setReKyc(Character reKyc) {
		this.reKyc = reKyc;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	
	
	
	
	
}
