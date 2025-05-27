package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class PWAAppliactionLoginRequest {

	@Pattern(regexp = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$", message = "Please enter valid phone number")
	@NotBlank(message = "phone number can not be empty")
	@Size(min = 10, max = 10, message = "Please enter your 10 digit phone number")
	private String mobileNo;
	private String hardwareId;
	private String deviceModel;
	private String version;
	private String fireBaseToken;

	public PWAAppliactionLoginRequest() {
	}

	public PWAAppliactionLoginRequest(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getHardwareId() {
		return hardwareId;
	}

	public void setHardwareId(String hardwareId) {
		this.hardwareId = hardwareId;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFireBaseToken() {
		return fireBaseToken;
	}

	public void setFireBaseToken(String fireBaseToken) {
		this.fireBaseToken = fireBaseToken;
	}

}
