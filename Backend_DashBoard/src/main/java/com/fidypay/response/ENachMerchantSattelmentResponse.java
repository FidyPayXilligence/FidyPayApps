package com.fidypay.response;

public class ENachMerchantSattelmentResponse {

	private Character isVerfied;
	private String settlementDetails;
	private long merchantId;
	private String settlementAmount;
	private String settlementDate;
	private String fromDate;
	private String toDate;
	private Long merchantServiceId;
	private String totalAmount;
	private String serviceName;
	private Integer totalTransaction;
	private String utr;

	public Character getIsVerfied() {
		return isVerfied;
	}

	public void setIsVerfied(Character isVerfied) {
		this.isVerfied = isVerfied;
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

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
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

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Integer getTotalTransaction() {
		return totalTransaction;
	}

	public void setTotalTransaction(Integer totalTransaction) {
		this.totalTransaction = totalTransaction;
	}

	public String getUtr() {
		return utr;
	}

	public void setUtr(String utr) {
		this.utr = utr;
	}

}
