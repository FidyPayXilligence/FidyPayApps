package com.fidypay.service;

import java.sql.Timestamp;
import java.text.ParseException;

import com.fidypay.entity.PayinResponse;

public interface PayinResponseService {

	PayinResponse save(String bankResponse, String userResponse, String merchantTrxnRefId, String pgMerchantId,double amount, String utr,
			String status, String api,long merchantId,Timestamp trxnDate)throws ParseException;

}
