package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fidypay.entity.PayinRequest;
import com.fidypay.repo.PayinRequestRepository;
import com.fidypay.service.PayinRequestService;
import com.fidypay.utils.ex.DateAndTime;
@Service
public class PayinRequestServiceImpl implements PayinRequestService {

	private PayinRequestRepository payinRequestRepository;

	public PayinRequestServiceImpl(PayinRequestRepository payinRequestRepository) {
		this.payinRequestRepository = payinRequestRepository;
	}

	@Override
	public PayinRequest save(long merchantId, String userRequest, String bankRequest, String merchantTrxnRefId,
			double amount, String vpa, String submerchantVPA,String pgMerchantId ,String api,Timestamp trxnDate) throws ParseException {

		//Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());


		PayinRequest payinRequest = new PayinRequest();
		payinRequest.setRequestDate(trxnDate);
		payinRequest.setBankRequest(bankRequest);
		payinRequest.setUserRequest(userRequest);
		payinRequest.setMerchantId(merchantId);
		payinRequest.setAmount(amount);
		payinRequest.setMerchantTransactionRefId(merchantTrxnRefId);
		payinRequest.setPayeeVPA(submerchantVPA);
		payinRequest.setPayerVPA(vpa);
		payinRequest.setApi(api);
		payinRequest.setPgMerchantId(pgMerchantId);

		return payinRequestRepository.save(payinRequest);
	}
	
	@Override
	public boolean existsByMerchantTransactionRefId(String merchantTransactionRefId) {
		return payinRequestRepository.existsByMerchantTransactionRefId(merchantTransactionRefId);
	}

	@Override
	public List findByTrxnId(String trxnId) {
		return payinRequestRepository.findByTrxnId(trxnId);
	}


	@Override
	public boolean existsByBankRequest(String bankRequest) {
	return payinRequestRepository.existsByBankRequest(bankRequest);
	}
	

	
	

}
