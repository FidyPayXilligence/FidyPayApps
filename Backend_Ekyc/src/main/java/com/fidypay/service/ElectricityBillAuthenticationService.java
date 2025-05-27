package com.fidypay.service;

import java.util.Map;

import com.fidypay.request.ElectricityBillRequest;

public interface ElectricityBillAuthenticationService {

	Map<String, Object> electricityBillAuthentication(ElectricityBillRequest electricityBillRequest, Long merchantId,
			double merchantFloatAmount, String businessName, String email);

	Map<String, Object> electricityServiceProvidersList();

	Map<String, Object> epfUANValidation( String uanNumber, long merchantId, Double merchantFloatAmount, String businessName, String email);

}
