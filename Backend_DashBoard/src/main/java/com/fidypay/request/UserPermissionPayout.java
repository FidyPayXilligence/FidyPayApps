package com.fidypay.request;

import javax.validation.Valid;

public class UserPermissionPayout {

	@Valid
	private UserPermissionType balance;

	@Valid
	private UserPermissionType statement;

	@Valid
	private UserPermissionType bankAccount;

	@Valid
	private UserPermissionType beneficiary;

	@Valid
	private UserPermissionType transactionReport;

	@Valid
	private UserPermissionType transactionStatus;

	@Valid
	private UserPermissionType payment;

	@Valid
	private UserPermissionType commercials;

	public UserPermissionType getCommercials() {
		return commercials;
	}

	public void setCommercials(UserPermissionType commercials) {
		this.commercials = commercials;
	}

	public UserPermissionType getBalance() {
		return balance;
	}

	public void setBalance(UserPermissionType balance) {
		this.balance = balance;
	}

	public UserPermissionType getStatement() {
		return statement;
	}

	public void setStatement(UserPermissionType statement) {
		this.statement = statement;
	}

	public UserPermissionType getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(UserPermissionType bankAccount) {
		this.bankAccount = bankAccount;
	}

	public UserPermissionType getBeneficiary() {
		return beneficiary;
	}

	public void setBeneficiary(UserPermissionType beneficiary) {
		this.beneficiary = beneficiary;
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

	public UserPermissionType getPayment() {
		return payment;
	}

	public void setPayment(UserPermissionType payment) {
		this.payment = payment;
	}

}
