package com.fidypay.request;

public class subMerchantRequest {

	private String subMerchantId;
	private String mobile;
	private String email;
	private String name;
	private String pan;
	private String mccCode;
	private String gst;

	// Getter Methods

	public String getSubMerchantId() {
		return subMerchantId;
	}

	public String getMobile() {
		return mobile;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getPan() {
		return pan;
	}

	public String getMccCode() {
		return mccCode;
	}

	public String getGst() {
		return gst;
	}

	// Setter Methods

	public void setSubMerchantId(String subMerchantId) {
		this.subMerchantId = subMerchantId;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public void setMccCode(String mccCode) {
		this.mccCode = mccCode;
	}

	public void setGst(String gst) {
		this.gst = gst;
	}
}
