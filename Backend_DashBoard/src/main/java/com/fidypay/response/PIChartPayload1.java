package com.fidypay.response;

import java.math.BigInteger;

public class PIChartPayload1 {

	private String serviceName;
	private BigInteger transactions;
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public BigInteger getTransactions() {
		return transactions;
	}
	public void setTransactions(BigInteger transactions) {
		this.transactions = transactions;
	}
	
	
	
}
