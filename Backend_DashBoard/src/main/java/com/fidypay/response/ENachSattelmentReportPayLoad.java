package com.fidypay.response;

public class ENachSattelmentReportPayLoad {

	private int sNo;

	private String isVerfied;

	private String merchantTransactionRefId;

	private String settlementDetails;

	private long merchantId;

	private String settlementAmount;

	private String settlementDate;

	private String fromDate;

	private String toDate;

	private Long merchantServiceId;

	private String amount;

	private Integer totalTransaction;

	private String serviceName;

	private String utr;

	public String getUtr() {
		return utr;
	}

	public void setUtr(String utr) {
		this.utr = utr;
	}

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

	public String getIsVerfied() {
		return isVerfied;
	}

	public void setIsVerfied(String isVerfied) {
		this.isVerfied = isVerfied;
	}

	public String getMerchantTransactionRefId() {
		return merchantTransactionRefId;
	}

	public void setMerchantTransactionRefId(String merchantTransactionRefId) {
		this.merchantTransactionRefId = merchantTransactionRefId;
	}

	public String getSettlementDetails() {
		return settlementDetails;
	}

	public void setSettlementDetails(String settlementDetails) {
		this.settlementDetails = settlementDetails;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public String getSettlementAmount() {
		return settlementAmount;
	}

	public void setSettlementAmount(String settlementAmount) {
		this.settlementAmount = settlementAmount;
	}

	public String getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String startDate) {
		this.fromDate = startDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public Long getMerchantServiceId() {
		return merchantServiceId;
	}

	public void setMerchantServiceId(Long merchantServiceId) {
		this.merchantServiceId = merchantServiceId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public Integer getTotalTransaction() {
		return totalTransaction;
	}

	public void setTotalTransaction(Integer totalTransaction) {
		this.totalTransaction = totalTransaction;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

}
