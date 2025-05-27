package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LOGIN_DETAILS")
public class LoginDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "LOGIN_DETAILS_ID")
	private Long logInDetailsId;

	@Column(name = "MERCHANT_ID")
	private Long merchantId;

	@Column(name = "LOGIN_TIME")
	private String logInTime;

	@Column(name = "LOGOUT_TIME")
	private String logOutTime;

	@Column(name = "DATE")
	private Timestamp date;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "LOGIN_ID")
	private String logInid;

	public String getLogInid() {
		return logInid;
	}

	public void setLoginid(String logInid) {
		this.logInid = logInid;
	}

	public Long getLogInDetailsId() {
		return logInDetailsId;
	}

	public void setLogInDetailsId(Long logInDetailsId) {
		this.logInDetailsId = logInDetailsId;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public String getLogInTime() {
		return logInTime;
	}

	public void setLogInTime(String logInTime) {
		this.logInTime = logInTime;
	}

	public String getLogOutTime() {
		return logOutTime;
	}

	public void setLogOutTime(String logOutTime) {
		this.logOutTime = logOutTime;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
