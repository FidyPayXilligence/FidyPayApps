package com.fidypay.service;

import java.util.Map;

import javax.validation.Valid;

import com.fidypay.request.FetchPassportRequest;
import com.fidypay.request.VerifyPassportRequest;
import com.fidypay.request.VerifyPassportRequestV2;

public interface PassportService {

		
	//----------------------------------------------
	Map<String, Object> saveDataForPassportFetch(@Valid FetchPassportRequest fetchPassportRequest, long merchantId,
			Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> saveDataForPassportFetchV2(@Valid FetchPassportRequest fetchPassportRequest, long merchantId,
			Double merchantFloatAmount, String serviceName, String businessName, String email);

	Map<String, Object> saveDataForVerifyPassport(VerifyPassportRequest verifyPassportRequest, long merchantId,
			Double merchantFloatAmount, String serviceName, String businessName, String email);

	Map<String, Object> saveDataForVerifyPassportV2(VerifyPassportRequestV2 verifyPassportRequestV2, long merchantId,
			Double merchantFloatAmount, String serviceName, String businessName, String email);
}
