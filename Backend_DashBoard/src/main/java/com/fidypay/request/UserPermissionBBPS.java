package com.fidypay.request;

import javax.validation.Valid;

public class UserPermissionBBPS {

	private UserPermissionType transactionReport;

	private UserPermissionType transactionStatus;

	private UserPermissionType settlementReport;

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

	public UserPermissionType getTransactionReport() {
		return transactionReport;
	}

	public void setTransactionReport(UserPermissionType transactionReport) {
		this.transactionReport = transactionReport;
	}

	public UserPermissionType getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(UserPermissionType transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public UserPermissionType getSettlementReport() {
		return settlementReport;
	}

	public void setSettlementReport(UserPermissionType settlementReport) {
		this.settlementReport = settlementReport;
	}

}
