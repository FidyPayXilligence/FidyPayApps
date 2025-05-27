package com.fidypay.response;

public class SettlementReportPayload {

	private int sNo;
	private String settlementDate;
	private String settlementFromDate;
	private String settlementToDate;
	private String submerchantBusinessName;
	private String submerchantVpa;
	private String utr;
	private double amount;
	private String status;
	private long totalTransaction;

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

	public String getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getSubmerchantBusinessName() {
		return submerchantBusinessName;
	}

	public void setSubmerchantBusinessName(String submerchantBusinessName) {
		this.submerchantBusinessName = submerchantBusinessName;
	}

	public String getSubmerchantVpa() {
		return submerchantVpa;
	}

	public void setSubmerchantVpa(String submerchantVpa) {
		this.submerchantVpa = submerchantVpa;
	}

	public String getUtr() {
		return utr;
	}

	public void setUtr(String utr) {
		this.utr = utr;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getSettlementFromDate() {
		return settlementFromDate;
	}

	public void setSettlementFromDate(String settlementFromDate) {
		this.settlementFromDate = settlementFromDate;
	}

	public String getSettlementToDate() {
		return settlementToDate;
	}

	public void setSettlementToDate(String settlementToDate) {
		this.settlementToDate = settlementToDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getTotalTransaction() {
		return totalTransaction;
	}

	public void setTotalTransaction(long totalTransaction) {
		this.totalTransaction = totalTransaction;
	}

}
