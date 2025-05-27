package com.fidypay.dto;

/*
 * This class is for IFSC Code Response.
 */

public class IfscCodeResponse {

	private String branch;
	private String bank;
	private String ifsc;
	private String code;
	private String status;
	private String timestamp;

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public static IfscCodeResponse getIfscCodeResponse(IfscCodeResponse ifscCodeResponse) {
		ifscCodeResponse.setBank(ifscCodeResponse.getBank());
		ifscCodeResponse.setBranch(ifscCodeResponse.getBranch());
		ifscCodeResponse.setCode(ifscCodeResponse.getCode());
		ifscCodeResponse.setIfsc(ifscCodeResponse.getIfsc());
		ifscCodeResponse.setStatus(ifscCodeResponse.getStatus());
		return ifscCodeResponse;
	}
}
