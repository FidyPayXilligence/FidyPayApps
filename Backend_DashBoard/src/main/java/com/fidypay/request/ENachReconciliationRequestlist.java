package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Value;

public class ENachReconciliationRequestlist {

	@NotBlank(message = "fromDate can not be blank")
	@Pattern(regexp = "([0-9]{4})-([0-9]{2})-([0-9]{2})", message = "Invalid fromDate format. The format should be yyyy-mm-dd.")
	private String fromDate;

	@NotBlank(message = "toDate can not be blank")
	@Pattern(regexp = "([0-9]{4})-([0-9]{2})-([0-9]{2})", message = "Invalid toDate format. The format should be yyyy-mm-dd.")
	private String toDate;

	
	private Integer pageNo;

	private Integer pageSize;

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

	
	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

}
