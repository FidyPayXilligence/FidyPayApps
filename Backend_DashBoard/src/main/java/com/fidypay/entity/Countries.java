package com.fidypay.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "COUNTRIES")
public class Countries {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name="ISO2")
	private String iso;
	
	@Column(name="NAME")
	private String name;

	
	@Column(name="PHONECODE")
	private String phonecode;


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getIso() {
		return iso;
	}


	public void setIso(String iso) {
		this.iso = iso;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPhonecode() {
		return phonecode;
	}


	public void setPhonecode(String phonecode) {
		this.phonecode = phonecode;
	}


	
	
	
	
	
}
