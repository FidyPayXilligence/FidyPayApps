package com.fidypay.request;

public class TransactionExcelReportRequest {
	
	
	private String startDate;
	private String endDate;
	private Long serviceId;
	private String startHours;
	private String endHours;
	
	
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

	
	
}
