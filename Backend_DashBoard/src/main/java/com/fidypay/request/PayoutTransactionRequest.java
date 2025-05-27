package com.fidypay.request;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Value;

public class PayoutTransactionRequest {

	@NotBlank(message = "start date can not be blank")
	private String startDate;
	
	@NotBlank(message = "end date can not be blank")
	private String endDate;
	
	@Value("10")
	private Integer pageSize;
	
	@Value("0")
	private Integer pageNo;
	
	@Value("0")
	private Long merchantServiceId;
	
	@Value("0")
	private Long statusId;
	
	@Value("")
	private String startTime;
	
	@Value("")
	private String endTime;

	

	public Long getStatusId() {
		return statusId;
	}

	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

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



	
	public Long getMerchantServiceId() {
		return merchantServiceId;
	}

	public void setMerchantServiceId(Long merchantServiceId) {
		this.merchantServiceId = merchantServiceId;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

}
