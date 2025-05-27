package com.fidypay.response;

public class ServiceWiseTransactionResponse {

	private Integer sNo;
	private String servicename;
	private Integer totalTransaction;
	private String totalamount;
	private Integer successTransaction;
	private String successAmount;
	private Integer pendingTransaction;
	private String pendingAmount;
	private Integer failedTransaction;
	private String failedAmount;
	private Integer reversedTransaction;
	private String reversedAmount;
	private Integer cancelTransaction;
	private String cancelAmount;

	public Integer getsNo() {
		return sNo;
	}

	public void setsNo(Integer sNo) {
		this.sNo = sNo;
	}

	public String getServicename() {
		return servicename;
	}

	public void setServicename(String servicename) {
		this.servicename = servicename;
	}

	public Integer getTotalTransaction() {
		return totalTransaction;
	}

	public void setTotalTransaction(Integer totalTransaction) {
		this.totalTransaction = totalTransaction;
	}

	public String getTotalamount() {
		return totalamount;
	}

	public void setTotalamount(String totalamount) {
		this.totalamount = totalamount;
	}

	public Integer getSuccessTransaction() {
		return successTransaction;
	}

	public void setSuccessTransaction(Integer successTransaction) {
		this.successTransaction = successTransaction;
	}

	public String getSuccessAmount() {
		return successAmount;
	}

	public void setSuccessAmount(String successAmount) {
		this.successAmount = successAmount;
	}

	public Integer getPendingTransaction() {
		return pendingTransaction;
	}

	public void setPendingTransaction(Integer pendingTransaction) {
		this.pendingTransaction = pendingTransaction;
	}

	public String getPendingAmount() {
		return pendingAmount;
	}

	public void setPendingAmount(String pendingAmount) {
		this.pendingAmount = pendingAmount;
	}

	public Integer getFailedTransaction() {
		return failedTransaction;
	}

	public void setFailedTransaction(Integer failedTransaction) {
		this.failedTransaction = failedTransaction;
	}

	public String getFailedAmount() {
		return failedAmount;
	}

	public void setFailedAmount(String failedAmount) {
		this.failedAmount = failedAmount;
	}

	public Integer getReversedTransaction() {
		return reversedTransaction;
	}

	public void setReversedTransaction(Integer reversedTransaction) {
		this.reversedTransaction = reversedTransaction;
	}

	public String getReversedAmount() {
		return reversedAmount;
	}

	public void setReversedAmount(String reversedAmount) {
		this.reversedAmount = reversedAmount;
	}

	public Integer getCancelTransaction() {
		return cancelTransaction;
	}

	public void setCancelTransaction(Integer cancelTransaction) {
		this.cancelTransaction = cancelTransaction;
	}

	public String getCancelAmount() {
		return cancelAmount;
	}

	public void setCancelAmount(String cancelAmount) {
		this.cancelAmount = cancelAmount;
	}
}
