package com.fidypay.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "STATES")
public class States {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name="NAME")
	private String name;

	@Column(name = "COUNTRY_ID")
	private Long countryId;
	
	@Column(name="COUNTRY_CODE")
	private char countryCode;
	
	@Column(name="ISO2")
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
