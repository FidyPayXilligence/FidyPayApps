package com.fidypay.response;

public class TransactionStatusPayload {

	private long transactionStatusId;
	
	private String statusName;

	public long getTransactionStatusId() {
		return transactionStatusId;
	}

	public void setTransactionStatusId(long transactionStatusId) {
		this.transactionStatusId = transactionStatusId;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	
	
	
}
