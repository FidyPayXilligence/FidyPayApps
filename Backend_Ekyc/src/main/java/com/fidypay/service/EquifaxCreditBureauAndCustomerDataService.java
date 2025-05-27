package com.fidypay.service;

import java.util.Map;

import com.fidypay.request.CreditBureauReportRequest;
import com.fidypay.request.CustomerDataRequest;

public interface EquifaxCreditBureauAndCustomerDataService {

	Map<String, Object> saveDataForCreditBureauReport(CreditBureauReportRequest creditBureauReportRequest,
			long merchantId, Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> saveDataForFetchCustomerDetails(CustomerDataRequest customerDataRequest, long merchantId,
			Double merchantFloatAmount, String businessName, String email);
	
	Map<String, Object> saveDataForFetchCreditScore(CustomerDataRequest customerDataRequest, long merchantId,
			Double merchantFloatAmount, String businessName, String email);

}
