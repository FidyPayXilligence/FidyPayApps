package com.fidypay.service;


public interface CoreTempTransactionsService {
	
	
	 boolean existsByRetryCount(String retryCount)throws Exception;


}
