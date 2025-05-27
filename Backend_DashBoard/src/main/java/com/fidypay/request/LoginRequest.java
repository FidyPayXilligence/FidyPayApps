package com.fidypay.request;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Value;

public class LoginRequest {


	@NotBlank(message = "start date can not be blank")
	private String startDate;
	@NotBlank(message = "end date can not be blank")
	private String endDate;
	@Value("10")
	private Integer pageSize;
	@Value("0")
	private Integer pageNo;
	@Value("0")
	private Long  merchantId;

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
	public Long getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}
	
}
