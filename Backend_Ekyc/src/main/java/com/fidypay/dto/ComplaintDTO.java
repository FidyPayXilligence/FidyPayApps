package com.fidypay.dto;

public class ComplaintDTO {
	
	
	private String complaintType;
	private String description;
	private String disposition;
	private String txnReferenceId;
	private String xchangeId;
	
	
	public String getComplaintType() {
		return complaintType;
	}
	public void setComplaintType(String complaintType) {
		this.complaintType = complaintType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDisposition() {
		return disposition;
	}
	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}
	public String getTxnReferenceId() {
		return txnReferenceId;
	}
	public void setTxnReferenceId(String txnReferenceId) {
		this.txnReferenceId = txnReferenceId;
	}
	public String getXchangeId() {
		return xchangeId;
	}
	public void setXchangeId(String xchangeId) {
		this.xchangeId = xchangeId;
	}


	
}
