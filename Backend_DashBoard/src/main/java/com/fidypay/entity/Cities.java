package com.fidypay.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CITIES")
public class Cities {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="STATE_ID")
	private Long stateId;
	
	@Column(name="STATE_CODE")
	private String stateCode;
	
	@Column(name="COUNTRY_ID")
	private Long countryId;

	@Column(name="COUNTRY_CODE")
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
