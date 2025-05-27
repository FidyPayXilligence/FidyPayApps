package com.fidypay.response;

public class FdWealthTxnResponse {

	private int sNo;
	private String wealthTxnId;
	private String date;
	private String merchantTrxnRefId;
	private String customerName;
	private String customerMobile;
	private String customerEmail;
	private String paymentMode;
	private String accountNumber;
	private String ifsc;
	private String uId;
	private String dob;
	private String gender;
	private String investmentAmount;
	private String investmentPeriod;
	private Double interestRate;

	private String panNumber;
	private String paymentTxId;
	private String nomineeDetails;
	private String bankName;
	private String interestPayoutFrequency;
	private String trxnId;
	private String fdResponseId;

	public String getNomineeDetails() {
		return nomineeDetails;
	}

	public void setNomineeDetails(String nomineeDetails) {
		this.nomineeDetails = nomineeDetails;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getInterestPayoutFrequency() {
		return interestPayoutFrequency;
	}

	public void setInterestPayoutFrequency(String interestPayoutFrequency) {
		this.interestPayoutFrequency = interestPayoutFrequency;
	}

	public String getTrxnId() {
		return trxnId;
	}

	public void setTrxnId(String trxnId) {
		this.trxnId = trxnId;
	}

	public String getFdResponseId() {
		return fdResponseId;
	}

	public void setFdResponseId(String fdResponseId) {
		this.fdResponseId = fdResponseId;
	}

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

	public String getWealthTxnId() {
		return wealthTxnId;
	}

	public void setWealthTxnId(String wealthTxnId) {
		this.wealthTxnId = wealthTxnId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMerchantTrxnRefId() {
		return merchantTrxnRefId;
	}

	public void setMerchantTrxnRefId(String merchantTrxnRefId) {
		this.merchantTrxnRefId = merchantTrxnRefId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerMobile() {
		return customerMobile;
	}

	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

	public String getuId() {
		return uId;
	}

	public void setuId(String uId) {
		this.uId = uId;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getInvestmentAmount() {
		return investmentAmount;
	}

	public void setInvestmentAmount(String investmentAmount) {
		this.investmentAmount = investmentAmount;
	}

	public String getInvestmentPeriod() {
		return investmentPeriod;
	}

	public void setInvestmentPeriod(String investmentPeriod) {
		this.investmentPeriod = investmentPeriod;
	}

	public Double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getPaymentTxId() {
		return paymentTxId;
	}

	public void setPaymentTxId(String paymentTxId) {
		this.paymentTxId = paymentTxId;
	}

}
