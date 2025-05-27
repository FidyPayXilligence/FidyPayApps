package com.fidypay.request;

import org.springframework.beans.factory.annotation.Value;

public class EkycUserRequest {

	@Value("10")
	private Integer pageSize;
	@Value("0")
	private Integer pageNo;

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
