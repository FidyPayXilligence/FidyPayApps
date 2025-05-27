package com.fidypay.response;

public class ENachTransactionResponse {

	private int sNo;
	private String eNachTransactionId;
	private String transactionStatus;
	private String transactionDate;
	private String merchantTransactionRefId;
	private String transactionAmount;
	private String isReconcile;
	private String isSettled;
	private String serviceName;
	private String trxnRefId;
	private String mandateId;
	private String customerId;
	private String customerName;
	private String umrnNo;
	private double charges;
	private String requestSource;
	private String bankId;
	private String remark;
	private String debitDate;
	private String customerBankAccountNo;
	private String customerBankIFSC;

	public String getDebitDate() {
		return debitDate;
	}

	public void setDebitDate(String debitDate) {
		this.debitDate = debitDate;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String geteNachTransactionId() {
		return eNachTransactionId;
	}

	public void seteNachTransactionId(String eNachTransactionId) {
		this.eNachTransactionId = eNachTransactionId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getUmrnNo() {
		return umrnNo;
	}

	public void setUmrnNo(String umrnNo) {
		this.umrnNo = umrnNo;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getMandateId() {
		return mandateId;
	}

	public void setMandateId(String mandateId) {
		this.mandateId = mandateId;
	}

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
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

	public double getCharges() {
		return charges;
	}

	public void setCharges(double charges) {
		this.charges = charges;
	}

	public String getRequestSource() {
		return requestSource;
	}

	public void setRequestSource(String requestSource) {
		this.requestSource = requestSource;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCustomerBankAccountNo() {
		return customerBankAccountNo;
	}

	public String getCustomerBankIFSC() {
		return customerBankIFSC;
	}

	public void setCustomerBankAccountNo(String customerBankAccountNo) {
		this.customerBankAccountNo = customerBankAccountNo;
	}

	public void setCustomerBankIFSC(String customerBankIFSC) {
		this.customerBankIFSC = customerBankIFSC;
	}

}
