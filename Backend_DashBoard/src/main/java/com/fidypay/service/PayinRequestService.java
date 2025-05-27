package com.fidypay.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

import com.fidypay.entity.PayinRequest;

public interface PayinRequestService {

	 PayinRequest save(long merchantId, String userRequest, String bankRequest, String merchantTrxnRefId,
			double amount, String vpa, String submerchantVPA,String pgMerchantId ,String api,Timestamp trxnDate)throws ParseException;
	
	boolean existsByMerchantTransactionRefId(String merchantTransactionRefId);

	List findByTrxnId(String trxnId);
	boolean existsByBankRequest(String bankRequest);

	

}
