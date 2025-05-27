package com.fidypay.response;

import com.fidypay.request.UserPermissionAdministrator;
import com.fidypay.request.UserPermissionBBPS;
import com.fidypay.request.UserPermissionEkyc;
import com.fidypay.request.UserPermissionEnach;
import com.fidypay.request.UserPermissionPG;
import com.fidypay.request.UserPermissionPayin;
import com.fidypay.request.UserPermissionPayout;

public class MerchantPermissionPayload {

	private UserPermissionPayout payout;
	private UserPermissionPayin payin;
	private UserPermissionEkyc eKyc;
	private UserPermissionEnach eNach;
	private UserPermissionPG pg;
	private UserPermissionBBPS bbps;
	private UserPermissionAdministrator administrator;

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
