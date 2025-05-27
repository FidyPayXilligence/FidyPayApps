package com.fidypay.service;

import java.util.Map;

import com.fidypay.request.Validate;
import com.fidypay.request.ValidateOtp;

public interface AadhaarService {

	Map<String, Object> saveDataForVerify(String accountNumber, long merchantId, double merchantWallet, String bussinessName, String email);

	public Map<String, Object> saveDataForBasicVerify(String accountNumber, long merchantId, double merchantWallet, String bussinessName, String email);

	Map<String, Object> saveDataForGetURL(long merchantId, double merchantWallet,String url, String bussinessName, String email);

	Map<String, Object> eAdhar(String requestId, long merchantId, double merchantWallet, String bussinessName, String email);

	Map<String, Object> getDetailsEAdhar(String requestId, long merchantId, double merchantWallet, String bussinessName, String email);

	Map<String, Object> saveDataForGenerateOtp(String aadhaarNumber, long merchantId, double merchantWallet,
			String serviceName, String bussinessName, String email);

	Map<String, Object> saveDataForAadharValidate(ValidateOtp validateotp, long merchantId, double merchantWallet, String bussinessName, String email);

	Map<String, Object> saveDataForValidate(Validate validate, long merchantId, double merchantWallet, String bussinessName, String email);

	// ELK

//	Iterable<EkycTransactionDetails> migrateDataByDateRange(ElasticMigrationRequest elasticMigrationRequest);
//	
//	Iterable<EkycTransactionDetails> findAllEkycTxnFromElasticsearch();
//
//	void doIndex(int pageNo, int pageSize, String fromDate, String toDate);
}
