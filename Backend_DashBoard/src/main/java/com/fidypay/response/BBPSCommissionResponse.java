package com.fidypay.response;

import java.sql.Timestamp;

public class BBPSCommissionResponse {

	private int sNo;

	private String reconciliationDate;

	private String reconciliationDetails;

	private Double reconTotalAmount;

	private Double reconSettlementAmount;

	private String recoAmountGst;

	private String recoAmountTds;

	private Character isVerified;

	private int reconTotalTransactionCount;

	private String fromDate;

	private String toDate;

	private String serviceName;

	private Long merchantServiceId;

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

	public String getReconciliationDetails() {
		return reconciliationDetails;
	}

	public void setReconciliationDetails(String reconciliationDetails) {
		this.reconciliationDetails = reconciliationDetails;
	}

	public Double getReconTotalAmount() {
		return reconTotalAmount;
	}

	public void setReconTotalAmount(Double reconTotalAmount) {
		this.reconTotalAmount = reconTotalAmount;
	}

	public Double getReconSettlementAmount() {
		return reconSettlementAmount;
	}

	public void setReconSettlementAmount(Double reconSettlementAmount) {
		this.reconSettlementAmount = reconSettlementAmount;
	}

	public String getRecoAmountGst() {
		return recoAmountGst;
	}

	public void setRecoAmountGst(String recoAmountGst) {
		this.recoAmountGst = recoAmountGst;
	}

	public String getRecoAmountTds() {
		return recoAmountTds;
	}

	public void setRecoAmountTds(String recoAmountTds) {
		this.recoAmountTds = recoAmountTds;
	}

	public Character getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Character isVerified) {
		this.isVerified = isVerified;
	}

	public int getReconTotalTransactionCount() {
		return reconTotalTransactionCount;
	}

	public void setReconTotalTransactionCount(int reconTotalTransactionCount) {
		this.reconTotalTransactionCount = reconTotalTransactionCount;
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

	public Long getMerchantServiceId() {
		return merchantServiceId;
	}

	public void setMerchantServiceId(Long merchantServiceId) {
		this.merchantServiceId = merchantServiceId;
	}

}
