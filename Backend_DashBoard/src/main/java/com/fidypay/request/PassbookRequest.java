package com.fidypay.request;

public class PassbookRequest {

	private String startDate;
	private String endDate;
	private int pageNo;
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

}
