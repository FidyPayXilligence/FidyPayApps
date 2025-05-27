package com.fidypay.request;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Value;

public class BBPSTransactionHistoryRequest {

	@NotBlank(message = "start date can not be blank")
	private String startDate;

	@NotBlank(message = "end date can not be blank")
	private String endDate;

	@Value("10")
	private Integer pageSize;

	@Value("0")
	private Integer pageNo;

	private long merchantUserId;

	public long getMerchantUserId() {
		return merchantUserId;
	}

	public void setMerchantUserId(long merchantUserId) {
		this.merchantUserId = merchantUserId;
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
