package com.fidypay.request;

import javax.validation.Valid;

public class UserPermissionEnach {

	private UserPermissionType singleMandate;

	private UserPermissionType bulkMandate;

	private UserPermissionType singleDebit;

	private UserPermissionType bulkDebit;

	private UserPermissionType mandateStatus;

	private UserPermissionType debitCancel;

	private UserPermissionType debitPause;

	private UserPermissionType debitResume;

	private UserPermissionType bulkMandateReport;

	private UserPermissionType bulkDebitReport;

	private UserPermissionType transactionReport;

	private UserPermissionType settlementReport;

	private UserPermissionType commercials;

	public UserPermissionType getCommercials() {
		return commercials;
	}

	public void setCommercials(UserPermissionType commercials) {
		this.commercials = commercials;
	}

	public UserPermissionType getSingleMandate() {
		return singleMandate;
	}

	public void setSingleMandate(UserPermissionType singleMandate) {
		this.singleMandate = singleMandate;
	}

	public UserPermissionType getBulkMandate() {
		return bulkMandate;
	}

	public void setBulkMandate(UserPermissionType bulkMandate) {
		this.bulkMandate = bulkMandate;
	}

	public UserPermissionType getSingleDebit() {
		return singleDebit;
	}

	public void setSingleDebit(UserPermissionType singleDebit) {
		this.singleDebit = singleDebit;
	}

	public UserPermissionType getBulkDebit() {
		return bulkDebit;
	}

	public void setBulkDebit(UserPermissionType bulkDebit) {
		this.bulkDebit = bulkDebit;
	}

	public UserPermissionType getMandateStatus() {
		return mandateStatus;
	}

	public void setMandateStatus(UserPermissionType mandateStatus) {
		this.mandateStatus = mandateStatus;
	}

	public UserPermissionType getDebitCancel() {
		return debitCancel;
	}

	public void setDebitCancel(UserPermissionType debitCancel) {
		this.debitCancel = debitCancel;
	}

	public UserPermissionType getDebitPause() {
		return debitPause;
	}

	public void setDebitPause(UserPermissionType debitPause) {
		this.debitPause = debitPause;
	}

	public UserPermissionType getDebitResume() {
		return debitResume;
	}

	public void setDebitResume(UserPermissionType debitResume) {
		this.debitResume = debitResume;
	}

	public UserPermissionType getBulkMandateReport() {
		return bulkMandateReport;
	}

	public void setBulkMandateReport(UserPermissionType bulkMandateReport) {
		this.bulkMandateReport = bulkMandateReport;
	}

	public UserPermissionType getBulkDebitReport() {
		return bulkDebitReport;
	}

	public void setBulkDebitReport(UserPermissionType bulkDebitReport) {
		this.bulkDebitReport = bulkDebitReport;
	}

	public UserPermissionType getTransactionReport() {
		return transactionReport;
	}

	public void setTransactionReport(UserPermissionType transactionReport) {
		this.transactionReport = transactionReport;
	}

	public UserPermissionType getSettlementReport() {
		return settlementReport;
	}

	public void setSettlementReport(UserPermissionType settlementReport) {
		this.settlementReport = settlementReport;
	}

}
