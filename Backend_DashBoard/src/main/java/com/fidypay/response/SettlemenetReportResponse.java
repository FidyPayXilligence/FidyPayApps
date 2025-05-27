package com.fidypay.response;

public class SettlemenetReportResponse {

	private int sNo;
	private Long merchantSettlementId;
	private Long merchantId;
	private String settlementDate;
	private String trxnId;
	private char isSettlementVerified;
//	private double settlementAmount;
	private String amount;
	private String merchantBussinessName;
	private String subMerchantBusinessName;
	private String vpa;
	private String settlementType;
	private String subMerchantIfscCode;	
	private String subMerchantBankName;
    private String subMerchantBankAccount;
//    private double walletBalance;
	private String fromDate;
	private String toDate;
	private int totalTransactions;
	private String status;

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

	public Long getMerchantSettlementId() {
		return merchantSettlementId;
	}

	public void setMerchantSettlementId(Long merchantSettlementId) {
		this.merchantSettlementId = merchantSettlementId;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public String getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getTrxnId() {
		return trxnId;
	}

	public void setTrxnId(String trxnId) {
		this.trxnId = trxnId;
	}

	public char getIsSettlementVerified() {
		return isSettlementVerified;
	}

	public void setIsSettlementVerified(char isSettlementVerified) {
		this.isSettlementVerified = isSettlementVerified;
	}

	

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getMerchantBussinessName() {
		return merchantBussinessName;
	}

	public void setMerchantBussinessName(String merchantBussinessName) {
		this.merchantBussinessName = merchantBussinessName;
	}

	public String getSubMerchantBusinessName() {
		return subMerchantBusinessName;
	}

	public void setSubMerchantBusinessName(String subMerchantBusinessName) {
		this.subMerchantBusinessName = subMerchantBusinessName;
	}

	public String getVpa() {
		return vpa;
	}

	public void setVpa(String vpa) {
		this.vpa = vpa;
	}

	public String getSettlementType() {
		return settlementType;
	}

	public void setSettlementType(String settlementType) {
		this.settlementType = settlementType;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public int getTotalTransactions() {
		return totalTransactions;
	}

	public void setTotalTransactions(int totalTransactions) {
		this.totalTransactions = totalTransactions;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubMerchantIfscCode() {
		return subMerchantIfscCode;
	}

	public void setSubMerchantIfscCode(String subMerchantIfscCode) {
		this.subMerchantIfscCode = subMerchantIfscCode;
	}

	public String getSubMerchantBankName() {
		return subMerchantBankName;
	}

	public void setSubMerchantBankName(String subMerchantBankName) {
		this.subMerchantBankName = subMerchantBankName;
	}

	public String getSubMerchantBankAccount() {
		return subMerchantBankAccount;
	}

	public void setSubMerchantBankAccount(String subMerchantBankAccount) {
		this.subMerchantBankAccount = subMerchantBankAccount;
	}
	
	

}
