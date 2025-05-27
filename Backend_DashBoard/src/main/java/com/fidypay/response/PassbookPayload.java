package com.fidypay.response;

public class PassbookPayload {

	private int sNo;
	private String transactionDate;
	private String trxnRefId;
	private String merchantTrxnRefId;
	private String debitAmount;
	private String creditAmount;
	private String amount;
	private String serviceName;
	private String walletTrxnRefId;

	public String getWalletTrxnRefId() {
		return walletTrxnRefId;
	}

	public void setWalletTrxnRefId(String walletTrxnRefId) {
		this.walletTrxnRefId = walletTrxnRefId;
	}

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTrxnRefId() {
		return trxnRefId;
	}

	public void setTrxnRefId(String trxnRefId) {
		this.trxnRefId = trxnRefId;
	}

	public String getMerchantTrxnRefId() {
		return merchantTrxnRefId;
	}

	public void setMerchantTrxnRefId(String merchantTrxnRefId) {
		this.merchantTrxnRefId = merchantTrxnRefId;
	}

	public String getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(String debitAmount) {
		this.debitAmount = debitAmount;
	}

	public String getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(String creditAmount) {
		this.creditAmount = creditAmount;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

}
