package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Value;

public class EkycReconciliationRequest {

	@NotBlank(message = "start date can not be blank")
	@Pattern(regexp = "([0-9]{4})-([0-9]{2})-([0-9]{2})", message = "Invalid fromDate format. The format should be yyyy-mm-dd.")
	private String fromDate;

	@NotBlank(message = "end date can not be blank")
	@Pattern(regexp = "([0-9]{4})-([0-9]{2})-([0-9]{2})", message = "Invalid toDate format. The format should be yyyy-mm-dd.")
	private String toDate;
	
	
	@Value("")
	private String startTime;

	@Value("")
	private String endTime;

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

}
