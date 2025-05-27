package com.fidypay.request;

import java.util.ArrayList;

import javax.validation.Valid;

public class EkycUpdateWorkflowRequest {

	private long ekycWorkflowId;

	private String workflowName;

	private String days;

	private String description;
	
	
	@Valid
	private ArrayList<EkycServicesRequest> services;


	public long getEkycWorkflowId() {
		return ekycWorkflowId;
	}

	public void setEkycWorkflowId(long ekycWorkflowId) {
		this.ekycWorkflowId = ekycWorkflowId;
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

	public ArrayList<EkycServicesRequest> getServices() {
		return services;
	}

	public void setServices(ArrayList<EkycServicesRequest> services) {
		this.services = services;
	}

	
	
}
