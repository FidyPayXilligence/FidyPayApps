package com.fidypay.response;

public class CitiesListPayLoad {

	
     private Long id;
     
	private String name;
	
	private Long stateId;
	
	private String stateCode;
	
	private Long countryId;

	private char countryCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getStateId() {
		return stateId;
	}

	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	public char getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(char countryCode) {
		this.countryCode = countryCode;
	}
	
	
}
