package com.fidypay.response;

public class PayoutTransactionResponse {

	private int sNo;
	private String bankAccountKey;
	private String bankTransactionIdentification;
	private String transactionStatus;
	private String transactionDate;
	private String merchantTransactionRefId;
	private String transactionAmount;
	private String debitorAcountNumber;
	private String debitorIfsc;
	private String creditorAccountNumber;
	private String creditorIfsc;
	private String creditorName;
	private String creditorEmail;
	private String creditorMobile;
	private String transactionType;
	private String utr;
	private String bankSideStatus;
	private String isReconcile;
	private String isSettled;
	private String serviceName;
	private String trxnRefId;

	
	
	
	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

	public PayoutTransactionResponse() {
	}

	public String getIsReconcile() {
		return isReconcile;
	}

	public void setIsReconcile(String isReconcile) {
		this.isReconcile = isReconcile;
	}

	public String getIsSettled() {
		return isSettled;
	}

	public void setIsSettled(String isSettled) {
		this.isSettled = isSettled;
	}

	public String getBankAccountKey() {
		return bankAccountKey;
	}

	public void setBankAccountKey(String bankAccountKey) {
		this.bankAccountKey = bankAccountKey;
	}

	public String getBankTransactionIdentification() {
		return bankTransactionIdentification;
	}

	public void setBankTransactionIdentification(String bankTransactionIdentification) {
		this.bankTransactionIdentification = bankTransactionIdentification;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getMerchantTransactionRefId() {
		return merchantTransactionRefId;
	}

	public void setMerchantTransactionRefId(String merchantTransactionRefId) {
		this.merchantTransactionRefId = merchantTransactionRefId;
	}

	public String getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(String transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getDebitorAcountNumber() {
		return debitorAcountNumber;
	}

	public void setDebitorAcountNumber(String debitorAcountNumber) {
		this.debitorAcountNumber = debitorAcountNumber;
	}

	public String getDebitorIfsc() {
		return debitorIfsc;
	}

	public void setDebitorIfsc(String debitorIfsc) {
		this.debitorIfsc = debitorIfsc;
	}

	public String getCreditorAccountNumber() {
		return creditorAccountNumber;
	}

	public void setCreditorAccountNumber(String creditorAccountNumber) {
		this.creditorAccountNumber = creditorAccountNumber;
	}

	public String getCreditorIfsc() {
		return creditorIfsc;
	}

	public void setCreditorIfsc(String creditorIfsc) {
		this.creditorIfsc = creditorIfsc;
	}

	public String getCreditorName() {
		return creditorName;
	}

	public void setCreditorName(String creditorName) {
		this.creditorName = creditorName;
	}

	public String getCreditorEmail() {
		return creditorEmail;
	}

	public void setCreditorEmail(String creditorEmail) {
		this.creditorEmail = creditorEmail;
	}

	public String getCreditorMobile() {
		return creditorMobile;
	}

	public void setCreditorMobile(String creditorMobile) {
		this.creditorMobile = creditorMobile;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getUtr() {
		return utr;
	}

	public void setUtr(String utr) {
		this.utr = utr;
	}

	public String getBankSideStatus() {
		return bankSideStatus;
	}

	public void setBankSideStatus(String bankSideStatus) {
		this.bankSideStatus = bankSideStatus;
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

}
