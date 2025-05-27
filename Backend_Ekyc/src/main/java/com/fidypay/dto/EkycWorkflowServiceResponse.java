package com.fidypay.dto;

public class EkycWorkflowServiceResponse {

	private Long ekycWorkflowServiceId;

	private String creationDate;

	private Long merchantId;

	private char isVerified;

	private Long ekycWorkflowId;

	private String serviceUniqueId;

	private Long ekycUserId;

	private String documentResponse;

	private Long serviceId;

	private String serviceName;

	private String documentId;

	private String documentRequest;
	
	private String documentJson;
	
	private String title;
	
	private String rejectReason;

	public Long getEkycWorkflowServiceId() {
		return ekycWorkflowServiceId;
	}

	public void setEkycWorkflowServiceId(Long ekycWorkflowServiceId) {
		this.ekycWorkflowServiceId = ekycWorkflowServiceId;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
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

	public String getServiceUniqueId() {
		return serviceUniqueId;
	}

	public void setServiceUniqueId(String serviceUniqueId) {
		this.serviceUniqueId = serviceUniqueId;
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

	public String getDocumentJson() {
		return documentJson;
	}

	public void setDocumentJson(String documentJson) {
		this.documentJson = documentJson;
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
