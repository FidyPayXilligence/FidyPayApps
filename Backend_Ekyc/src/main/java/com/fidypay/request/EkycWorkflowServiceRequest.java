package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class EkycWorkflowServiceRequest {

	@Pattern(regexp = "^[0-9]+$", message = "Invalid ekycUserId, Special characters and alphabets not allowed.")
	@NotEmpty(message = "ekycUserId can not be empty.")
	private String ekycUserId;
	
	@Pattern(regexp = "^[0-9]+$", message = "Invalid serviceId, Special characters and alphabets not allowed.")
	@NotEmpty(message = "serviceId can not be empty.")
	private String serviceId;
	
	@NotBlank(message = "serviceName cannot be empty")
	@Size(min=1,max =50,message = "Please enter serviceName between 1 to 50 alphabets")
	@Pattern(regexp = "^[a-zA-Z\\s]+$", message = "serviceName should be alphabets")
	private String serviceName;
	
	
	@NotBlank(message = "apiResponse cannot be empty")
	private String apiResponse;
	
	
	@NotBlank(message = "url cannot be empty")
	private String url;
	
	
	@NotBlank(message = "title cannot be empty")
	private String title;

	
	
	public String getEkycUserId() {
		return ekycUserId;
	}
	public void setEkycUserId(String ekycUserId) {
		this.ekycUserId = ekycUserId;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getApiResponse() {
		return apiResponse;
	}
	public void setApiResponse(String apiResponse) {
		this.apiResponse = apiResponse;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	
	
}
