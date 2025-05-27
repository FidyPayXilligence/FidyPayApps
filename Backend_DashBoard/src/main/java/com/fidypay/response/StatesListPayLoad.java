package com.fidypay.response;

import javax.persistence.Column;

public class StatesListPayLoad {

    private Long id;
	
	private String name;

	private Long countryId;
	
	private char countryCode;

	private String iso2;

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

	public String getIso2() {
		return iso2;
	}

	public void setIso2(String iso2) {
		this.iso2 = iso2;
	}
	
	
	
}
