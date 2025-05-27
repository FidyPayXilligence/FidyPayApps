package com.fidypay.request;

import java.util.ArrayList;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class EkycServicesRequest {

//	@Size(min = 1, max = 4, message = "serviceId size should be 1 to 4.")
//	@Pattern(regexp = "^[0-9]+$", message = "Invalid serviceId, Special characters and alphabets not allowed.")
//	@NotEmpty(message = "serviceId can not be empty.")
//	private String serviceId;

	@Size(min = 2, max = 30, message = "serviceName size should be 2 to 30.")
	@Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Invalid serviceName, Special characters and numbers not allowed.")
	@NotEmpty(message = "serviceName can not be empty.")
	private String serviceName;
	
	
	@NotEmpty(message = "description can not be empty.")
	public String description;


	private String stepName;
	
	private String flag;
	
	private String stepId;
	
	
	public ArrayList<APIRequest> apis;
	
	
//	public String getServiceId() {
//		return serviceId;
//	}
//
//	public void setServiceId(String serviceId) {
//		this.serviceId = serviceId;
//	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public ArrayList<APIRequest> getApis() {
		return apis;
	}

	public void setApis(ArrayList<APIRequest> apis) {
		this.apis = apis;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getStepId() {
		return stepId;
	}

	public void setStepId(String stepId) {
		this.stepId = stepId;
	}

	
	
}
