package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PGMerchantInfoRequest {

	private String remark;

	@Size(min = 736, max = 800, message = "encryptionKey can not be more than 800 alphaNumeric")
	@NotBlank(message = "encryptionKey can not be empty")
	private String encryptionKey;

	@Size(min = 1, max = 1, message = "index can not be more than 1 numeric")
	@NotBlank(message = "index can not be empty")
	private String index;

	@Size(min = 36, max = 40, message = "saltKey an not be more than 40 Character")
	@NotBlank(message = "saltKey can not be empty")
	private String saltKey;

	@Size(min = 16, max = 20, message = "apiMerchantId an not be more than 20 Character")
	@NotBlank(message = "apiMerchantId can not be empty")
	private String apiMerchantId;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getSaltKey() {
		return saltKey;
	}

	public void setSaltKey(String saltKey) {
		this.saltKey = saltKey;
	}

	public String getApiMerchantId() {
		return apiMerchantId;
	}

	public void setApiMerchantId(String apiMerchantId) {
		this.apiMerchantId = apiMerchantId;
	}

}
