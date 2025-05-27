package com.fidypay.response;

public class CheckUPILimitResponse {

	private String currentBalance;
	private String withdrawableBalance;
	private String monthlyLimitUtilized;
	private String overallMonthlyCollectionLimit;

	public String getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(String currentBalance) {
		this.currentBalance = currentBalance;
	}

	public String getWithdrawableBalance() {
		return withdrawableBalance;
	}

	public void setWithdrawableBalance(String withdrawableBalance) {
		this.withdrawableBalance = withdrawableBalance;
	}

	public String getMonthlyLimitUtilized() {
		return monthlyLimitUtilized;
	}

	public void setMonthlyLimitUtilized(String monthlyLimitUtilized) {
		this.monthlyLimitUtilized = monthlyLimitUtilized;
	}

	public String getOverallMonthlyCollectionLimit() {
		return overallMonthlyCollectionLimit;
	}

	public void setOverallMonthlyCollectionLimit(String overallMonthlyCollectionLimit) {
		this.overallMonthlyCollectionLimit = overallMonthlyCollectionLimit;
	}

}
