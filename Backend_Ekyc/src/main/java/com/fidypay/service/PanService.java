package com.fidypay.service;

import java.util.Map;

import javax.validation.Valid;

import com.fidypay.request.PanAadharRequest;

public interface PanService {
	
	//--------------------------------------------------
	Map<String, Object> saveDataForFetchPan(@Valid String panNumber, long merchantId, Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> saveDataForFetchPanDetails(@Valid String panNumber, long merchantId,
			Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> saveDataForFetchPanV3Details(@Valid String panNumber, long merchantId,
			Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> saveDataForPanCompliance(@Valid String panNumber, long merchantId, Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> saveDataForBasicPanCompliance(@Valid String panNumber, long merchantId,
			Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> saveDataForPanAdhar(PanAadharRequest panAadharRequest, long merchantId,
			Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> checkPanAadharLinkStatus(@Valid String panNumber, long merchantId, Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> checkPanStatus(@Valid String panNumber, long merchantId, Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> panProfileDetails(@Valid String panNumber, long merchantId, Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> validatePan(@Valid String panNumber, long merchantId, Double merchantFloatAmount, String businessName, String email);
	
	Map<String, Object> panDetails(@Valid String panNumber, long merchantId, Double merchantFloatAmount, String businessName, String email);
}
