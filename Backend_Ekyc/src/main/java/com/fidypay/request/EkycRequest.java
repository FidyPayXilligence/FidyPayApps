package com.fidypay.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class EkycRequest {

	@Size(min = 2, max = 30, message = "userName size should be 2 to 30.")
	@Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Invalid userName, Special characters not allowed.")
	@NotEmpty(message = "userName can not be empty.")
	private String userName;

	@Size(min = 2, max = 50, message = "userEmail size should be 2 to 50.")
	@NotEmpty(message = "userEmail can not be empty.")
	@Email(regexp = "^(?=.{1,50}@)[a-zA-Z0-9-_]+(\\.[a-zA-Z0-9-_]+)*@[^-][a-zA-Z0-9]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{2,})$",message = "userEmail must be a well-formed")
    private String userEmail;

	@Pattern(regexp = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$", message = "Invalid usermobile, Special characters and alphabets not allowed.")
	@NotEmpty(message = "usermobile can not be empty.")
	@Size(min = 2, max = 10, message = "Invalid usermobile")
	private String usermobile;

	@Pattern(regexp = "^[0-9]+$", message = "Invalid ekycWorkflowId, Special characters and alphabets not allowed.")
	@NotEmpty(message = "ekycWorkflowId can not be empty.")
	private String ekycWorkflowId;

	@Pattern(regexp = "1|2", message = "Invalid Notification Id")
	@NotEmpty(message = "isNotification can not be empty.")
	private String isNotification;
	
	@Pattern(regexp = "^[0-9]+$", message = "Invalid serviceCount, Special characters and alphabets not allowed.")
	@NotEmpty(message = "serviceCount can not be empty.")
	private String serviceCount;

	
	public String getEkycWorkflowId() {
		return ekycWorkflowId;
	}

	public void setEkycWorkflowId(String ekycWorkflowId) {
		this.ekycWorkflowId = ekycWorkflowId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUsermobile() {
		return usermobile;
	}

	public void setUsermobile(String usermobile) {
		this.usermobile = usermobile;
	}

	public String getIsNotification() {
		return isNotification;
	}

	public void setIsNotification(String isNotification) {
		this.isNotification = isNotification;
	}

	public String getServiceCount() {
		return serviceCount;
	}

	public void setServiceCount(String serviceCount) {
		this.serviceCount = serviceCount;
	}

	
	
}
