package com.fidypay.request;

import org.json.simple.JSONArray;

public class EKycUpdateRequest {
	
	private Long ekycUserId;
	
//	@NotEmpty(message = "servicesJson can not be empty.")
	private JSONArray servicesJson;
	
	public Long getEkycUserId() {
		return ekycUserId;
	}
	public void setEkycUserId(Long ekycUserId) {
		this.ekycUserId = ekycUserId;
	}
	public JSONArray getServicesJson() {
		return servicesJson;
	}
	public void setServicesJson(JSONArray servicesJson) {
		this.servicesJson = servicesJson;
	}
	
	
	

	
	
	
	
	

}
