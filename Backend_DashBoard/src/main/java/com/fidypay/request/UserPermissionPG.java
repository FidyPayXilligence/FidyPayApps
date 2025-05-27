package com.fidypay.request;

import javax.validation.Valid;

public class UserPermissionPG {

	private UserPermissionType transactionReport;

	private UserPermissionType transactionStatus;

	private UserPermissionType settlementReport;

	private UserPermissionType transactionRefund;

	private UserPermissionType commercials;

	private UserPermissionType paymentLink;

	public UserPermissionType getPaymentLink() {
		return paymentLink;
	}

	public void setPaymentLink(UserPermissionType paymentLink) {
		this.paymentLink = paymentLink;
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

	public UserPermissionType getTransactionRefund() {
		return transactionRefund;
	}

	public void setTransactionRefund(UserPermissionType transactionRefund) {
		this.transactionRefund = transactionRefund;
	}

}
