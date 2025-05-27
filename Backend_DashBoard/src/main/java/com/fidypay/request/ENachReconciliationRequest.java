package com.fidypay.request;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Value;

public class ENachReconciliationRequest {

	@NotBlank(message = "start date can not be blank")
	private String fromDate;

	@NotBlank(message = "end date can not be blank")
	private String toDate;

	

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

	
}
