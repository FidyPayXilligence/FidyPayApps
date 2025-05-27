package com.fidypay.request;

import java.util.ArrayList;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class EkycWorkflowRequest {

	@Valid
	private ArrayList<EkycServicesRequest> services;

	@Size(min = 2, max = 30, message = "workflowName size should be 2 to 30.")
	@NotEmpty(message = "workflowName can not be empty.")
	
	private String workflowName;

	@NotEmpty(message = "days can not be empty.")
	//@Pattern(regexp = "0|90|180|360", message = "Please Pass 0,90,180 and 360 on days parameter")
	@Pattern(regexp = "[0-9]+", message = "days should be numeric")
	private String days;

	@Size(min = 2, max = 150, message = "description size should be 2 to 150.")
	@NotEmpty(message = "description can not be empty.")
	private String description;

	@Pattern(regexp = "Individual KYC|Business KYC|Bank KYC|Custom KYC", message = "Invalid kycType, Valid kyc types are : Individual KYC, Business KYC, Bank KYC, Custom KYC.")
	@NotEmpty(message = "kycType can not be empty.")
	private String kycType;

	public ArrayList<EkycServicesRequest> getServices() {
		return services;
	}

	public void setServices(ArrayList<EkycServicesRequest> services) {
		this.services = services;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKycType() {
		return kycType;
	}

	public void setKycType(String kycType) {
		this.kycType = kycType;
	}

//	public ArrayList<EkycServicesRequest> Services() {
//		return services;
//	}
//
//	public void setServices(ArrayList<EkycServicesRequest> services) {
//		this.services = services;
//	}

	
	
	
	
	
}
