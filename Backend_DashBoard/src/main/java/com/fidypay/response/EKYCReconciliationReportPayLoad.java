package com.fidypay.response;

public class EKYCReconciliationReportPayLoad {

	private int sNo;

	private String reconciliationDate;

	private String fromDate;

	private String toDate;

	private String serviceName;

	private long reconciliationTotalTrxn;

	private Double reconciliationTotalAmount;

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

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
