package com.fidypay.request;

import javax.validation.Valid;

public class UserPermissionPayin {

	private UserPermissionType addSubmerchant;

	private UserPermissionType submerchantList;

	private UserPermissionType transactionReport;

	private UserPermissionType transactionStatus;

	private UserPermissionType settlementReport;

	private UserPermissionType commercials;

	public UserPermissionType getCommercials() {
		return commercials;
	}

	public void setCommercials(UserPermissionType commercials) {
		this.commercials = commercials;
	}

	public UserPermissionType getAddSubmerchant() {
		return addSubmerchant;
	}

	public void setAddSubmerchant(UserPermissionType addSubmerchant) {
		this.addSubmerchant = addSubmerchant;
	}

	public UserPermissionType getSubmerchantList() {
		return submerchantList;
	}

	public void setSubmerchantList(UserPermissionType submerchantList) {
		this.submerchantList = submerchantList;
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
