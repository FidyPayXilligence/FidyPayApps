package com.fidypay.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "CRED_DETAILS")
@EntityListeners(AuditingEntityListener.class)
public class CredDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;
	
	@Column(name = "NAME")
	private String name;

	@Column(name = "ACCESS_KEY")
	private String accesskey;
	
	@Column(name = "SECRET_ACCESS_KEY")
	private String secretAccesskey;
	
	@Column(name = "OTHER")
	private String other;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccesskey() {
		return accesskey;
	}

	public void setAccesskey(String accesskey) {
		this.accesskey = accesskey;
	}

	public String getSecretAccesskey() {
		return secretAccesskey;
	}

	public void setSecretAccesskey(String secretAccesskey) {
		this.secretAccesskey = secretAccesskey;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}
	
	
	
	

}
