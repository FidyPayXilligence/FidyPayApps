package com.fidypay.dto;

import java.util.List;

public class PANService {
	
	private String serviceName;
	private List<PANApi> apis;
	
	
	private String description;
	 private String flag;
	 private String stepId;
	 private String stepName;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public List<PANApi> getApis() {
		return apis;
	}

	public void setApis(List<PANApi> apis) {
		this.apis = apis;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
	
	
	
}
