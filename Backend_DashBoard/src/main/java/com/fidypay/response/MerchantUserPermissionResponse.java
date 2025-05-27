package com.fidypay.response;

public class MerchantUserPermissionResponse {

	private long merchantUserPermissionId;

	private long merchnatUserId;

	private long merchantId;

	private String payout;

	private String payin;

	private String eKyc;

	private String bbps;

	private String pg;

	private String eNach;

	private String isActive;

	private String administrator;

	public String getAdministrator() {
		return administrator;
	}

	public void setAdministrator(String administrator) {
		this.administrator = administrator;
	}

	public long getMerchantUserPermissionId() {
		return merchantUserPermissionId;
	}

	public void setMerchantUserPermissionId(long merchantUserPermissionId) {
		this.merchantUserPermissionId = merchantUserPermissionId;
	}

	public long getMerchnatUserId() {
		return merchnatUserId;
	}

	public void setMerchnatUserId(long merchnatUserId) {
		this.merchnatUserId = merchnatUserId;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
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

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

}
