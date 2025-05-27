package com.fidypay.dto;

public class PANApi {

	private String apiname;
	private String serviceId;
	private String flag;
	private String title;
	private String charge;

	public PANApi(String apiname, String serviceId, String flag, String title, String charge) {
		this.apiname = apiname;
		this.serviceId = serviceId;
		this.flag = flag;
		this.title = title;
		this.charge = charge;
	}

	public String getApiname() {
		return apiname;
	}

	public void setApiname(String apiname) {
		this.apiname = apiname;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

}
