package com.fidypay.dto;

import java.util.List;
import java.util.Map;

public class KYCTypeListResponse {
	private String code;
	private Map<String, List<KYCService>> kycTypeList;
	private String description;
	private String status;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Map<String, List<KYCService>> getKycTypeList() {
		return kycTypeList;
	}

	public void setKycTypeList(Map<String, List<KYCService>> kycTypeList) {
		this.kycTypeList = kycTypeList;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}

class KYCService {
	private String serviceName;
	private List<KYCApi> apis;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public List<KYCApi> getApis() {
		return apis;
	}

	public void setApis(List<KYCApi> apis) {
		this.apis = apis;
	}
}

class KYCApi {
	private String apiname;
	private String serviceId;
	private String flag;
	private String title;
	private String charge;

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
