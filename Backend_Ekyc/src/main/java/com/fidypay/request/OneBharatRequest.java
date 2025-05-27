package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class OneBharatRequest {

	@Pattern(regexp = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$", message = "Please enter your 10 digit customerMobile")
	@NotBlank(message = "customerMobile cannot be empty")
	private String customerMobile;

	@NotBlank(message = "Customer name cannot be empty")
	@Size(min = 1, max = 30, message = "Please enter customerName between 1 to 30 alphabets")
	@Pattern(regexp = "^[a-zA-Z\\s]+$", message = "customerName should be alphabets")
	private String customerName;

	@NotBlank(message = "initiatorName cannot be empty")
	@Size(min = 1, max = 30, message = "Please enter initiatorName between 1 to 30 alphabets")
	@Pattern(regexp = "^[a-zA-Z\\s]+$", message = "initiatorName should be alphabets")
	private String initiatorName;

	@Pattern(regexp = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$", message = "Please enter your 10 digit initiatorMobile")
	@NotBlank(message = "initiatorMobile cannot be empty")
	private String initiatorMobile;

	public String getCustomerMobile() {
		return customerMobile;
	}

	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getInitiatorName() {
		return initiatorName;
	}

	public void setInitiatorName(String initiatorName) {
		this.initiatorName = initiatorName;
	}

	public String getInitiatorMobile() {
		return initiatorMobile;
	}

	public void setInitiatorMobile(String initiatorMobile) {
		this.initiatorMobile = initiatorMobile;
	}

}
