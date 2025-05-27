package com.fidypay.request;

public class UserPermissionAdministrator {

	private UserPermissionType virtualAccount;

	private UserPermissionType authentication;

	private UserPermissionType services;

	public UserPermissionType getVirtualAccount() {
		return virtualAccount;
	}

	public void setVirtualAccount(UserPermissionType virtualAccount) {
		this.virtualAccount = virtualAccount;
	}

	public UserPermissionType getAuthentication() {
		return authentication;
	}

	public void setAuthentication(UserPermissionType authentication) {
		this.authentication = authentication;
	}

	public UserPermissionType getServices() {
		return services;
	}

	public void setServices(UserPermissionType services) {
		this.services = services;
	}

}
