package com.fidypay.wallet;

public class WalletRequest {
	private long merchantId;
	private double amount;
	private String transactionType; // debit or credit
	private String description;
	private String serviceName;
	private long coreTransactionId;
	private String transactionRefId;
	private String merchantTransactionRefId;
	private String walletTxnRefNo;

	public WalletRequest() {
	}

	public WalletRequest(long merchantId, double amount, String transactionType, String description, String serviceName,
			long coreTransactionId, String transactionRefId, String merchantTransactionRefId, String walletTxnRefNo) {
		this.merchantId = merchantId;
		this.amount = amount;
		this.transactionType = transactionType;
		this.description = description;
		this.serviceName = serviceName;
		this.coreTransactionId = coreTransactionId;
		this.transactionRefId = transactionRefId;
		this.merchantTransactionRefId = merchantTransactionRefId;
		this.walletTxnRefNo = walletTxnRefNo;
	}

	public String getWalletTxnRefNo() {
		return walletTxnRefNo;
	}

	public void setWalletTxnRefNo(String walletTxnRefNo) {
		this.walletTxnRefNo = walletTxnRefNo;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public long getCoreTransactionId() {
		return coreTransactionId;
	}

	public void setCoreTransactionId(long coreTransactionId) {
		this.coreTransactionId = coreTransactionId;
	}

	public String getTransactionRefId() {
		return transactionRefId;
	}

	public void setTransactionRefId(String transactionRefId) {
		this.transactionRefId = transactionRefId;
	}

	public String getMerchantTransactionRefId() {
		return merchantTransactionRefId;
	}

	public void setMerchantTransactionRefId(String merchantTransactionRefId) {
		this.merchantTransactionRefId = merchantTransactionRefId;
	}

}
