package com.fidypay.request;

public class EKycWorkFlowTempRequest {

	private String startDate;
	private String endDate;
	private String ekycWorkflowId;
	
	private int pageNo=0;
	
	private int pageSize=10;
	
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
	public String getEkycWorkflowId() {
		return ekycWorkflowId;
	}
	public void setEkycWorkflowId(String ekycWorkflowId) {
		this.ekycWorkflowId = ekycWorkflowId;
	}
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
