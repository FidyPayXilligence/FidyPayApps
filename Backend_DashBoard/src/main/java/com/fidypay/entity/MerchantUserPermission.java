package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MERCHANT_USER_PERMISSION")
public class MerchantUserPermission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MERCHANT_USER_PERMISSION_ID")
	private long merchantUserPermissionId;

	@Column(name = "MERCHANT_USER_ID")
	private long merchantUserId;

	@Column(name = "MERCHANT_ID")
	private long merchantId;

	@Column(name = "DATE")
	private Timestamp date;

	@Column(name = "PAYOUT")
	private String payout;

	@Column(name = "PAYIN")
	private String payin;

	@Column(name = "EKYC")
	private String eKyc;

	@Column(name = "BBPS")
	private String bbps;

	@Column(name = "PG")
	private String pg;

	@Column(name = "ENACH")
	private String eNach;

	@Column(name = "ADMINISTRATOR")
	private String administrator;

	@Column(name = "IS_ACTIVE")
	private char isActive;

	public long getMerchantUserPermissionId() {
		return merchantUserPermissionId;
	}

	public void setMerchantUserPermissionId(long merchantUserPermissionId) {
		this.merchantUserPermissionId = merchantUserPermissionId;
	}

	public long getMerchantUserId() {
		return merchantUserId;
	}

	public void setMerchantUserId(long merchantUserId) {
		this.merchantUserId = merchantUserId;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getPayout() {
		return payout;
	}

	public void setPayout(String payout) {
		this.payout = payout;
	}

	public String getPayin() {
		return payin;
	}

	public void setPayin(String payin) {
		this.payin = payin;
	}

	public String geteKyc() {
		return eKyc;
	}

	public void seteKyc(String eKyc) {
		this.eKyc = eKyc;
	}

	public String getBbps() {
		return bbps;
	}

	public void setBbps(String bbps) {
		this.bbps = bbps;
	}

	public String getPg() {
		return pg;
	}

	public void setPg(String pg) {
		this.pg = pg;
	}

	public String geteNach() {
		return eNach;
	}

	public void seteNach(String eNach) {
		this.eNach = eNach;
	}

	public char getIsActive() {
		return isActive;
	}

	public void setIsActive(char isActive) {
		this.isActive = isActive;
	}

	public String getAdministrator() {
		return administrator;
	}

	public void setAdministrator(String administrator) {
		this.administrator = administrator;
	}

}
