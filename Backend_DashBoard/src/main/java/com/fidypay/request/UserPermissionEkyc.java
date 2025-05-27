package com.fidypay.request;

import javax.validation.Valid;

public class UserPermissionEkyc {

	private UserPermissionType tansactionReport;

	private UserPermissionType walletBalance;

	private UserPermissionType createWorkflow;

	private UserPermissionType assignUser;

	private UserPermissionType commercials;

	private UserPermissionType passBook;

	public UserPermissionType getPassBook() {
		return passBook;
	}

	public void setPassBook(UserPermissionType passBook) {
		this.passBook = passBook;
	}

	public UserPermissionType getCommercials() {
		return commercials;
	}

	public void setCommercials(UserPermissionType commercials) {
		this.commercials = commercials;
	}

	public UserPermissionType getTansactionReport() {
		return tansactionReport;
	}

	public void setTansactionReport(UserPermissionType tansactionReport) {
		this.tansactionReport = tansactionReport;
	}

	public UserPermissionType getWalletBalance() {
		return walletBalance;
	}

	public void setWalletBalance(UserPermissionType walletBalance) {
		this.walletBalance = walletBalance;
	}

	public UserPermissionType getCreateWorkflow() {
		return createWorkflow;
	}

	public void setCreateWorkflow(UserPermissionType createWorkflow) {
		this.createWorkflow = createWorkflow;
	}

	public UserPermissionType getAssignUser() {
		return assignUser;
	}

	public void setAssignUser(UserPermissionType assignUser) {
		this.assignUser = assignUser;
	}

}
