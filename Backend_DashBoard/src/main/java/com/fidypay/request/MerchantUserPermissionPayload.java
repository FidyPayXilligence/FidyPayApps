package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class MerchantUserPermissionPayload {

	private UserPermissionPayout payout;
	private UserPermissionPayin payin;
	private UserPermissionEkyc eKyc;
	private UserPermissionEnach eNach;
	private UserPermissionPG pg;
	private UserPermissionBBPS bbps;
	private UserPermissionAdministrator administrator;

	@NotBlank(message = "productName can not be blank")
	@Pattern(regexp = "Payout|EKyc|ENach|Payin|Bbps|PG|Administrator", message = "productName -> please pass Payout,EKyc,ENach,Payin,PG,Bbps or Administrator on productName parameter")
	private String productName;
	private long merchantUserId;

	public long getMerchantUserId() {
		return merchantUserId;
	}

	public void setMerchantUserId(long merchantUserId) {
		this.merchantUserId = merchantUserId;
	}

	public UserPermissionPayout getPayout() {
		return payout;
	}

	public void setPayout(UserPermissionPayout payout) {
		this.payout = payout;
	}

	public UserPermissionPayin getPayin() {
		return payin;
	}

	public void setPayin(UserPermissionPayin payin) {
		this.payin = payin;
	}

	public UserPermissionEkyc geteKyc() {
		return eKyc;
	}

	public void seteKyc(UserPermissionEkyc eKyc) {
		this.eKyc = eKyc;
	}

	public UserPermissionEnach geteNach() {
		return eNach;
	}

	public void seteNach(UserPermissionEnach eNach) {
		this.eNach = eNach;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public UserPermissionPG getPg() {
		return pg;
	}

	public void setPg(UserPermissionPG pg) {
		this.pg = pg;
	}

	public UserPermissionBBPS getBbps() {
		return bbps;
	}

	public void setBbps(UserPermissionBBPS bbps) {
		this.bbps = bbps;
	}

	public UserPermissionAdministrator getAdministrator() {
		return administrator;
	}

	public void setAdministrator(UserPermissionAdministrator administrator) {
		this.administrator = administrator;
	}

}
