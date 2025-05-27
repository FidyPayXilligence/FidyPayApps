package com.fidypay.service;

import java.util.Map;

public interface GSTService {

	String GSTINSearch(String GSTIN) throws Exception;

	String GSTINDetailsSearch(String GSTIN) throws Exception;

	String GSTSearchCompanyName(String companyName) throws Exception;

	String login(String username, String password) throws Exception;

	// --------------------------------

	Map<String, Object> saveDataForGSTINSearch(String gSTIN, long merchantId, Double merchantFloatAmount, String businessName, String email);
	
	Map<String, Object> saveDataForValidateGSTINSearch(String gSTIN, long merchantId, Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> saveDataForGSTDetailSearch(String gSTIN, long merchantId, Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> saveDataForGSTSearchCompanyName(String companyName, long merchantId,
			Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> saveDataForGSTINSearchKarza(String gSTIN, long merchantId, Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> gstinAuthentication(String gSTIN, long merchantId, Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> saveDataForGSTINSearchByPan(String pan, long merchantId, Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> GSTSearchBasisPAN(String panNo, long merchantId, Double merchantFloatAmount, String businessName, String email);

}
