package com.fidypay.response;

public class TReportPayload {

	private Long reponseStatus;
	private String date;
	private String month;
	private String sourceType;
	private String serviceIdentifier;
	private double amount;
	private String transactionStatus;
	private String transactionRefrenceId;
	private String sourceName;
	private String trxnTime;
	private String year;
	private String responseMessage;
	private String utr;

	public String getUtr() {
		return utr;
	}

	public void setUtr(String utr) {
		this.utr = utr;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public Long getReponseStatus() {
		return reponseStatus;
	}

	public void setReponseStatus(Long reponseStatus) {
		this.reponseStatus = reponseStatus;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getServiceIdentifier() {
		return serviceIdentifier;
	}

	public void setServiceIdentifier(String serviceIdentifier) {
		this.serviceIdentifier = serviceIdentifier;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getTransactionRefrenceId() {
		return transactionRefrenceId;
	}

	public void setTransactionRefrenceId(String transactionRefrenceId) {
		this.transactionRefrenceId = transactionRefrenceId;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getTrxnTime() {
		return trxnTime;
	}

	public void setTrxnTime(String trxnTime) {
		this.trxnTime = trxnTime;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

}
