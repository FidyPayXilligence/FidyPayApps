package com.fidypay.request;

public class TransactionsReportRequest {

	private String startDate;
	private String endDate;
	private Long serviceId;
	private String startHours;
	private String endHours;
	Long trxnStatusId;
	private String vpa;

	private int pageNo = 1;
	private int pageSize;

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

//	public Long getMerchantId() {
//		return merchantId;
//	}
//	public void setMerchantId(Long merchantId) {
//		this.merchantId = merchantId;
//	}
	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public String getStartHours() {
		return startHours;
	}

	public void setStartHours(String startHours) {
		this.startHours = startHours;
	}

	public String getEndHours() {
		return endHours;
	}

	public void setEndHours(String endHours) {
		this.endHours = endHours;
	}

//	public Pagination getPagination() {
//		return pagination;
//	}
//	public void setPagination(Pagination pagination) {
//		this.pagination = pagination;
//	}
	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Long getTrxnStatusId() {
		return trxnStatusId;
	}

	public void setTrxnStatusId(Long trxnStatusId) {
		this.trxnStatusId = trxnStatusId;
	}

	public String getVpa() {
		return vpa;
	}

	public void setVpa(String vpa) {
		this.vpa = vpa;
	}

}
