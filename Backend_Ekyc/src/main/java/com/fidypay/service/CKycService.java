package com.fidypay.service;

import java.util.Map;

import com.fidypay.request.CKycRequest;

public interface CKycService {

	Map<String, Object> saveDataCKYCSearch(String accountNumber, long merchantId, double merchantWallet,
			String businessName, String email);

	Map<String, Object> saveDataCKYCDetails(CKycRequest cKycRequest, long merchantId, double merchantWallet,
			String businessName, String email);

	Map<String, Object> saveDataCKYCSearchV2(String panNumber, long merchantId, Double merchantFloatAmount,
			String merchantBusinessName, String merchantEmail);

}
