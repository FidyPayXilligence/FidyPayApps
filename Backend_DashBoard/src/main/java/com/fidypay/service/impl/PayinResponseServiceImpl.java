package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.entity.PayinResponse;
import com.fidypay.repo.PayinResponseRepository;
import com.fidypay.service.PayinResponseService;
@Service
public class PayinResponseServiceImpl implements PayinResponseService {

	@Autowired
	private PayinResponseRepository payinResponseRepository;

	@Override
	public PayinResponse save(String bankResponse, String userResponse, String merchantTrxnRefId, String pgMerchantId,
			double amount, String utr, String status, String api, long merchantId,Timestamp trxnDate) throws ParseException {

		//Date date = new Date();

		PayinResponse payinResponse = new PayinResponse();
		payinResponse.setResponseDate(trxnDate);
		payinResponse.setAmount(amount);
		payinResponse.setApi(api);
		payinResponse.setBankResponse(bankResponse);
		payinResponse.setMerchantId(merchantId);
		payinResponse.setMerchantTransactionRefId(merchantTrxnRefId);
		payinResponse.setPgMerchantId(pgMerchantId);
		payinResponse.setStatus(status);
		payinResponse.setUserResponse(userResponse);
		payinResponse.setUTR(utr);

		return payinResponseRepository.save(payinResponse);
	}

}
