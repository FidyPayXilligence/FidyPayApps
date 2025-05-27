package com.fidypay.service;

import java.util.Map;

import com.fidypay.request.AccountVerificationRequest;
import com.fidypay.request.NSDLRequest;

public interface BankAccountVerificationService {

	Map<String, Object> bankAccountVerificationRequest(AccountVerificationRequest accountVerificationRequest,
			long merchantId, double merchantWallet, String bussinessName, String email);

	public boolean checkServiceExistOrNot(long merchantId, String serviceName);

	Map<String, Object> accountVerificationPennyless(AccountVerificationRequest accountVerificationRequest,
			long merchantId, double merchantWallet, String bussinessName, String email);

	Map<String, Object> accountVerificationPennyDrop(NSDLRequest nsdlRequest, long merchantId,
			double merchantFloatAmount, String bussinessName, String email);
	
	Map<String, Object> bankAccountVerificationPennyDrop(NSDLRequest nsdlRequest,
			long merchantId, double merchantWallet, String bussinessName, String email);
	
	Map<String, Object> bankAccountVerificationPennyDropUat(NSDLRequest nsdlRequest, long merchantId,
			double merchantWallet, String bussinessName, String email);

			Map<String, Object> accountVerificationPennylessUat(AccountVerificationRequest accountVerificationRequest,
			long merchantId, double merchantWallet, String bussinessName, String email);

}
