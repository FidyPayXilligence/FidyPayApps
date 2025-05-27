package com.fidypay.response;

public class EkycReconResponse {

	private String reconciliationDate;

	private String fromDate;

	private String toDate;

	private long merchantId;

	private long totalTrxn;

	private String totalAmount;

	private long serviceProviderId;

	private long merchantServiceId;

	private String serviceName;

	private long reconciliationTotalTrxn;

	private Double reconciliationTotalAmount;

	public String getReconciliationDate() {
		return reconciliationDate;
	}

	public void setReconciliationDate(String reconciliationDate) {
		this.reconciliationDate = reconciliationDate;
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

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public long getTotalTrxn() {
		return totalTrxn;
	}

	public void setTotalTrxn(long totalTrxn) {
		this.totalTrxn = totalTrxn;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public long getServiceProviderId() {
		return serviceProviderId;
	}

	public void setServiceProviderId(long serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}

	public long getMerchantServiceId() {
		return merchantServiceId;
	}

	public void setMerchantServiceId(long merchantServiceId) {
		this.merchantServiceId = merchantServiceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public long getReconciliationTotalTrxn() {
		return reconciliationTotalTrxn;
	}

	public void setReconciliationTotalTrxn(long reconciliationTotalTrxn) {
		this.reconciliationTotalTrxn = reconciliationTotalTrxn;
	}

	public Double getReconciliationTotalAmount() {
		return reconciliationTotalAmount;
	}

	public void setReconciliationTotalAmount(Double reconciliationTotalAmount) {
		this.reconciliationTotalAmount = reconciliationTotalAmount;
	}
	
	
	
	
	

}
